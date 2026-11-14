package lib.sketchbook.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.captureScreenRoboImage
import org.junit.Rule
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
abstract class ScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    protected fun captureRoot(name: String, content: @Composable () -> Unit) {
        composeRule.setContent(content)
        composeRule.waitForIdle()
        composeRule.onRoot().captureRoboImage(path(name))
    }

    protected fun captureScreen(name: String, content: @Composable () -> Unit) {
        composeRule.setContent(content)
        composeRule.waitForIdle()
        captureScreenRoboImage(path(name))
    }

    private fun path(name: String) = "src/test/snapshots/$name.png"

    companion object {
        const val Light = "w360dp-h640dp-normal-notnight-xhdpi"
        const val Dark = "w360dp-h640dp-normal-night-xhdpi"
    }
}
