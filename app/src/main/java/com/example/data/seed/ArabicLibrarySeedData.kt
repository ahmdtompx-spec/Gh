package com.example.data.seed

import com.example.data.model.AuthorEntity
import java.util.UUID

object LibrarySectionDefaults {

    const val DEFAULT_PDF_AUTHOR_NAME = "ملفات PDF"

    private val accentPalette = listOf(
        0xFFD4AF37,
        0xFF63B3A0,
        0xFFE5A952,
        0xFF8BB7D6,
        0xFFD67A6E,
        0xFFC596D8,
        0xFF79C48F,
        0xFFE6BE6A
    )

    fun createAuthor(name: String, order: Int): AuthorEntity {
        val cleanName = name.trim().ifEmpty { DEFAULT_PDF_AUTHOR_NAME }
        val firstChar = cleanName.firstOrNull { !it.isWhitespace() }?.toString() ?: "م"
        val isAutoPdf = cleanName == DEFAULT_PDF_AUTHOR_NAME
        return AuthorEntity(
            id = "author_${UUID.nameUUIDFromBytes(cleanName.lowercase().toByteArray())}",
            name = cleanName,
            literaryTitle = if (isAutoPdf) "الكتب والملفات المستوردة من الهاتف" else "قسم مؤلفات $cleanName",
            era = if (isAutoPdf) "استيراد تلقائي / يدوي" else "مكتبتك الخاصة",
            bio = if (isAutoPdf) {
                "رف مخصص لملفات PDF والكتب المضافة من جهازك."
            } else {
                "قسم خاص يضم الكتب والملفات المنسوبة إلى $cleanName في مكتبتك."
            },
            accentColorHex = accentPalette[order % accentPalette.size],
            emblemSymbol = firstChar,
            sectionOrder = order
        )
    }
}
