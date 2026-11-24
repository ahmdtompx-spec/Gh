package com.example

import com.example.data.model.BookEntity
import com.example.data.seed.LibrarySectionDefaults
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `dynamic author section factory builds deterministic Arabic author section`() {
        val author = LibrarySectionDefaults.createAuthor("نجيب محفوظ", 0)
        assertEquals("نجيب محفوظ", author.name)
        assertEquals("ن", author.emblemSymbol)

        val defaultPdfAuthor = LibrarySectionDefaults.createAuthor("   ", 1)
        assertEquals(LibrarySectionDefaults.DEFAULT_PDF_AUTHOR_NAME, defaultPdfAuthor.name)
    }

    @Test
    fun `pdf and text books calculate readable pages accurately`() {
        val pdfBook = BookEntity(
            id = "pdf_1",
            authorId = "a_1",
            authorName = "ملفات PDF",
            title = "كتاب تجريبي",
            subtitle = "مستند PDF",
            category = "PDF",
            publishYear = "محلي",
            pagesCount = 42,
            synopsis = "",
            coverColorHex = 0xFF19423B,
            spineAccentHex = 0xFFE8C872,
            heightFactor = 1f,
            thicknessDp = 48,
            contentPages = "",
            localUri = "content://example/book.pdf",
            mimeType = BookEntity.MIME_PDF,
            pdfPageCount = 42,
            currentPage = 20
        )
        assertTrue(pdfBook.isPdf)
        assertTrue(pdfBook.isLocalFile)
        assertEquals(42, pdfBook.totalReadablePages)
        assertEquals(0.5f, pdfBook.readingProgress, 0.01f)
    }
}
