package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookEntity
import com.example.ui.components.GlassAndWalnutShelfLedge
import com.example.ui.components.GlassCard
import com.example.ui.components.IslamicStarDivider
import com.example.ui.components.RealisticShelfBookItem
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.MutedIvory
import com.example.ui.theme.SoftGold
import com.example.ui.theme.WarmIvory

@Composable
fun FavoritesScreen(
    bookmarkedBooks: List<BookEntity>,
    readingInProgressBooks: List<BookEntity>,
    onBookClick: (BookEntity) -> Unit,
    onToggleBookmark: (BookEntity) -> Unit,
    onOpenReader: (BookEntity) -> Unit,
    contentBottomPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("favorites_screen"),
        contentPadding = PaddingValues(
            top = 14.dp,
            bottom = contentBottomPadding.calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "مفضّلتي وقراءاتي",
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = WarmIvory
                )
                Text(
                    text = "رفّك الشخصي للكتب المحفوظة والصفحات المفتوحة",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftGold.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                IslamicStarDivider()
            }
        }

        if (bookmarkedBooks.isEmpty() && readingInProgressBooks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier
                            .widthIn(max = 520.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        showArabesqueCorners = true
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Bookmarks,
                                contentDescription = null,
                                tint = SoftGold,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "رفّك الشخصي بانتظار أول كتاب",
                                style = MaterialTheme.typography.titleMedium,
                                color = WarmIvory,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "اختر أي كتاب من رفوف المكتبة واضغط على أيقونة الحفظ أو ابدأ القراءة ليظهر هنا.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedIvory,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        if (bookmarkedBooks.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "الرف الشخصي المحفوظ (${bookmarkedBooks.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftGold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 22.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(216.dp)
                    ) {
                        items(bookmarkedBooks, key = { "fav_shelf_${it.id}" }) { book ->
                            RealisticShelfBookItem(
                                book = book,
                                onClick = { onBookClick(book) }
                            )
                        }
                    }
                    GlassAndWalnutShelfLedge(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    )
                }
            }

            items(bookmarkedBooks, key = { "fav_card_${it.id}" }) { book ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { onBookClick(book) },
                        cornerRadius = 18.dp,
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 48.dp, height = 66.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(book.coverColorHex))
                                    .border(1.dp, AntiqueGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = book.title,
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    color = WarmIvory,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${book.authorName} • ${book.category}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SoftGold
                                )
                            }
                            IconButton(
                                onClick = { onToggleBookmark(book) },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Bookmark,
                                    contentDescription = "إزالة من المفضلة",
                                    tint = SoftGold
                                )
                            }
                        }
                    }
                }
            }
        }

        if (readingInProgressBooks.isNotEmpty()) {
            item {
                Text(
                    text = "قيد القراءة (${readingInProgressBooks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftGold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            items(readingInProgressBooks, key = { "reading_${it.id}" }) { book ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { onOpenReader(book) },
                        cornerRadius = 18.dp,
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = book.title,
                                        fontFamily = AmiriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 19.sp,
                                        color = WarmIvory
                                    )
                                    Text(
                                        text = "${book.authorName} • صفحة ${book.currentPage + 1} من ${book.totalReadablePages}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MutedIvory
                                    )
                                }
                                Text(
                                    text = "متابعة",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = SoftGold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { book.readingProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = AntiqueGold,
                                trackColor = Color(0x33FFFFFF)
                            )
                        }
                    }
                }
            }
        }
    }
}
