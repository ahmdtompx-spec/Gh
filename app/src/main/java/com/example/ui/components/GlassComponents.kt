package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.GlassBorderBright
import com.example.ui.theme.GlassBorderDim
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.GlassEmeraldFillBottom
import com.example.ui.theme.GlassEmeraldFillTop
import com.example.ui.theme.GlassFillBottom
import com.example.ui.theme.GlassFillTop
import com.example.ui.theme.MutedIvory
import com.example.ui.theme.SoftGold
import com.example.ui.theme.WarmIvory

/**
 * Multi-layered frosted glass card with specular highlight rim,
 * subtle emerald tint, and optional Arabesque filigree corners.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    borderAccent: Color = GlassBorderGold,
    showArabesqueCorners: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .shadow(
                elevation = 14.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.45f),
                spotColor = Color.Black.copy(alpha = 0.45f)
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GlassEmeraldFillTop, GlassEmeraldFillBottom)
                )
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(GlassFillTop, GlassFillBottom, Color.Transparent),
                    start = Offset(0f, 0f),
                    end = Offset(600f, 600f)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GlassBorderBright,
                        borderAccent,
                        GlassBorderDim
                    )
                ),
                shape = shape
            )
            .drawBehind {
                // Top specular glass highlight edge
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.42f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(size.width * 0.08f, 1.dp.toPx()),
                    end = Offset(size.width * 0.92f, 1.dp.toPx()),
                    strokeWidth = 1.2.dp.toPx()
                )
            }
    ) {
        if (showArabesqueCorners) {
            ArabesqueCornerFiligree(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp),
                flipHorizontal = false
            )
            ArabesqueCornerFiligree(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
                flipHorizontal = true
            )
        }
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun GlassSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) AntiqueGold else GlassBorderBright,
        label = "search_border"
    )
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x36FFFFFF), Color(0x18FFFFFF))
                )
            )
            .border(width = 1.dp, color = borderColor, shape = shape)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = placeholder,
            tint = if (isFocused) SoftGold else MutedIvory,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedIvory.copy(alpha = 0.78f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = WarmIvory),
                cursorBrush = SolidColor(AntiqueGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused }
                    .testTag("library_search_input")
            )
        }
        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("clear_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "مسح البحث",
                    tint = MutedIvory
                )
            }
        }
    }
}

@Composable
fun GlassFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = AntiqueGold
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip_scale"
    )
    val bgTop = if (selected) accentColor.copy(alpha = 0.30f) else Color(0x20FFFFFF)
    val bgBottom = if (selected) accentColor.copy(alpha = 0.14f) else Color(0x0DFFFFFF)
    val border = if (selected) accentColor.copy(alpha = 0.85f) else GlassBorderDim
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .minimumInteractiveComponentSize()
            .clip(shape)
            .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
            .border(1.dp, border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) WarmIvory else MutedIvory,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

data class IqraNavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun IqraGlassBottomBar(
    destinations: List<IqraNavDestination>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        val barShape = RoundedCornerShape(26.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, barShape, ambientColor = Color.Black, spotColor = Color.Black)
                .clip(barShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xDD143027), Color(0xEE0B1D17))
                    )
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x26FFFFFF), Color(0x08FFFFFF))
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(GlassBorderBright, GlassBorderGold, GlassBorderDim)
                    ),
                    shape = barShape
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinations.forEach { dest ->
                val isSelected = currentRoute == dest.route
                val itemScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "nav_scale_${dest.route}"
                )
                val pillColor by animateColorAsState(
                    targetValue = if (isSelected) AntiqueGold.copy(alpha = 0.22f) else Color.Transparent,
                    label = "nav_pill_${dest.route}"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .scale(itemScale)
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(18.dp))
                        .background(pillColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onNavigate(dest.route) }
                        )
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .testTag(dest.testTag),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                        contentDescription = dest.label,
                        tint = if (isSelected) SoftGold else MutedIvory.copy(alpha = 0.8f),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dest.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) WarmIvory else MutedIvory.copy(alpha = 0.8f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun IqraGlassNavRail(
    destinations: List<IqraNavDestination>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier
            .border(width = 1.dp, color = GlassBorderDim),
        containerColor = Color(0xCC0D201A),
        contentColor = WarmIvory
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        destinations.forEach { dest ->
            val selected = currentRoute == dest.route
            NavigationRailItem(
                selected = selected,
                onClick = { onNavigate(dest.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                        contentDescription = dest.label
                    )
                },
                label = {
                    Text(
                        text = dest.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = SoftGold,
                    selectedTextColor = WarmIvory,
                    indicatorColor = AntiqueGold.copy(alpha = 0.22f),
                    unselectedIconColor = MutedIvory,
                    unselectedTextColor = MutedIvory
                ),
                modifier = Modifier.testTag(dest.testTag)
            )
        }
    }
}
