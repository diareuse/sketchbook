package lib.sketchbook.behavior

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.center
import androidx.compose.ui.test.down
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import lib.sketchbook.PaneProbe
import lib.sketchbook.SketchbookButton
import lib.sketchbook.SketchbookFilledIconButton
import lib.sketchbook.SketchbookGlassButton
import lib.sketchbook.SketchbookGlassIconButton
import lib.sketchbook.SketchbookIconButton
import lib.sketchbook.SketchbookOutlinedButton
import lib.sketchbook.SketchbookTonalButton
import lib.sketchbook.SketchbookTonalIconButton
import lib.sketchbook.StripeColors
import lib.sketchbook.StripedBackdrop
import lib.sketchbook.changedPixels
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

abstract class ButtonGlassShapeBehavior : GlassPaneBehavior() {

    @Test
    fun `filled button pane edge matches the theme button shape`() = assertPane {
        SketchbookButton({}, it) { Text("Go") }
    }

    @Test
    fun `tonal button pane edge matches the theme button shape`() = assertPane {
        SketchbookTonalButton({}, it) { Text("Go") }
    }

    @Test
    fun `outlined button pane edge matches the theme button shape`() = assertPane {
        SketchbookOutlinedButton({}, it) { Text("Go") }
    }

    @Test
    fun `glass button pane edge matches the theme button shape`() = assertPane {
        SketchbookGlassButton({}, it) { Text("Go") }
    }

    @Test
    fun `plain icon button pane edge matches the theme button shape`() = assertPane {
        SketchbookIconButton({}, "go", it) { Box(Modifier.size(24.dp)) }
    }

    @Test
    fun `filled icon button pane edge matches the theme button shape`() = assertPane {
        SketchbookFilledIconButton({}, "go", it) { Box(Modifier.size(24.dp)) }
    }

    @Test
    fun `tonal icon button pane edge matches the theme button shape`() = assertPane {
        SketchbookTonalIconButton({}, "go", it) { Box(Modifier.size(24.dp)) }
    }

    @Test
    fun `glass icon button pane edge matches the theme button shape`() = assertPane {
        SketchbookGlassIconButton({}, "go", it) { Box(Modifier.size(24.dp)) }
    }
}

@Config(sdk = [26])
class ButtonGlassShapeAndroid8BehaviorTest : ButtonGlassShapeBehavior()

@Config(sdk = [31])
class ButtonGlassShapeAndroid12BehaviorTest : ButtonGlassShapeBehavior()

@Config(sdk = [33])
class ButtonGlassShapeAndroid13BehaviorTest : ButtonGlassShapeBehavior()

abstract class ButtonPressShapeBehavior : GlassPaneBehavior() {

    @Test
    fun `filled button press highlight stays inside the pane shape`() = assertPressInside {
        SketchbookButton({}, it) { Text("Go") }
    }

    @Test
    fun `outlined button press highlight stays inside the pane shape`() = assertPressInside {
        SketchbookOutlinedButton({}, it) { Text("Go") }
    }

    @Test
    fun `plain icon button press highlight stays inside the pane shape`() = assertPressInside {
        SketchbookIconButton({}, "go", it) { Box(Modifier.size(24.dp)) }
    }

    @Test
    fun `glass icon button press highlight stays inside the pane shape`() = assertPressInside {
        SketchbookGlassIconButton({}, "go", it) { Box(Modifier.size(24.dp)) }
    }
}

@Config(sdk = [26])
class ButtonPressShapeAndroid8BehaviorTest : ButtonPressShapeBehavior()

@Config(sdk = [33])
class ButtonPressShapeAndroid13BehaviorTest : ButtonPressShapeBehavior()
