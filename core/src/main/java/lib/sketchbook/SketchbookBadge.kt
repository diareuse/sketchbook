package lib.sketchbook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private val DotSize = 8.dp
private const val DefaultMaxCount = 99

internal fun badgeLabel(count: Int, max: Int): String = if (count > max) "$max+" else count.toString()

@Composable
fun SketchbookBadge(
    modifier: Modifier = Modifier,
    containerColor: Color = Theme.color.emphasis.error
) {
    Box(modifier.size(DotSize).background(containerColor, CircleShape))
}

@Composable
fun SketchbookBadge(
    count: Int,
    modifier: Modifier = Modifier,
    max: Int = DefaultMaxCount,
    containerColor: Color = Theme.color.emphasis.error,
    contentColor: Color = Theme.color.contentColorFor(containerColor)
) {
    require(max >= 1) { "max must be at least 1" }
    if (count <= 0) return
    Badge(modifier = modifier, containerColor = containerColor, contentColor = contentColor) {
        Text(badgeLabel(count, max))
    }
}

@Composable
fun SketchbookBadgedBox(
    badge: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) = BadgedBox(badge = badge, modifier = modifier, content = content)

@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookBadgePreview() = SketchbookPreviewLayout {
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        SketchbookBadge()
        SketchbookBadge(count = 7)
        SketchbookBadge(count = 250)
        SketchbookBadgedBox(badge = { SketchbookBadge(count = 3) }) {
            Icon(Icons.Default.Notifications, contentDescription = null)
        }
    }
}
