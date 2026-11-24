package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = AuthorEntity::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["authorId"])]
)
data class BookEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val publishYear: String,
    val pagesCount: Int,
    val synopsis: String,
    val coverColorHex: Long,
    val spineAccentHex: Long,
    val heightFactor: Float,
    val thicknessDp: Int,
    val contentPages: String,
    val isBookmarked: Boolean = false,
    val currentPage: Int = 0,
    val localUri: String? = null,
    val mimeType: String = MIME_BUILTIN,
    val pdfPageCount: Int = 0
) {
    val isLocalFile: Boolean
        get() = !localUri.isNullOrBlank()

    val isPdf: Boolean
        get() = mimeType.equals(MIME_PDF, ignoreCase = true)

    fun splitPages(): List<String> =
        contentPages.split(PAGE_DELIMITER).map { it.trim() }.filter { it.isNotEmpty() }

    val totalReadablePages: Int
        get() = if (isPdf) {
            pdfPageCount.coerceAtLeast(1)
        } else {
            splitPages().size.coerceAtLeast(1)
        }

    val readingProgress: Float
        get() = if (currentPage <= 0) 0f else ((currentPage + 1).toFloat() / totalReadablePages.toFloat()).coerceIn(0f, 1f)

    companion object {
        const val PAGE_DELIMITER = "\n===PAGE===\n"
        const val MIME_BUILTIN = "builtin"
        const val MIME_PDF = "application/pdf"
        const val MIME_TEXT = "text/plain"
    }
}
