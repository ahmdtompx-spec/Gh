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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.ui.components.AuthorKhatamSeal
import com.example.ui.components.GlassCard
import com.example.ui.components.IslamicStarDivider
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.MutedIvory
import com.example.ui.theme.SoftGold
import com.example.ui.theme.WarmIvory
import com.example.ui.viewmodel.AuthorShelfUiModel

@Composable
fun AuthorsScreen(
    shelves: List<AuthorShelfUiModel>,
    isExpandedWidth: Boolean,
    onAuthorClick: (AuthorEntity) -> Unit,
    contentBottomPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val columns = if (isExpandedWidth) 2 else 1

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier
            .fillMaxSize()
            .testTag("authors_grid"),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 14.dp,
            bottom = contentBottomPadding.calculateBottomPadding() + 24.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(span = { GridItemSpan(columns) }) {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = "أقسام المؤلفين",
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = WarmIvory
                )
                Text(
                    text = "لكل مؤلفٍ تضيفه ركنٌ خاص ورفٌ يجمع كتبه",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftGold.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                IslamicStarDivider()
            }
        }

        if (shelves.isEmpty()) {
            item(span = { GridItemSpan(columns) }) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 22.dp,
                    showArabesqueCorners = true,
                    contentPadding = PaddingValues(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا توجد أقسام مؤلفين بعد",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmIvory
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "عند إضافة ملفاتك من الهاتف وتحديد اسم المؤلف، سيظهر قسمه ورفّه هنا تلقائياً.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedIvory
                        )
                    }
                }
            }
        }

        items(shelves, key = { it.author.id }) { shelf ->
            val author = shelf.author
            val accent = Color(author.accentColorHex)
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAuthorClick(author) }
                    .testTag("author_card_${author.id}"),
                cornerRadius = 22.dp,
                borderAccent = accent.copy(alpha = 0.55f),
                showArabesqueCorners = true,
                contentPadding = PaddingValues(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AuthorKhatamSeal(
                            symbol = author.emblemSymbol,
                            accentColor = accent,
                            size = 54.dp
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = author.name,
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = WarmIvory
                            )
                            Text(
                                text = author.literaryTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = accent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = author.era,
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedIvory
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = author.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmIvory.copy(alpha = 0.88f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(accent.copy(alpha = 0.16f))
                                .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${shelf.books.size} مؤلفات على الرف",
                                style = MaterialTheme.typography.labelMedium,
                                color = WarmIvory
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "دخول القسم",
                                style = MaterialTheme.typography.labelLarge,
                                color = accent
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
