package com.example.data

import android.graphics.Bitmap
import android.net.Uri
import com.example.data.local.LibraryDao
import com.example.data.local.LocalFileImporter
import com.example.data.local.LocalImportResult
import com.example.data.local.ReaderPreferences
import com.example.data.local.ReaderPreferencesDataStore
import com.example.data.local.ReaderThemeMode
import com.example.data.model.AuthorEntity
import com.example.data.model.BookEntity
import com.example.data.seed.LibrarySectionDefaults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class LibraryRepository(
    private val dao: LibraryDao,
    private val prefsDataStore: ReaderPreferencesDataStore,
    private val fileImporter: LocalFileImporter
) {
    private val syncMutex = Mutex()

    val authors: Flow<List<AuthorEntity>> = dao.getAllAuthors()
    val books: Flow<List<BookEntity>> = dao.getAllBooks()
    val bookmarkedBooks: Flow<List<BookEntity>> = dao.getBookmarkedBooks()
    val readingInProgressBooks: Flow<List<BookEntity>> = dao.getReadingInProgressBooks()
    val readerPreferences: Flow<ReaderPreferences> = prefsDataStore.preferences

    fun getAuthorById(authorId: String): Flow<AuthorEntity?> = dao.getAuthorById(authorId)
    fun getBooksByAuthor(authorId: String): Flow<List<BookEntity>> = dao.getBooksByAuthor(authorId)
    fun getBookById(bookId: String): Flow<BookEntity?> = dao.getBookById(bookId)

    private suspend fun getOrCreateAuthorByName(rawName: String): AuthorEntity {
        val cleanName = rawName.trim().ifEmpty { LibrarySectionDefaults.DEFAULT_PDF_AUTHOR_NAME }
        val existing = dao.findAuthorByName(cleanName)
        if (existing != null) return existing
        val order = dao.countAuthors()
        val created = LibrarySectionDefaults.createAuthor(cleanName, order)
        dao.insertAuthor(created)
        return created
    }

    suspend fun importPickedFiles(
        uris: List<Uri>,
        authorName: String
    ): LocalImportResult = syncMutex.withLock {
        val targetAuthor = getOrCreateAuthorByName(authorName)
        val existing = dao.getExistingLocalUris().toSet()
        val result = fileImporter.importUris(
            uris = uris,
            existingLocalUris = existing,
            targetAuthor = targetAuthor
        )
        if (result.addedBooks.isNotEmpty()) {
            dao.insertBooks(result.addedBooks)
        }
        result
    }

    suspend fun linkAndSyncFolder(treeUri: Uri): LocalImportResult = syncMutex.withLock {
        val folderName = fileImporter.resolveTreeFolderDisplayName(treeUri)
        prefsDataStore.setLinkedPdfFolder(treeUri.toString(), folderName)
        val existing = dao.getExistingLocalUris().toSet()
        val result = fileImporter.scanTreeForDocuments(
            treeUri = treeUri,
            existingLocalUris = existing,
            resolveAuthorForName = { name -> getOrCreateAuthorByName(name) }
        )
        if (result.addedBooks.isNotEmpty()) {
            dao.insertBooks(result.addedBooks)
        }
        result
    }

    suspend fun syncLinkedFolderIfConfigured(): LocalImportResult? = syncMutex.withLock {
        val prefs = prefsDataStore.preferences.first()
        val uriStr = prefs.linkedPdfFolderUri ?: return@withLock null
        val existing = dao.getExistingLocalUris().toSet()
        val result = fileImporter.scanTreeForDocuments(
            treeUri = Uri.parse(uriStr),
            existingLocalUris = existing,
            resolveAuthorForName = { name -> getOrCreateAuthorByName(name) }
        )
        if (result.addedBooks.isNotEmpty()) {
            dao.insertBooks(result.addedBooks)
        }
        result
    }

    suspend fun unlinkPdfFolder() {
        prefsDataStore.setLinkedPdfFolder(null, null)
    }

    suspend fun renderPdfPage(uriString: String, pageIndex: Int, targetWidthPx: Int): Bitmap? {
        return fileImporter.renderPdfPage(uriString, pageIndex, targetWidthPx)
    }

    suspend fun updateBookTitleAndAuthor(
        book: BookEntity,
        newTitle: String,
        newAuthorName: String
    ) {
        val cleanTitle = newTitle.trim().ifEmpty { book.title }
        val newAuthor = getOrCreateAuthorByName(newAuthorName)
        dao.updateBookMetadata(
            bookId = book.id,
            title = cleanTitle,
            authorName = newAuthor.name,
            authorId = newAuthor.id
        )
        if (book.authorId != newAuthor.id && dao.countBooksForAuthor(book.authorId) == 0) {
            dao.deleteAuthorById(book.authorId)
        }
    }

    suspend fun removeBook(book: BookEntity) {
        dao.deleteBookById(book.id)
        if (dao.countBooksForAuthor(book.authorId) == 0) {
            dao.deleteAuthorById(book.authorId)
        }
    }

    suspend fun toggleBookmark(book: BookEntity) {
        dao.updateBookmark(book.id, !book.isBookmarked)
    }

    suspend fun updateCurrentPage(bookId: String, page: Int) {
        dao.updateCurrentPage(bookId, page.coerceAtLeast(0))
    }

    suspend fun setReaderFontSize(sizeSp: Float) {
        prefsDataStore.setFontSize(sizeSp)
    }

    suspend fun setReaderThemeMode(mode: ReaderThemeMode) {
        prefsDataStore.setThemeMode(mode)
    }

    suspend fun setRealisticShelfMode(isRealistic: Boolean) {
        prefsDataStore.setShelfMode(isRealistic)
    }
}
