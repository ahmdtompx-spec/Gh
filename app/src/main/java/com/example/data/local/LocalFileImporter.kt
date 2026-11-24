package com.example.data.local

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import com.example.data.model.AuthorEntity
import com.example.data.model.BookEntity
import com.example.data.seed.LibrarySectionDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

data class DiscoveredTreeDoc(
    val uri: Uri,
    val folderAuthorName: String
)

data class LocalImportResult(
    val addedBooks: List<BookEntity>,
    val skippedDuplicates: Int = 0
)

class LocalFileImporter(private val context: Context) {

    private val coverPalette = listOf(
        0xFF19423B,
        0xFF6B222D,
        0xFF213C5B,
        0xFF7B4A1B,
        0xFF4D234A,
        0xFF1D4D54
    )

    suspend fun importUris(
        uris: List<Uri>,
        existingLocalUris: Set<String>,
        targetAuthor: AuthorEntity
    ): LocalImportResult = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val added = mutableListOf<BookEntity>()
        var skipped = 0

        uris.forEachIndexed { index, uri ->
            val uriStr = uri.toString()
            if (existingLocalUris.contains(uriStr)) {
                skipped++
                return@forEachIndexed
            }

            runCatching {
                resolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            val book = buildBookFromUri(
                resolver = resolver,
                uri = uri,
                targetAuthor = targetAuthor,
                colorIndex = existingLocalUris.size + index
            )
            if (book != null) {
                added.add(book)
            }
        }
        LocalImportResult(addedBooks = added, skippedDuplicates = skipped)
    }

    suspend fun scanTreeForDocuments(
        treeUri: Uri,
        existingLocalUris: Set<String>,
        resolveAuthorForName: suspend (String) -> AuthorEntity
    ): LocalImportResult = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        runCatching {
            resolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        val discovered = mutableListOf<DiscoveredTreeDoc>()
        val rootDocId = runCatching { DocumentsContract.getTreeDocumentId(treeUri) }.getOrNull()
        if (rootDocId != null) {
            collectDocumentsRecursively(
                resolver = resolver,
                treeUri = treeUri,
                parentDocId = rootDocId,
                currentSubfolderName = null,
                depth = 0,
                output = discovered
            )
        }

        val added = mutableListOf<BookEntity>()
        var skipped = 0
        discovered.forEachIndexed { index, doc ->
            val uriStr = doc.uri.toString()
            if (existingLocalUris.contains(uriStr)) {
                skipped++
                return@forEachIndexed
            }
            val targetAuthor = resolveAuthorForName(doc.folderAuthorName)
            val book = buildBookFromUri(
                resolver = resolver,
                uri = doc.uri,
                targetAuthor = targetAuthor,
                colorIndex = existingLocalUris.size + index
            )
            if (book != null) {
                added.add(book)
            }
        }

        LocalImportResult(addedBooks = added, skippedDuplicates = skipped)
    }

    fun resolveTreeFolderDisplayName(treeUri: Uri): String {
        val docId = runCatching { DocumentsContract.getTreeDocumentId(treeUri) }.getOrNull()
        if (!docId.isNullOrBlank()) {
            val cleaned = docId.substringAfterLast(':').substringAfterLast('/')
            if (cleaned.isNotBlank()) return cleaned
        }
        return "مجلد PDF"
    }

