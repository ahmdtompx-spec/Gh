package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ReaderPreferences
import com.example.data.local.ReaderThemeMode
import com.example.data.model.BookEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.IslamicStarDivider
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepEmeraldSurface
import com.example.ui.theme.EmeraldObsidian
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.MutedIvory
import com.example.ui.theme.ParchmentBg
import com.example.ui.theme.ParchmentInk
import com.example.ui.theme.SepiaBg
import com.example.ui.theme.SepiaInk
import com.example.ui.theme.SoftGold
import com.example.ui.theme.WarmIvory

@Composable
fun ReaderScreen(
    book: BookEntity?,
    preferences: ReaderPreferences,
    renderPdfPage: suspend (String, Int, Int) -> Bitmap?,
    onBack: () -> Unit,
    onPageChange: (Int) -> Unit,
    onToggleBookmark: (BookEntity) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onThemeModeChange: (ReaderThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    if (book == null) return

    val totalPages = book.totalReadablePages
    val textPages = remember(book.id, book.contentPages) {
        if (book.isPdf) emptyList() else book.splitPages()
    }
    val currentPage = book.currentPage.coerceIn(0, (totalPages - 1).coerceAtLeast(0))
    var showSettings by remember { mutableStateOf(false) }

    val (bgBrush, textColor, accentColor) = when (preferences.themeMode) {
        ReaderThemeMode.NIGHT_GLASS -> Triple(
            Brush.verticalGradient(listOf(EmeraldObsidian, DeepEmeraldSurface, EmeraldObsidian)),
            WarmIvory,
            SoftGold
        )
        ReaderThemeMode.SEPIA -> Triple(
            Brush.verticalGradient(listOf(SepiaBg, Color(0xFFE7D7B7))),
            SepiaInk,
            Color(0xFF7D5618)
        )
        ReaderThemeMode.PARCHMENT -> Triple(
            Brush.verticalGradient(listOf(ParchmentBg, Color(0xFFF3EBDA))),
            ParchmentInk,
            Color(0xFF8A621B)
        )
    }

    val nightPdfColorFilter = remember {
        ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    -0.88f, 0f, 0f, 0f, 232f,
                    0f, -0.84f, 0f, 0f, 236f,
                    0f, 0f, -0.88f, 0f, 224f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("reader_screen")
    ) {
        // Top Reader Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("reader_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "إغلاق القراءة",
                    tint = textColor
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.authorName,
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor
                )
            }
            IconButton(
                onClick = { onToggleBookmark(book) },
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = if (book.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "حفظ الكتاب",
                    tint = if (book.isBookmarked) AntiqueGold else textColor
                )
            }
            IconButton(
                onClick = { showSettings = !showSettings },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("reader_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "إعدادات القراءة",
                    tint = if (showSettings) AntiqueGold else textColor
                )
            }
        }

        // Expandable Frosted Glass Reading Settings
        AnimatedVisibility(visible = showSettings) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                cornerRadius = 18.dp,
                contentPadding = PaddingValues(14.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (!book.isPdf) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "حجم الخط",
                                style = MaterialTheme.typography.labelLarge,
                                color = WarmIvory
                            )
                            Text(
                                text = "${preferences.fontSizeSp.toInt()} sp",
                                style = MaterialTheme.typography.labelMedium,
                                color = SoftGold
                            )
                        }
                        Slider(
                            value = preferences.fontSizeSp,
                            onValueChange = onFontSizeChange,
                            valueRange = 15f..28f,
                            colors = SliderDefaults.colors(
                                thumbColor = AntiqueGold,
                                activeTrackColor = AntiqueGold,
                                inactiveTrackColor = Color(0x40FFFFFF)
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    Text(
                        text = if (book.isPdf) "إضاءة عرض PDF" else "نمط الورق والزجاج",
                        style = MaterialTheme.typography.labelLarge,
                        color = WarmIvory
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReaderThemeMode.entries.forEach { mode ->
                            val selected = preferences.themeMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selected) AntiqueGold.copy(alpha = 0.25f) else Color(0x1FFFFFFF)
                                    )
                                    .border(
                                        1.dp,
                                        if (selected) AntiqueGold else Color(0x33FFFFFF),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onThemeModeChange(mode) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode.labelAr,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selected) WarmIvory else MutedIvory
                                )
                            }
                        }
                    }
                }
            }
        }

        // Page Content Area: Real PDF Renderer OR Arabic Text Pages
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val targetWidthPx = with(LocalDensity.current) { maxWidth.roundToPx().coerceIn(600, 1600) }

            if (book.isPdf) {
                var renderedBitmap by remember(book.id, currentPage) { mutableStateOf<Bitmap?>(null) }
                var isLoadingPage by remember(book.id, currentPage) { mutableStateOf(true) }

                LaunchedEffect(book.localUri, currentPage, targetWidthPx) {
                    isLoadingPage = true
                    renderedBitmap = book.localUri?.let { uri ->
                        renderPdfPage(uri, currentPage, targetWidthPx)
                    }
                    isLoadingPage = false
                }

                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        val forward = targetState >= initialState
                        (slideInHorizontally { if (forward) -it / 4 else it / 4 } + fadeIn())
                            .togetherWith(slideOutHorizontally { if (forward) it / 4 else -it / 4 } + fadeOut())
                    },
                    label = "pdf_page_turn"
                ) { _ ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isLoadingPage -> {
                                CircularProgressIndicator(color = AntiqueGold)
                            }
                            renderedBitmap != null -> {
                                val bmp = renderedBitmap!!
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "صفحة ${currentPage + 1}",
                                    contentScale = ContentScale.FillWidth,
                                    colorFilter = if (preferences.themeMode == ReaderThemeMode.NIGHT_GLASS) {
                                        nightPdfColorFilter
                                    } else {
                                        null
                                    },
                                    modifier = Modifier
                                        .widthIn(max = 760.dp)
                                        .fillMaxWidth()
                                        .shadow(14.dp, RoundedCornerShape(12.dp))
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, GlassBorderGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                )
                            }
                            else -> {
                                GlassCard(
                                    modifier = Modifier
                                        .widthIn(max = 520.dp)
                                        .fillMaxWidth(),
                                    showArabesqueCorners = true
                                ) {
                                    Text(
                                        text = "تعذر فتح ملف PDF؛ تأكد من بقاء الملف في موضعه على الهاتف أو أعد إضافته.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = WarmIvory,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        val forward = targetState >= initialState
                        (slideInHorizontally { if (forward) -it / 4 else it / 4 } + fadeIn())
                            .togetherWith(slideOutHorizontally { if (forward) it / 4 else -it / 4 } + fadeOut())
                    },
                    label = "reader_page_turn"
                ) { pageIndex ->
                    val pageText = textPages.getOrElse(pageIndex) { "" }
                    Column(
                        modifier = Modifier
                            .widthIn(max = 680.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "— صفحة ${pageIndex + 1} —",
                            fontFamily = AmiriFontFamily,
                            fontSize = 15.sp,
                            color = accentColor
                        )
                        IslamicStarDivider(starColor = accentColor)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = pageText,
                            fontFamily = AmiriFontFamily,
                            fontSize = preferences.fontSizeSp.sp,
                            lineHeight = (preferences.fontSizeSp * 1.75f).sp,
                            color = textColor,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Bottom Frosted Glass Page Navigation Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            val progress = (currentPage + 1).toFloat() / totalPages.coerceAtLeast(1).toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = AntiqueGold,
                trackColor = textColor.copy(alpha = 0.16f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x24000000))
                    .border(1.dp, GlassBorderGold.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = currentPage > 0) { onPageChange(currentPage - 1) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("reader_prev_page"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = if (currentPage > 0) accentColor else textColor.copy(alpha = 0.35f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "السابق",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (currentPage > 0) textColor else textColor.copy(alpha = 0.35f)
                    )
                }

                Text(
                    text = "${currentPage + 1} / $totalPages",
                    style = MaterialTheme.typography.labelLarge,
                    color = accentColor
                )

                Row(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = currentPage < totalPages - 1) { onPageChange(currentPage + 1) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("reader_next_page"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "التالي",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (currentPage < totalPages - 1) textColor else textColor.copy(alpha = 0.35f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = if (currentPage < totalPages - 1) accentColor else textColor.copy(alpha = 0.35f)
                    )
                }
            }
        }
    }
}
