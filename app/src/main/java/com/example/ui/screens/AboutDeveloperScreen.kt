package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AuthorKhatamSeal
import com.example.ui.components.GlassCard
import com.example.ui.components.IslamicStarDivider
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.EmeraldObsidian
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.MutedIvory
import com.example.ui.theme.SoftGold
import com.example.ui.theme.WarmIvory
import kotlinx.coroutines.launch

@Composable
fun AboutDeveloperScreen(
    totalAuthors: Int,
    totalBooks: Int,
    contentBottomPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val developerName = stringResource(R.string.developer_name)
    val developerEmail = stringResource(R.string.developer_email)
    val copiedMsg = stringResource(R.string.email_copied)
    val noEmailAppMsg = stringResource(R.string.no_email_app)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 14.dp,
                    bottom = contentBottomPadding.calculateBottomPadding() + 28.dp
                )
                .testTag("about_developer_screen"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // App Showcase Glass Card
            GlassCard(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxWidth(),
                cornerRadius = 26.dp,
                borderAccent = GlassBorderGold,
                showArabesqueCorners = true,
                contentPadding = PaddingValues(22.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.iqra_icon_fg_1795506706035),
                        contentDescription = stringResource(R.string.app_name),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(86.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .border(1.5.dp, AntiqueGold.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        color = WarmIvory
                    )
                    Text(
                        text = stringResource(R.string.library_subtitle),
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftGold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    IslamicStarDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "تجربة مكتبة عربية رقمية تحاكي رفوف المكتبة الواقعية بتأثيرات الزجاج الشفاف والزخارف العربية الهادئة، حيث يضم كل مؤلف قسماً مستقلاً ورفاً يعرض مؤلفاته.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = WarmIvory.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatBadge(title = "أقسام المؤلفين", value = "$totalAuthors", modifier = Modifier.weight(1f))
                        StatBadge(title = "الكتب المتاحة", value = "$totalBooks", modifier = Modifier.weight(1f))
                    }
                }
            }

            // Developer Contact Glass Card (المهندس أحمد إسماعيل)
            GlassCard(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxWidth()
                    .testTag("developer_contact_card"),
                cornerRadius = 26.dp,
                borderAccent = AntiqueGold.copy(alpha = 0.7f),
                showArabesqueCorners = true,
                contentPadding = PaddingValues(22.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(AntiqueGold.copy(alpha = 0.2f))
                                .border(1.dp, AntiqueGold.copy(alpha = 0.55f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Engineering,
                                contentDescription = null,
                                tint = SoftGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.developer_section_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = SoftGold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    AuthorKhatamSeal(
                        symbol = "أ",
                        accentColor = AntiqueGold,
                        size = 64.dp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = developerName,
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = WarmIvory,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = developerEmail,
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("developer_email_text")
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    IslamicStarDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$developerEmail")
                                putExtra(Intent.EXTRA_SUBJECT, "التواصل بخصوص تطبيق مكتبة اقرأ")
                            }
                            runCatching {
                                context.startActivity(intent)
                            }.onFailure {
                                clipboard.setText(AnnotatedString(developerEmail))
                                scope.launch { snackbarHostState.showSnackbar(noEmailAppMsg) }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("email_developer_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AntiqueGold,
                            contentColor = EmeraldObsidian
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.contact_developer),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(developerEmail))
                            scope.launch { snackbarHostState.showSnackbar(copiedMsg) }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("copy_email_button"),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderGold),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = WarmIvory
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = SoftGold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.copy_email),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = contentBottomPadding.calculateBottomPadding() + 12.dp)
        )
    }
}

@Composable
private fun StatBadge(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x20FFFFFF))
            .border(1.dp, Color(0x36FFFFFF), RoundedCornerShape(16.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontFamily = AmiriFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = SoftGold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MutedIvory
        )
    }
}