    suspend fun renderPdfPage(
        uriString: String,
        pageIndex: Int,
        targetWidthPx: Int
    ): Bitmap? = withContext(Dispatchers.IO) {
        runCatching {
            val uri = Uri.parse(uriString)
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    if (renderer.pageCount <= 0) return@withContext null
                    val safeIndex = pageIndex.coerceIn(0, renderer.pageCount - 1)
                    renderer.openPage(safeIndex).use { page ->
                        val width = targetWidthPx.coerceIn(480, 1800)
                        val ratio = page.height.toFloat() / page.width.toFloat().coerceAtLeast(1f)
                        val height = (width * ratio).toInt().coerceAtLeast(1)
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        bitmap.eraseColor(AndroidColor.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmap
                    }
                }
            }
        }.getOrNull()
    }

    private fun collectDocumentsRecursively(
        resolver: ContentResolver,
        treeUri: Uri,
        parentDocId: String,
        currentSubfolderName: String?,
        depth: Int,
        output: MutableList<DiscoveredTreeDoc>
    ) {
        if (depth > 3 || output.size >= 150) return
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, parentDocId)
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME
        )
        runCatching {
            resolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
                val idIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val mimeIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)
                val nameIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val docId = if (idIdx >= 0) cursor.getString(idIdx) else continue
                    val mime = if (mimeIdx >= 0) cursor.getString(mimeIdx).orEmpty() else ""
                    val name = if (nameIdx >= 0) cursor.getString(nameIdx).orEmpty() else ""

                    if (mime == DocumentsContract.Document.MIME_TYPE_DIR) {
                        val nextFolderName = currentSubfolderName ?: name.takeIf { it.isNotBlank() }
                        collectDocumentsRecursively(
                            resolver = resolver,
                            treeUri = treeUri,
                            parentDocId = docId,
                            currentSubfolderName = nextFolderName,
                            depth = depth + 1,
                            output = output
                        )
                    } else if (
                        mime.equals(BookEntity.MIME_PDF, ignoreCase = true) ||
                        name.endsWith(".pdf", ignoreCase = true) ||
                        mime.equals(BookEntity.MIME_TEXT, ignoreCase = true) ||
                        name.endsWith(".txt", ignoreCase = true)
                    ) {
                        val docUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, docId)
                        output.add(
                            DiscoveredTreeDoc(
                                uri = docUri,
                                folderAuthorName = currentSubfolderName
                                    ?: LibrarySectionDefaults.DEFAULT_PDF_AUTHOR_NAME
                            )
                        )
                    }
                }
            }
        }
    }

    private fun buildBookFromUri(
        resolver: ContentResolver,
        uri: Uri,
        targetAuthor: AuthorEntity,
        colorIndex: Int
    ): BookEntity? {
        val displayName = queryDisplayName(resolver, uri) ?: "كتاب مضاف"
        val detectedMime = resolver.getType(uri) ?: if (displayName.endsWith(".txt", ignoreCase = true)) {
            BookEntity.MIME_TEXT
        } else {
            BookEntity.MIME_PDF
        }

        val isPdf = detectedMime.equals(BookEntity.MIME_PDF, ignoreCase = true) ||
            displayName.endsWith(".pdf", ignoreCase = true)

        val cleanTitle = displayName
            .removeSuffix(".pdf")
            .removeSuffix(".PDF")
            .removeSuffix(".txt")
            .removeSuffix(".TXT")
            .replace('_', ' ')
            .trim()
            .ifBlank { "كتاب مضاف" }

        val pdfPages = if (isPdf) countPdfPages(resolver, uri) else 0
        val textPages = if (!isPdf) readAndSplitTextPages(resolver, uri) else ""
        val totalPagesCount = if (isPdf) {
            pdfPages.coerceAtLeast(1)
        } else {
            textPages.split(BookEntity.PAGE_DELIMITER).count { it.isNotBlank() }.coerceAtLeast(1)
        }

        val heightVariations = listOf(0.90f, 0.96f, 1.0f, 0.87f, 0.93f)
        val chosenColor = coverPalette[colorIndex % coverPalette.size]

        return BookEntity(
            id = "local_${UUID.randomUUID()}",
            authorId = targetAuthor.id,
            authorName = targetAuthor.name,
            title = cleanTitle,
            subtitle = if (isPdf) "مستند PDF من الهاتف" else "ملف نصي من الهاتف",
            category = if (isPdf) "PDF" else "نص",
            publishYear = "محلي",
            pagesCount = totalPagesCount,
            synopsis = if (isPdf) {
                "كتاب بصيغة PDF مضاف من جهازك ($totalPagesCount صفحة). يمكنك تعديل اسم الكتاب أو المؤلف أو قراءته مباشرة."
            } else {
                "كتاب نصي مضاف من جهازك ($totalPagesCount صفحة) مهيأ للقراءة بالخط العربي."
            },
            coverColorHex = chosenColor,
            spineAccentHex = 0xFFE8C872,
            heightFactor = heightVariations[colorIndex % heightVariations.size],
            thicknessDp = 48,
            contentPages = textPages,
            isBookmarked = false,
            currentPage = 0,
            localUri = uri.toString(),
            mimeType = if (isPdf) BookEntity.MIME_PDF else BookEntity.MIME_TEXT,
            pdfPageCount = pdfPages.coerceAtLeast(1)
        )
    }

    private fun queryDisplayName(resolver: ContentResolver, uri: Uri): String? {
        runCatching {
            resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) return cursor.getString(idx)
                }
            }
        }
        return uri.lastPathSegment?.substringAfterLast('/')
    }

    private fun countPdfPages(resolver: ContentResolver, uri: Uri): Int {
        return runCatching {
            resolver.openFileDescriptor(uri, "r")?.use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    renderer.pageCount
                }
            } ?: 1
        }.getOrDefault(1)
    }

    private fun readAndSplitTextPages(resolver: ContentResolver, uri: Uri): String {
        val rawText = runCatching {
            resolver.openInputStream(uri)?.use { stream ->
                BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).readText()
            }
        }.getOrNull().orEmpty().trim()

        if (rawText.isEmpty()) return "تعذر قراءة محتوى الملف النصي."

        val chunkSize = 900
        val pages = mutableListOf<String>()
        var cursor = 0
        while (cursor < rawText.length && pages.size < 200) {
            val end = (cursor + chunkSize).coerceAtMost(rawText.length)
            val sliceEnd = if (end < rawText.length) {
                val lastBreak = rawText.lastIndexOf(' ', end)
                if (lastBreak > cursor + 400) lastBreak else end
            } else {
                end
            }
            pages.add(rawText.substring(cursor, sliceEnd).trim())
            cursor = sliceEnd
        }
        return pages.joinToString(BookEntity.PAGE_DELIMITER)
    }
}
