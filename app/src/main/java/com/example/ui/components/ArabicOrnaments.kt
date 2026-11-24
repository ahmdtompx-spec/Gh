package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepEmeraldSurface
import com.example.ui.theme.EmeraldObsidian
import com.example.ui.theme.LanternAmber
import com.example.ui.theme.SoftGold
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Atmospheric nocturnal library background with subtle animated lantern glow
 * and soft floating light motes so translucent glass cards refract rich depth.
 */
@Composable
fun AmbientLibraryBackdrop(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "lantern_ambient")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lantern_pulse"
    )
    val drift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mote_drift"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_library_ambience_1795506707780),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.24f,
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        EmeraldObsidian.copy(alpha = 0.86f),
                        DeepEmeraldSurface.copy(alpha = 0.78f),
                        EmeraldObsidian.copy(alpha = 0.94f)
                    )
                )
            )

            // Warm top-right lantern radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        LanternAmber.copy(alpha = 0.16f * pulse),
                        AntiqueGold.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.85f, size.height * 0.12f),
                    radius = size.maxDimension * 0.48f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.12f),
                radius = size.maxDimension * 0.48f
            )

            // Cool emerald ambient glow bottom-left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E5F4E).copy(alpha = 0.22f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.15f, size.height * 0.78f),
                    radius = size.maxDimension * 0.45f
                ),
                center = Offset(size.width * 0.15f, size.height * 0.78f),
                radius = size.maxDimension * 0.45f
            )

            // Subtle drifting golden dust motes
            val motes = listOf(0.18f, 0.34f, 0.56f, 0.74f, 0.88f)
            motes.forEachIndexed { index, xRatio ->
                val yRatio = ((index * 0.23f + drift) % 1.0f)
                val x = size.width * xRatio + sin(drift * 2f * PI.toFloat() + index) * 18f
                val y = size.height * (1f - yRatio)
                val radius = (2.2f + (index % 3) * 1.1f)
                drawCircle(
                    color = SoftGold.copy(alpha = 0.18f),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }
    }
}

/**
 * Light geometric Arabesque corner filigree drawn cleanly in antique gold.
 */
@Composable
fun ArabesqueCornerFiligree(
    modifier: Modifier = Modifier,
    color: Color = AntiqueGold.copy(alpha = 0.32f),
    flipHorizontal: Boolean = false
) {
    Canvas(modifier = modifier.size(44.dp)) {
        val w = size.width
        val h = size.height
        fun px(x: Float): Float = if (flipHorizontal) w - x else x

        val outerPath = Path().apply {
            moveTo(px(2f), h * 0.85f)
            lineTo(px(2f), 2f)
            lineTo(px(w * 0.85f), 2f)
        }
        drawPath(outerPath, color = color, style = Stroke(width = 1.4.dp.toPx()))

        val archPath = Path().apply {
            moveTo(px(8f), h * 0.55f)
            quadraticTo(px(8f), 8f, px(w * 0.55f), 8f)
        }
        drawPath(archPath, color = color.copy(alpha = color.alpha * 0.75f), style = Stroke(width = 1.dp.toPx()))

        // Small geometric diamond knot
        val cx = px(13f)
        val cy = 13f
        val r = 4.dp.toPx()
        val diamond = Path().apply {
            moveTo(cx, cy - r)
            lineTo(cx + r, cy)
            lineTo(cx, cy + r)
            lineTo(cx - r, cy)
            close()
        }
        drawPath(diamond, color = color, style = Stroke(width = 1.dp.toPx()))
    }
}

/**
 * Refined Arabic horizontal divider featuring an 8-pointed Khatam star.
 */
@Composable
fun IslamicStarDivider(
    modifier: Modifier = Modifier,
    starColor: Color = AntiqueGold
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
        ) {
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, starColor.copy(alpha = 0.45f))
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }

        Canvas(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(16.dp)
        ) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val rOuter = size.minDimension / 2f
            val rInner = rOuter * 0.48f
            val path = Path()
            for (i in 0 until 16) {
                val angle = (i * 22.5) * PI / 180.0
                val rad = if (i % 2 == 0) rOuter else rInner
                val x = c.x + (rad * cos(angle)).toFloat()
                val y = c.y + (rad * sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, color = starColor.copy(alpha = 0.75f), style = Stroke(width = 1.1.dp.toPx()))
            drawCircle(color = starColor.copy(alpha = 0.65f), radius = 1.8.dp.toPx(), center = c)
        }

        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
        ) {
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(starColor.copy(alpha = 0.45f), Color.Transparent)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

/**
 * 8-pointed Arabic geometric medallion for each author section.
 */
@Composable
fun AuthorKhatamSeal(
    symbol: String,
    accentColor: Color,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val half = this.size.minDimension / 2f
            val center = Offset(half, half)
            val squareRadius = half * 0.78f

            // Two rotated squares forming an 8-pointed Rub el Hizb star
            for (offsetAngle in listOf(0.0, 45.0)) {
                val path = Path()
                for (i in 0 until 4) {
                    val angle = (offsetAngle + 45.0 + i * 90.0) * PI / 180.0
                    val x = center.x + (squareRadius * cos(angle)).toFloat()
                    val y = center.y + (squareRadius * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(
                    path = path,
                    color = accentColor.copy(alpha = 0.22f)
                )
                drawPath(
                    path = path,
                    color = accentColor.copy(alpha = 0.72f),
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }
        }
        Text(
            text = symbol,
            fontFamily = AmiriFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.42f).sp,
            color = accentColor
        )
    }
}
