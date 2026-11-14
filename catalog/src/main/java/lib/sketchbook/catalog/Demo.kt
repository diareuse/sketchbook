package lib.sketchbook.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
        content()
    }
}
