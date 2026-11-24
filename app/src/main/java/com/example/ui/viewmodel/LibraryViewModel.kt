package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LibraryRepository
import com.example.data.local.LibraryDatabase
import com.example.data.local.LocalFileImporter
import com.example.data.local.ReaderPreferences
import com.example.data.local.ReaderPreferencesDataStore
import com.example.data.local.ReaderThemeMode
import com.example.data.model.AuthorEntity
import com.example.data.model.BookEntity
import com.example.data.seed.LibrarySectionDefaults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthorShelfUiModel(
    val author: AuthorEntity,
    val books: List<BookEntity>
)

data class LibraryUiState(
    val isLoading: Boolean = true,
    val authors: List<AuthorEntity> = emptyList(),
    val shelves: List<AuthorShelfUiModel> = emptyList(),
    val selectedAuthorId: String? = null,
    val searchQuery: String = "",
    val bookmarkedBooks: List<BookEntity> = emptyList(),
    val readingInProgressBooks: List<BookEntity> = emptyList(),
    val readerPreferences: ReaderPreferences = ReaderPreferences(),
    val totalBooksCount: Int = 0
)

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LibraryRepository

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _importFeedback = MutableStateFlow<String?>(null)
    val importFeedback: StateFlow<String?> = _importFeedback.asStateFlow()

    init {
        val db = LibraryDatabase.getInstance(application)
        val prefs = ReaderPreferencesDataStore(application)
        val importer = LocalFileImporter(application)
        repository = LibraryRepository(db.libraryDao(), prefs, importer)
        viewModelScope.launch {
            val syncResult = repository.syncLinkedFolderIfConfigured()
            if (syncResult != null && syncResult.addedBooks.isNotEmpty()) {
                _importFeedback.value = "تمت إضافة ${syncResult.addedBooks.size} ملف PDF تلقائياً من المجلد المرتبط"
            }
        }
    }

    private val searchQuery = MutableStateFlow("")
    private val selectedAuthorId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<LibraryUiState> = combine(
        repository.authors,
        repository.books,
        repository.bookmarkedBooks,
        repository.readingInProgressBooks,
        repository.readerPreferences,
        searchQuery,
        selectedAuthorId
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val allAuthors = values[0] as List<AuthorEntity>
        @Suppress("UNCHECKED_CAST")
        val books = values[1] as List<BookEntity>
        @Suppress("UNCHECKED_CAST")
        val bookmarked = values[2] as List<BookEntity>
        @Suppress("UNCHECKED_CAST")
        val inProgress = values[3] as List<BookEntity>
        val prefs = values[4] as ReaderPreferences
        val query = (values[5] as String).trim()
        val activeAuthorId = values[6] as String?

        val visibleAuthors = allAuthors.filter { author ->
            books.any { it.authorId == author.id }
        }

        val effectiveAuthorId = activeAuthorId?.takeIf { id ->
            visibleAuthors.any { it.id == id }
        }

        val filteredAuthors = if (effectiveAuthorId == null) {
            visibleAuthors
        } else {
            visibleAuthors.filter { it.id == effectiveAuthorId }
        }

        val shelves = filteredAuthors.mapNotNull { author ->
            val authorBooks = books.filter { book ->
                book.authorId == author.id && (
                    query.isEmpty() ||
                        book.title.contains(query, ignoreCase = true) ||
                        book.subtitle.contains(query, ignoreCase = true) ||
                        book.authorName.contains(query, ignoreCase = true) ||
                        book.category.contains(query, ignoreCase = true)
                    )
            }
            if (authorBooks.isNotEmpty() || (query.isNotEmpty() && author.name.contains(query, ignoreCase = true))) {
                val finalBooks = if (authorBooks.isEmpty()) books.filter { it.authorId == author.id } else authorBooks
                AuthorShelfUiModel(author = author, books = finalBooks)
            } else {
                null
            }
        }

        LibraryUiState(
            isLoading = false,
            authors = visibleAuthors,
            shelves = shelves,
            selectedAuthorId = effectiveAuthorId,
            searchQuery = query,
            bookmarkedBooks = bookmarked,
            readingInProgressBooks = inProgress,
            readerPreferences = prefs,
            totalBooksCount = books.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState()
    )

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun selectAuthorFilter(authorId: String?) {
        selectedAuthorId.value = if (selectedAuthorId.value == authorId) null else authorId
    }

    fun importPickedFiles(
        uris: List<Uri>,
        authorName: String = LibrarySectionDefaults.DEFAULT_PDF_AUTHOR_NAME
    ) {
        if (uris.isEmpty()) return
        val cleanAuthor = authorName.trim().ifEmpty { LibrarySectionDefaults.DEFAULT_PDF_AUTHOR_NAME }
        viewModelScope.launch {
            _isImporting.value = true
            val result = repository.importPickedFiles(uris, cleanAuthor)
            _isImporting.value = false
            _importFeedback.value = when {
                result.addedBooks.isNotEmpty() ->
                    "تمت إضافة ${result.addedBooks.size} كتاب إلى قسم $cleanAuthor"
                result.skippedDuplicates > 0 ->
                    "الملفات المختارة مضافة مسبقاً في مكتبتك"
                else ->
                    "لم يتم العثور على ملفات صالحة للإضافة"
            }
        }
    }

    fun linkAndSyncFolder(treeUri: Uri) {
        viewModelScope.launch {
            _isImporting.value = true
            val result = repository.linkAndSyncFolder(treeUri)
            _isImporting.value = false
            _importFeedback.value = when {
                result.addedBooks.isNotEmpty() ->
                    "تم ربط المجلد وإضافة ${result.addedBooks.size} ملف PDF تلقائياً"
                result.skippedDuplicates > 0 ->
                    "تم ربط المجلد (جميع ملفات PDF مضافة مسبقاً)"
                else ->
                    "تم ربط المجلد، وسيتم استيراد أي ملف PDF يوضع بداخله تلقائياً"
            }
        }
    }

    fun syncLinkedFolderNow() {
        viewModelScope.launch {
            _isImporting.value = true
            val result = repository.syncLinkedFolderIfConfigured()
            _isImporting.value = false
            if (result != null) {
                _importFeedback.value = if (result.addedBooks.isNotEmpty()) {
                    "تمت مزامنة وإضافة ${result.addedBooks.size} ملف جديد"
                } else {
                    "المجلد محدّث بالفعل ولا توجد ملفات جديدة"
                }
            }
        }
    }

    fun unlinkPdfFolder() {
        viewModelScope.launch {
            repository.unlinkPdfFolder()
            _importFeedback.value = "تم إلغاء ربط المجلد التلقائي"
        }
    }

    fun clearImportFeedback() {
        _importFeedback.value = null
    }

    suspend fun renderPdfPage(uriString: String, pageIndex: Int, targetWidthPx: Int): Bitmap? {
        return repository.renderPdfPage(uriString, pageIndex, targetWidthPx)
    }

    fun updateBookTitleAndAuthor(book: BookEntity, newTitle: String, newAuthorName: String) {
        viewModelScope.launch {
            repository.updateBookTitleAndAuthor(book, newTitle, newAuthorName)
            _importFeedback.value = "تم تحديث بيانات الكتاب ونقله إلى قسم ${newAuthorName.trim().ifEmpty { book.authorName }}"
        }
    }

    fun removeBook(book: BookEntity) {
        viewModelScope.launch {
            repository.removeBook(book)
            _importFeedback.value = "تمت إزالة الكتاب من الرف"
        }
    }

    fun observeAuthor(authorId: String): Flow<AuthorEntity?> = repository.getAuthorById(authorId)

    fun observeBooksForAuthor(authorId: String): Flow<List<BookEntity>> = repository.getBooksByAuthor(authorId)

    fun observeBook(bookId: String): Flow<BookEntity?> = repository.getBookById(bookId)

    fun toggleBookmark(book: BookEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(book)
        }
    }

    fun updateCurrentPage(bookId: String, page: Int) {
        viewModelScope.launch {
            repository.updateCurrentPage(bookId, page)
        }
    }

    fun setReaderFontSize(sizeSp: Float) {
        viewModelScope.launch {
            repository.setReaderFontSize(sizeSp)
        }
    }

    fun setReaderThemeMode(mode: ReaderThemeMode) {
        viewModelScope.launch {
            repository.setReaderThemeMode(mode)
        }
    }

    fun toggleShelfMode() {
        viewModelScope.launch {
            val current = uiState.value.readerPreferences.isRealisticShelfMode
            repository.setRealisticShelfMode(!current)
        }
    }
}
