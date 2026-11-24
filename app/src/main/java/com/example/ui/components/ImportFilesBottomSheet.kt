package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthorEntity
import com.example.data.seed.LibrarySectionDefaults
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepEmeraldSurface
import com.example.ui.theme.EmeraldObsidian
import com.example.ui.theme.GlassBorderBright
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.MutedIvory
import com.example.ui.theme.SoftGold
import com.example.ui.theme.WarmIvory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportFilesBottomSheet(
    existingAuthors: List<AuthorEntity>,
    authorNameInput: String,
    onAuthorNameChange: (String) -> Unit,
    linkedFolderName: String?,
    isImporting: Boolean,
    onPickFilesClick: () -> Unit,
    onLinkFolderClick: () -> Unit,
    onSyncFolderNowClick: () -> Unit,
    onUnlinkFolderClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepEmeraldSurface,
        contentColor = WarmIvory,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .testTag("import_files_sheet"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "إضافة كتب وملفات من الهاتف",
                fontFamily = AmiriFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = WarmIvory
            )
            Text(
                text = "أضف ملفات PDF أو نصوص من هاتفك واكتب اسم المؤلف ليظهر له قسم ورف مستقل، أو اربط مجلداً لاستيراد ملفات PDF تلقائياً.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedIvory
            )

            if (isImporting) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AntiqueGold,
                    trackColor = Color(0x33FFFFFF)
                )
            }

            // Custom Author Name Input + Existing Author Quick Chips
            OutlinedTextField(
                value = authorNameInput,
                onValueChange = onAuthorNameChange,
                singleLine = true,
                label = { Text("اسم المؤلف / القسم (اختياري)") },
                placeholder = { Text(LibrarySectionDefaults.DEFAULT_PDF_AUTHOR_NAME) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = SoftGold
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AntiqueGold,
                    unfocusedBorderColor = GlassBorderBright,
                    focusedTextColor = WarmIvory,
                    unfocusedTextColor = WarmIvory,
                    focusedLabelColor = SoftGold,
                    unfocusedLabelColor = MutedIvory,
                    cursorColor = AntiqueGold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("import_author_name_input")
            )

            if (existingAuthors.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(existingAuthors, key = { it.id }) { author ->
                        GlassFilterChip(
                            label = author.name,
                            selected = authorNameInput.trim() == author.name,
                            onClick = { onAuthorNameChange(author.name) },
                            accentColor = Color(author.accentColorHex)
                        )
                    }
                }
            }

            IslamicStarDivider()

            // Option 1: Pick individual / multiple files from phone
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isImporting, onClick = onPickFilesClick)
                    .testTag("pick_phone_files_card"),
                cornerRadius = 18.dp,
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AntiqueGold.copy(alpha = 0.22f))
                            .border(1.dp, GlassBorderGold, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.NoteAdd,
                            contentDescription = null,
                            tint = SoftGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "اختيار ملفات من الهاتف (PDF / نص)",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmIvory
                        )
                        Text(
                            text = "اختر ملفاً أو عدة ملفات لوضعها على الرف فوراً",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedIvory
                        )
                    }
                }
            }

            // Option 2: Link phone folder for automatic PDF import
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auto_sync_folder_card"),
                cornerRadius = 18.dp,
                borderAccent = AntiqueGold.copy(alpha = 0.6f),
                contentPadding = PaddingValues(14.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1C5B4B))
                                .border(1.dp, GlassBorderGold, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreateNewFolder,
                                contentDescription = null,
                                tint = SoftGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "إضافة ملفات PDF تلقائياً من مجلد",
                                style = MaterialTheme.typography.titleMedium,
                                color = WarmIvory
                            )
                            Text(
                                text = if (linkedFolderName != null) {
                                    "المجلد المرتبط: $linkedFolderName"
                                } else {
                                    "اربط مجلد التنزيلات أو الكتب ليتم جلب ملفات PDF تلقائياً"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (linkedFolderName != null) SoftGold else MutedIvory
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (linkedFolderName == null) {
                        Button(
                            onClick = onLinkFolderClick,
                            enabled = !isImporting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("link_pdf_folder_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AntiqueGold,
                                contentColor = EmeraldObsidian
                            )
                        ) {
                            Icon(imageVector = Icons.Default.CreateNewFolder, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ربط مجلد على الهاتف",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onSyncFolderNowClick,
                                enabled = !isImporting,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("sync_folder_now_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AntiqueGold,
                                    contentColor = EmeraldObsidian
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Sync, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مزامنة الآن",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            OutlinedButton(
                                onClick = onLinkFolderClick,
                                enabled = !isImporting,
                                modifier = Modifier.height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderGold),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmIvory)
                            ) {
                                Text(text = "تغيير", style = MaterialTheme.typography.labelLarge)
                            }
                            IconButton(
                                onClick = onUnlinkFolderClick,
                                enabled = !isImporting,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x22FFFFFF))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOff,
                                    contentDescription = "إلغاء ربط المجلد",
                                    tint = MutedIvory
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
