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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthorEntity
import com.example.data.model.BookEntity
import com.example.ui.components.AuthorKhatamSeal
import com.example.ui.components.GlassAndWalnutShelfLedge
import com.example.ui.components.GlassCard
import com.example.ui.components.IslamicStarDivider
import com.example.ui.components.RealisticShelfBookItem
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.MutedIvory
import com.example.ui.theme.SoftGold
import com.example.ui.theme.WarmIvory

@Composable
fun AuthorDetailScreen(
    author: AuthorEntity?,
    books: List<BookEntity>,
    onBack: () -> Unit,
    onBookClick: (BookEntity) -> Unit,
    onToggleBookmark: (BookEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (author == null) return
    val accent = Color(author.accentColorHex)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("author_detail_screen"),
        contentPadding = PaddingValues(top = 12.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x28FFFFFF))
                        .border(1.dp, GlassBorderGold, RoundedCornerShape(14.dp))
                        .testTag("author_detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = WarmIvory
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "قسم ${author.name}",
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = WarmIvory
                )
            }
        }

        // Author Biography Glass Plaque
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    cornerRadius = 24.dp,
                    borderAccent = accent.copy(alpha = 0.6f),
                    showArabesqueCorners = true,
                    contentPadding = PaddingValues(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AuthorKhatamSeal(
                            symbol = author.emblemSymbol,
                            accentColor = accent,
                            size = 64.dp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = author.name,
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = WarmIvory
                        )
                        Text(
                            text = "${author.literaryTitle}  •  ${author.era}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = accent
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        IslamicStarDivider(starColor = accent)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = author.bio,
                            style = MaterialTheme.typography.bodyLarge,
                            color = WarmIvory.copy(alpha = 0.92f)
                        )
                    }
                }
            }
        }

        // Author's Realistic Bookshelf
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "الرف الخاص بالمؤلف",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftGold,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(216.dp)
                ) {
                    items(books, key = { it.id }) { book ->
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

        // Detailed Book Cards inside Author Section
        items(books, key = { "detail_${it.id}" }) { book ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onBookClick(book) }
                        .testTag("author_book_row_${book.id}"),
                    cornerRadius = 18.dp,
                    borderAccent = Color(book.spineAccentHex).copy(alpha = 0.45f),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 56.dp, height = 78.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(book.coverColorHex))
                                .border(
                                    1.dp,
                                    Color(book.spineAccentHex).copy(alpha = 0.6f),
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = book.publishYear,
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftGold
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = book.title,
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = WarmIvory
                            )
                            Text(
                                text = "${book.category} • ${book.pagesCount} صفحة",
                                style = MaterialTheme.typography.labelMedium,
                                color = SoftGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = book.synopsis,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedIvory,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = { onToggleBookmark(book) },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = if (book.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "حفظ الكتاب",
                                tint = if (book.isBookmarked) SoftGold else MutedIvory
                            )
                        }
                    }
                }
            }
        }
    }
}
