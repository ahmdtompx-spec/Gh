package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthorEntity
import com.example.data.model.BookEntity
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BookmarkCrimson
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.GlassBorderBright
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.LanternAmber
import com.example.ui.theme.MutedIvory
import com.example.ui.theme.ShelfGlassRim
import com.example.ui.theme.SoftGold
import com.example.ui.theme.WalnutDark
import com.example.ui.theme.WalnutHighlight
import com.example.ui.theme.WalnutMedium
import com.example.ui.theme.WarmIvory

/**
 * Full Author Section Shelf:
 * 1) Frosted glass author section plaque with Arabic geometric Khatam seal
 * 2) Tactile 3D books placed realistically on a walnut-and-glass shelf ledge
 */
@Composable
fun AuthorShelfSection(
    author: AuthorEntity,
    books: List<BookEntity>,
    isRealisticShelfMode: Boolean,
    onAuthorClick: (AuthorEntity) -> Unit,
    onBookClick: (BookEntity) -> Unit,
    onToggleBookmark: (BookEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(author.accentColorHex)

    Column(modifier = modifier.fillMaxWidth()) {
        // Frosted Glass Author Section Header Plaque
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onAuthorClick(author) }
                .testTag("author_section_${author.id}"),
            cornerRadius = 20.dp,
            borderAccent = accentColor.copy(alpha = 0.5f),
            showArabesqueCorners = true,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuthorKhatamSeal(
                    symbol = author.emblemSymbol,
                    accentColor = accentColor,
                    size = 46.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "قسم ${author.name}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = AmiriFontFamily,
                            fontSize = 21.sp
                        ),
                        color = WarmIvory,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = author.literaryTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftGold.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.16f))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${books.size} كتب",
                        style = MaterialTheme.typography.labelMedium,
                        color = WarmIvory
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "عرض قسم ${author.name}",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isRealisticShelfMode) {
            // Realistic Bookshelf with books resting directly on the glass-walnut ledge
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 22.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        items(books, key = { it.id }) { book ->
                            RealisticShelfBookItem(
                                book = book,
                                onClick = { onBookClick(book) }
                            )
                        }
                    }
                    // The physical glass-trimmed walnut shelf ledge right underneath the books
                    GlassAndWalnutShelfLedge(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    )
                }
            }
        } else {
            // Frosted Glass Cover Showcase Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(books, key = { it.id }) { book ->
                    GlassBookShowcaseCard(
                        book = book,
                        onClick = { onBookClick(book) },
                        onToggleBookmark = { onToggleBookmark(book) }
                    )
                }
            }
        }
    }
}

/**
 * Tactile 3D Book standing on the library shelf with leather/cloth binding,
 * visible page edges, gold Arabesque framing, Book Title, and Author Name.
 */
