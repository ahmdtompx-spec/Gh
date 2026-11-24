package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.IslamicStarDivider
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BookmarkCrimson
import com.example.ui.theme.DeepEmeraldSurface
import com.example.ui.theme.EmeraldObsidian
import com.example.ui.theme.GlassBorderBright
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.MutedIvory
import com.example.ui.theme.SoftGold
import com.example.ui.theme.WarmIvory

@Composable
fun BookDetailScreen(
    book: BookEntity?,
    onBack: () -> Unit,
    onOpenAuthorSection: (String) -> Unit,
    onOpenReader: (BookEntity) -> Unit,
    onToggleBookmark: (BookEntity) -> Unit,
    onUpdateBookMetadata: (BookEntity, String, String) -> Unit,
    onRemoveBook: (BookEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (book == null) return

    var tilted by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editTitle by remember(book.id, book.title) { mutableStateOf(book.title) }
    var editAuthor by remember(book.id, book.authorName) { mutableStateOf(book.authorName) }

    val rotationY by animateFloatAsState(
        targetValue = if (tilted) 14f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "book_cover_tilt"
    )
    val coverColor = Color(book.coverColorHex)
    val accentColor = Color(book.spineAccentHex)

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = DeepEmeraldSurface,
            titleContentColor = WarmIvory,
            textContentColor = WarmIvory,
            title = {
                Text(
                    text = "تعديل اسم الكتاب والمؤلف",
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        singleLine = true,
                        label = { Text("اسم الكتاب") },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AntiqueGold,
                            unfocusedBorderColor = GlassBorderBright,
                            focusedTextColor = WarmIvory,
                            unfocusedTextColor = WarmIvory,
                            focusedLabelColor = SoftGold,
                            unfocusedLabelColor = MutedIvory
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAuthor,
                        onValueChange = { editAuthor = it },
                        singleLine = true,
                        label = { Text("اسم المؤلف (القسم)") },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AntiqueGold,
                            unfocusedBorderColor = GlassBorderBright,
                            focusedTextColor = WarmIvory,
                            unfocusedTextColor = WarmIvory,
                            focusedLabelColor = SoftGold,
                            unfocusedLabelColor = MutedIvory
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateBookMetadata(book, editTitle, editAuthor)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AntiqueGold,
                        contentColor = EmeraldObsidian
                    )
                ) {
                    Text("حفظ", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("إلغاء", color = MutedIvory)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(bottom = 28.dp)
            .testTag("book_detail_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x28FFFFFF))
                    .border(1.dp, GlassBorderGold, RoundedCornerShape(14.dp))
                    .testTag("book_detail_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع",
                    tint = WarmIvory
                )
            }

            IconButton(
                onClick = { onToggleBookmark(book) },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x28FFFFFF))
                    .border(1.dp, GlassBorderGold, RoundedCornerShape(14.dp))
                    .testTag("book_detail_bookmark_button")
            ) {
                Icon(
                    imageVector = if (book.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "حفظ في المفضلة",
                    tint = if (book.isBookmarked) SoftGold else WarmIvory
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Interactive 3D Tactile Book Cover
        Box(
            modifier = Modifier
                .size(width = 186.dp, height = 264.dp)
                .graphicsLayer {
                    this.rotationY = rotationY
                    cameraDistance = 12f * density
                }
                .shadow(22.dp, RoundedCornerShape(16.dp), ambientColor = Color.Black, spotColor = Color.Black)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            coverColor,
                            Color(
                                red = (coverColor.red * 0.65f).coerceIn(0f, 1f),
                                green = (coverColor.green * 0.65f).coerceIn(0f, 1f),
                                blue = (coverColor.blue * 0.65f).coerceIn(0f, 1f)
                            )
                        )
                    )
                )
                .border(1.4.dp, accentColor.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                .clickable { tilted = !tilted }
                .testTag("book_3d_cover"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                drawRect(
                    color = accentColor.copy(alpha = 0.5f),
                    style = Stroke(width = 1.2.dp.toPx())
                )
                drawRect(
                    color = accentColor.copy(alpha = 0.25f),
                    topLeft = androidx.compose.ui.geometry.Offset(6.dp.toPx(), 6.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(
                        size.width - 12.dp.toPx(),
                        size.height - 12.dp.toPx()
                    ),
                    style = Stroke(width = 0.8.dp.toPx())
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (book.isPdf) "PDF" else book.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor
                )
                Text(
                    text = book.title,
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    color = WarmIvory,
                    textAlign = TextAlign.Center,
                    maxLines = 4
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = book.authorName,
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftGold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${book.totalReadablePages} صفحة",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedIvory
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "اضغط على الغلاف لاستعراض الميلان ثلاثي الأبعاد",
            style = MaterialTheme.typography.labelSmall,
            color = MutedIvory.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Frosted Glass Detail Card
        GlassCard(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            cornerRadius = 24.dp,
            borderAccent = accentColor.copy(alpha = 0.55f),
            showArabesqueCorners = true,
            contentPadding = PaddingValues(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = book.title,
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = WarmIvory
                )
                Text(
                    text = book.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SoftGold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22FFFFFF))
                            .border(1.dp, GlassBorderGold, RoundedCornerShape(12.dp))
                            .clickable { onOpenAuthorSection(book.authorId) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("open_author_from_book"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = SoftGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "قسم: ${book.authorName}",
                            style = MaterialTheme.typography.labelLarge,
                            color = WarmIvory
                        )
                    }

                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22FFFFFF))
                            .border(1.dp, GlassBorderGold, RoundedCornerShape(12.dp))
                            .testTag("edit_book_metadata_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل اسم الكتاب والمؤلف",
                            tint = SoftGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetadataGlassPill(label = "النوع", value = book.category, modifier = Modifier.weight(1f))
                    MetadataGlassPill(label = "المصدر", value = book.publishYear, modifier = Modifier.weight(0.8f))
                    MetadataGlassPill(label = "الصفحات", value = "${book.totalReadablePages}", modifier = Modifier.weight(0.8f))
                }

                Spacer(modifier = Modifier.height(14.dp))
                IslamicStarDivider(starColor = accentColor)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "معلومات الملف",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = book.synopsis,
                    style = MaterialTheme.typography.bodyLarge,
                    color = WarmIvory.copy(alpha = 0.92f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onOpenReader(book) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_reading_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AntiqueGold,
                        contentColor = EmeraldObsidian
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (book.currentPage > 0) {
                            "متابعة القراءة (صفحة ${book.currentPage + 1})"
                        } else {
                            "ابدأ القراءة الآن"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        onRemoveBook(book)
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("remove_book_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BookmarkCrimson.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB4AB))
                ) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "إزالة من المكتبة",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun MetadataGlassPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x1EFFFFFF))
            .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MutedIvory
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = WarmIvory,
            maxLines = 1
        )
    }
}
