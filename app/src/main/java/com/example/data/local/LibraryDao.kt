package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AuthorEntity
import com.example.data.model.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    @Query("SELECT * FROM authors ORDER BY sectionOrder ASC, name ASC")
    fun getAllAuthors(): Flow<List<AuthorEntity>>

    @Query("SELECT * FROM authors WHERE id = :authorId LIMIT 1")
    fun getAuthorById(authorId: String): Flow<AuthorEntity?>

    @Query("SELECT * FROM authors WHERE name = :name LIMIT 1")
    suspend fun findAuthorByName(name: String): AuthorEntity?

    @Query("SELECT COUNT(*) FROM authors")
    suspend fun countAuthors(): Int

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE authorId = :authorId ORDER BY title ASC")
    fun getBooksByAuthor(authorId: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    fun getBookById(bookId: String): Flow<BookEntity?>

    @Query("SELECT COUNT(*) FROM books WHERE authorId = :authorId")
    suspend fun countBooksForAuthor(authorId: String): Int

    @Query("SELECT * FROM books WHERE isBookmarked = 1 ORDER BY title ASC")
    fun getBookmarkedBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE currentPage > 0 ORDER BY currentPage DESC")
    fun getReadingInProgressBooks(): Flow<List<BookEntity>>

    @Query("SELECT localUri FROM books WHERE localUri IS NOT NULL")
    suspend fun getExistingLocalUris(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAuthor(author: AuthorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("UPDATE books SET isBookmarked = :isBookmarked WHERE id = :bookId")
    suspend fun updateBookmark(bookId: String, isBookmarked: Boolean)

    @Query("UPDATE books SET currentPage = :page WHERE id = :bookId")
    suspend fun updateCurrentPage(bookId: String, page: Int)

    @Query("UPDATE books SET title = :title, authorName = :authorName, authorId = :authorId WHERE id = :bookId")
    suspend fun updateBookMetadata(bookId: String, title: String, authorName: String, authorId: String)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: String)

    @Query("DELETE FROM authors WHERE id = :authorId")
    suspend fun deleteAuthorById(authorId: String)
}
