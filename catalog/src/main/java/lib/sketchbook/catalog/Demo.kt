package lib.sketchbook.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.unit.*
import lib.sketchbook.SketchbookAnimatedBackground
import lib.sketchbook.theme.Theme

internal enum class Group(val title: String, val short: String, val icon: ImageVector) {
    Actions("Actions & inputs", "Actions", Icons.Default.Build),
    Containers("Containers & navigation", "Containers", Icons.Default.Home),
    Feedback("Feedback & status", "Feedback", Icons.Default.Info),
    Effects("Effects & backgrounds", "Effects", Icons.Default.Star),
    Existing("Existing components", "Existing", Icons.Default.Settings)
}

internal class Demo(
    val group: Group,
    val title: String,
    val content: @Composable ColumnScope.() -> Unit
)

internal val Catalog: List<Demo> = ActionDemos + ContainerDemos + FeedbackDemos + EffectDemos + ExistingDemos

@Composable
internal fun Showcase(label: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = Theme.textStyle.caption)
        SketchbookAnimatedBackground(modifier = Modifier
            .fillMaxSize()
            .clip(Theme.container.card)
            .background(MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
        }
    }
}

private val Backdrop = listOf(Color(0xFFE53935), Color(0xFFFDD835), Color(0xFF43A047), Color(0xFF1E88E5), Color(0xFF8E24AA))
