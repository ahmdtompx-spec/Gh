package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AuthorEntity
import com.example.data.model.BookEntity
import com.example.ui.components.AuthorShelfSection
import com.example.ui.components.GlassAndWalnutShelfLedge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassFilterChip
import com.example.ui.components.GlassSearchBar
import com.example.ui.components.IslamicStarDivider
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.EmeraldObsidian
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.MutedIvory
import com.example.ui.theme.SoftGold
import com.example.ui.theme.WarmIvory
import com.example.ui.viewmodel.LibraryUiState

@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onSearchQueryChange: (String) -> Unit,
    onSelectAuthorFilter: (String?) -> Unit,
    onToggleShelfMode: () -> Unit,
    onOpenImportSheet: () -> Unit,
    onQuickPickFiles: () -> Unit,
    onQuickLinkFolder: () -> Unit,
    onSyncFolderNow: () -> Unit,
    onAuthorClick: (AuthorEntity) -> Unit,
    onBookClick: (BookEntity) -> Unit,
    onToggleBookmark: (BookEntity) -> Unit,
    onResumeBook: (BookEntity) -> Unit,
    contentBottomPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    if (uiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AntiqueGold)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("library_shelf_list"),
        contentPadding = PaddingValues(
            top = 12.dp,
            bottom = contentBottomPadding.calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Glassmorphic Hero Header
        item(key = "hero_header") {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    cornerRadius = 26.dp,
                    borderAccent = GlassBorderGold,
                    showArabesqueCorners = true,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_arabesque_banner_1795506708661),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                                .alpha(0.22f)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x55091713),
                                            Color(0x880D221C)
                                        )
                                    )
                                )
                                .padding(horizontal = 20.dp, vertical = 18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "مكتبة اقرأ",
                                        fontFamily = AmiriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 32.sp,
                                        color = WarmIvory
                                    )
                                    Text(
                                        text = "مكتبتك الخاصة بتأثيرات الزجاج الشفاف والرفوف الواقعية",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SoftGold.copy(alpha = 0.92f)
                                    )
                                }
                                IconButton(
                                    onClick = onToggleShelfMode,
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0x28FFFFFF))
                                        .border(1.dp, GlassBorderGold, RoundedCornerShape(14.dp))
                                        .testTag("toggle_shelf_mode_button")
                                ) {
                                    Icon(
                                        imageVector = if (uiState.readerPreferences.isRealisticShelfMode) {
                                            Icons.Default.GridView
                                        } else {
                                            Icons.Default.ViewCarousel
                                        },
                                        contentDescription = if (uiState.readerPreferences.isRealisticShelfMode) {
                                            "تبديل إلى عرض البطاقات"
                                        } else {
                                            "تبديل إلى عرض الرفوف الواقعي"
                                        },
                                        tint = SoftGold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            IslamicStarDivider()
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (uiState.totalBooksCount == 0) {
                                        "المكتبة فارغة حالياً"
                                    } else {
                                        "${uiState.authors.size} أقسام  •  ${uiState.totalBooksCount} كتاباً"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MutedIvory
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (uiState.readerPreferences.linkedPdfFolderName != null) {
                                        Row(
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0x28FFFFFF))
                                                .border(1.dp, GlassBorderGold, RoundedCornerShape(12.dp))
                                                .clickable(onClick = onSyncFolderNow)
                                                .padding(horizontal = 10.dp, vertical = 7.dp)
                                                .testTag("quick_sync_folder_button"),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Sync,
                                                contentDescription = "مزامنة ملفات PDF",
                                                tint = SoftGold,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "مزامنة",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = WarmIvory
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(AntiqueGold)
                                            .clickable(onClick = onOpenImportSheet)
                                            .padding(horizontal = 12.dp, vertical = 7.dp)
                                            .testTag("add_phone_files_button"),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "إضافة ملفات من الهاتف",
                                            tint = EmeraldObsidian,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "إضافة كتب / PDF",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldObsidian
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Empty User-Built Library State: Realistic Empty Shelf + Direct Import Actions
        if (uiState.totalBooksCount == 0) {
            item(key = "empty_user_library") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlassCard(
                        modifier = Modifier
                            .widthIn(max = 680.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .testTag("empty_library_card"),
                        cornerRadius = 24.dp,
                        borderAccent = AntiqueGold.copy(alpha = 0.6f),
                        showArabesqueCorners = true,
                        contentPadding = PaddingValues(22.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AutoStories,
                                contentDescription = null,
                                tint = SoftGold,
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "رفوف مكتبتك جاهزة لإضافاتك",
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                color = WarmIvory,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "أضف ملفاتك وكتبك من الهاتف وحدد اسم المؤلف ليظهر لكل مؤلف قسم ورف خاص، أو اربط مجلد PDF لتنضاف الملفات تلقائياً.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MutedIvory,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = onQuickPickFiles,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("empty_pick_files_button"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AntiqueGold,
                                    contentColor = EmeraldObsidian
                                )
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.NoteAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "إضافة ملفات من الهاتف (PDF / نص)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = onQuickLinkFolder,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("empty_link_folder_button"),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderGold),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmIvory)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CreateNewFolder,
                                    contentDescription = null,
                                    tint = SoftGold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ربط مجلد لاستيراد PDF تلقائياً",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))
                    GlassAndWalnutShelfLedge(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
            return@LazyColumn
        }

        // Quick resume currently reading glass card
        val latestReading = uiState.readingInProgressBooks.firstOrNull()
        if (latestReading != null) {
            item(key = "resume_reading") {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { onResumeBook(latestReading) }
                            .testTag("resume_reading_card"),
                        cornerRadius = 18.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(latestReading.coverColorHex))
                                    .border(1.dp, AntiqueGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = SoftGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "متابعة القراءة: ${latestReading.title}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = WarmIvory,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${latestReading.authorName} • صفحة ${latestReading.currentPage + 1} من ${latestReading.totalReadablePages}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MutedIvory
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { latestReading.readingProgress },
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
            }
        }

        // Frosted Glass Search + Author Section Filter Chips
        item(key = "search_and_filters") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    GlassSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = onSearchQueryChange,
                        placeholder = "ابحث عن كتاب أو مؤلف أضفته…"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item(key = "filter_all") {
                        GlassFilterChip(
                            label = "كل الأقسام",
                            selected = uiState.selectedAuthorId == null,
                            onClick = { onSelectAuthorFilter(null) },
                            modifier = Modifier.testTag("filter_chip_all")
                        )
                    }
                    items(uiState.authors, key = { it.id }) { author ->
                        GlassFilterChip(
                            label = author.name,
                            selected = uiState.selectedAuthorId == author.id,
                            onClick = { onSelectAuthorFilter(author.id) },
                            accentColor = Color(author.accentColorHex),
                            modifier = Modifier.testTag("filter_chip_${author.id}")
                        )
                    }
                }
            }
        }

        if (uiState.shelves.isEmpty()) {
            item(key = "empty_search") {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        showArabesqueCorners = true
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AutoStories,
                                contentDescription = null,
                                tint = SoftGold,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "لم يتم العثور على نتائج مطابقة",
                                style = MaterialTheme.typography.titleMedium,
                                color = WarmIvory
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "جرّب البحث باسم كتاب آخر أو اختر قسماً آخر.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedIvory
                            )
                        }
                    }
                }
            }
        } else {
            itemsIndexed(
                items = uiState.shelves,
                key = { _, shelf -> shelf.author.id }
            ) { index, shelf ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { (index + 1) * 18 })
                ) {
                    AuthorShelfSection(
                        author = shelf.author,
                        books = shelf.books,
                        isRealisticShelfMode = uiState.readerPreferences.isRealisticShelfMode,
                        onAuthorClick = onAuthorClick,
                        onBookClick = onBookClick,
                        onToggleBookmark = onToggleBookmark
                    )
                }
            }
        }
    }
}