@Composable
fun RealisticShelfBookItem(
    book: BookEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val liftY by animateFloatAsState(
        targetValue = if (isPressed) -14f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "book_lift"
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "book_scale"
    )

    val coverColor = Color(book.coverColorHex)
    val accentColor = Color(book.spineAccentHex)
    val bookHeight = (196f * book.heightFactor).dp
    val bookWidth = 122.dp

    Box(
        modifier = modifier
            .width(bookWidth)
            .height(bookHeight)
            .graphicsLayer {
                translationY = liftY
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("shelf_book_${book.id}")
    ) {
        // Right/back cream page-edge block for 3D book thickness illusion
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp, end = 0.dp, start = 6.dp, bottom = 2.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 10.dp, bottomEnd = 10.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFD8CEB8), Color(0xFFF6EFE0), Color(0xFFCBBFA6))
                    )
                )
        )

        // Main Hardcover Front + Spine
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 6.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp, topEnd = 4.dp, bottomEnd = 4.dp),
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp, topEnd = 4.dp, bottomEnd = 4.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            coverColor.copy(alpha = 0.96f),
                            coverColor,
                            Color(
                                red = (coverColor.red * 0.68f).coerceIn(0f, 1f),
                                green = (coverColor.green * 0.68f).coerceIn(0f, 1f),
                                blue = (coverColor.blue * 0.68f).coerceIn(0f, 1f)
                            )
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(300f, 500f)
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(accentColor.copy(alpha = 0.65f), accentColor.copy(alpha = 0.25f))
                    ),
                    shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp, topEnd = 4.dp, bottomEnd = 4.dp)
                )
        ) {
            // Subtle leather spine crease & gold bands on the spine edge
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(12.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f), Color.White.copy(alpha = 0.12f))
                        )
                    )
            )

            // Subtle gold Arabesque cover frame
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            ) {
                drawRect(
                    color = accentColor.copy(alpha = 0.42f),
                    style = Stroke(width = 0.9.dp.toPx())
                )
                // Inner ornamental top arch accent
                val archW = size.width * 0.55f
                val startX = (size.width - archW) / 2f
                val archPath = Path().apply {
                    moveTo(startX, 10.dp.toPx())
                    quadraticTo(size.width / 2f, 2.dp.toPx(), startX + archW, 10.dp.toPx())
                }
                drawPath(archPath, color = accentColor.copy(alpha = 0.6f), style = Stroke(width = 1.dp.toPx()))
            }

            // Book title + Author name embossed on the book cover on the shelf
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 18.dp, top = 22.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (book.isPdf) "PDF" else book.category,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = accentColor.copy(alpha = 0.92f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.title,
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = WarmIvory,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(26.dp)
                            .height(1.dp)
                            .background(accentColor.copy(alpha = 0.6f))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.authorName,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = SoftGold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Hanging crimson bookmark ribbon if bookmarked or currently reading
            if (book.isBookmarked || book.currentPage > 0) {
                Canvas(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 14.dp)
                        .size(width = 12.dp, height = 24.dp)
                ) {
                    val ribbon = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        lineTo(size.width / 2f, size.height - 6.dp.toPx())
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(
                        ribbon,
                        color = if (book.isBookmarked) BookmarkCrimson else AntiqueGold
                    )
                }
            }
        }
    }
}

/**
 * Realistic walnut & frosted-glass bookshelf ledge with specular highlight rim
 * and warm under-shelf amber LED illumination.
 */
@Composable
fun GlassAndWalnutShelfLedge(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        // Frosted glass top surface reflecting the books above
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x3DFFFFFF), Color(0x18FFFFFF))
                    )
                )
                .border(
                    width = 0.8.dp,
                    color = ShelfGlassRim,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
        )
        // Polished dark walnut shelf front fascia
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .shadow(10.dp, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(WalnutHighlight, WalnutMedium, WalnutDark)
                    )
                )
                .drawBehind {
                    // Subtle warm brass inlay strip along wood front
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                AntiqueGold.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        ),
                        start = Offset(size.width * 0.05f, size.height * 0.45f),
                        end = Offset(size.width * 0.95f, size.height * 0.45f),
                        strokeWidth = 0.9.dp.toPx()
                    )
                }
        )
        // Warm lantern under-shelf glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(LanternAmber.copy(alpha = 0.18f), Color.Transparent)
                    )
                )
        )
    }
}

@Composable
fun GlassBookShowcaseCard(
    book: BookEntity,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coverColor = Color(book.coverColorHex)
    val accentColor = Color(book.spineAccentHex)

    GlassCard(
        modifier = modifier
            .width(176.dp)
            .clickable(onClick = onClick)
            .testTag("grid_book_${book.id}"),
        cornerRadius = 20.dp,
        borderAccent = accentColor.copy(alpha = 0.45f),
        contentPadding = PaddingValues(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(coverColor, coverColor.copy(alpha = 0.72f))
                        )
                    )
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(10.dp)
            ) {
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = if (book.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "حفظ الكتاب",
                        tint = if (book.isBookmarked) SoftGold else WarmIvory.copy(alpha = 0.85f)
                    )
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(
                        text = book.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = book.title,
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = WarmIvory,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = book.authorName,
                style = MaterialTheme.typography.titleMedium,
                color = SoftGold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${book.publishYear} • ${book.pagesCount} صفحة",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedIvory,
                maxLines = 1
            )
            if (book.readingProgress > 0f) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { book.readingProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AntiqueGold,
                    trackColor = Color(0x33FFFFFF)
                )
            }
        }
    }
}
