package lib.sketchbook.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle

internal class ThemeTextMaterial3(
    private val typography: Typography
) : Theme.Text {

    override val caption: TextStyle
        get() = typography.bodySmall
    override val body: TextStyle
        get() = typography.bodyMedium
    override val emphasis: TextStyle
        get() = typography.bodyLarge
    override val title: TextStyle
        get() = typography.titleLarge
    override val display: TextStyle
        get() = typography.displaySmall
    override val headline: TextStyle
        get() = typography.titleMedium

}