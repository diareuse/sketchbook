package lib.sketchbook.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.captureScreenRoboImage
import com.github.takahirom.roborazzi.roborazziSystemPropertyOutputDirectory
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
abstract class ScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val options = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0.01f)
    )

    protected fun captureRoot(
        name: String,
        ready: () -> Boolean = { true },
        animated: Boolean = false,
        content: @Composable () -> Unit
    ) {
        if (animated) composeRule.mainClock.autoAdvance = false
        settle(ready, content)
        composeRule.onRoot().captureRoboImage(file(name), roborazziOptions = options)
    }

    protected fun captureScreen(
        name: String,
        ready: () -> Boolean = { true },
        content: @Composable () -> Unit
    ) {
        settle(ready, content)
        captureScreenRoboImage(file(name).path, options)
    }

    private fun settle(ready: () -> Boolean, content: @Composable () -> Unit) {
        composeRule.setContent(content)
        composeRule.waitUntil(timeoutMillis = 10_000, condition = ready)
        composeRule.mainClock.autoAdvance = false
        composeRule.mainClock.advanceTimeBy(SettleMillis)
        composeRule.mainClock.advanceTimeBy(FrameMillis)
        composeRule.waitForIdle()
    }

    private fun file(name: String) = File(roborazziSystemPropertyOutputDirectory(), "$name.png")

    companion object {
        const val Light = "w360dp-h640dp-normal-notnight-xhdpi"
        const val Dark = "w360dp-h640dp-normal-night-xhdpi"
        private const val SettleMillis = 2_000L
        private const val FrameMillis = 100L
    }
}
