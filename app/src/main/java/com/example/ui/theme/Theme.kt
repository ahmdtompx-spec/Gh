package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private val IqraDarkColorScheme = darkColorScheme(
    primary = AntiqueGold,
    onPrimary = EmeraldObsidian,
    primaryContainer = EmeraldCardTint,
    onPrimaryContainer = SoftGold,
    secondary = LanternAmber,
    onSecondary = EmeraldObsidian,
    secondaryContainer = WalnutMedium,
    onSecondaryContainer = WarmIvory,
    tertiary = SoftGold,
    onTertiary = EmeraldObsidian,
    background = EmeraldObsidian,
    onBackground = WarmIvory,
    surface = DeepEmeraldSurface,
    onSurface = WarmIvory,
    surfaceVariant = EmeraldCardTint,
    onSurfaceVariant = MutedIvory,
    outline = GlassBorderGold,
    outlineVariant = GlassBorderDim
)

val IqraShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun IqraTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = IqraDarkColorScheme,
            typography = IqraTypography,
            shapes = IqraShapes,
            content = content
        )
    }
}
