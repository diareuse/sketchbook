package lib.sketchbook.catalog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performScrollToNode
import android.content.Context
import android.provider.Settings
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h640dp-normal-notnight-xhdpi")
class CatalogSmokeTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun launchWithoutLoopingAnimations() {
        val resolver = ApplicationProvider.getApplicationContext<Context>().contentResolver
        Settings.Global.putFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun closeActivity() = scenario.close()

    @Test
    fun `the catalog lists a component group`() {
        composeRule.onNodeWithText("Buttons").assertIsDisplayed()
    }

    @Test
    fun `every actions and inputs component opens its detail screen`() = openEveryDemo("Actions")

    @Test
    fun `every containers and navigation component opens its detail screen`() = openEveryDemo("Containers")

    @Test
    fun `every feedback and status component opens its detail screen`() = openEveryDemo("Feedback")

    @Test
    fun `every effects and backgrounds component opens its detail screen`() = openEveryDemo("Effects")

    @Test
    fun `every existing component opens its detail screen`() = openEveryDemo("Existing")

    private fun openEveryDemo(group: String) {
        val titles = titlesOf(group)
        assertTrue(titles.isNotEmpty())
        titles.forEach { title ->
            try {
                openAndClose(group, title)
            } catch (error: AssertionError) {
                throw AssertionError("$title: ${error.message}", error)
            }
        }
    }

    private fun openAndClose(group: String, title: String) {
        composeRule.onNodeWithText(group).performClick()
        composeRule.onNodeWithTag("catalog-list").performScrollToNode(hasText(title))
        composeRule.onNodeWithText(title).performSemanticsAction(SemanticsActions.OnClick)
        composeRule.onNodeWithTag("catalog-list").assertDoesNotExist()
        assertTrue(composeRule.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty())
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithTag("catalog-list").assertIsDisplayed()
    }

    private fun titlesOf(group: String): List<String> = Catalog.filter { it.group.short == group }.map { it.title }
}
