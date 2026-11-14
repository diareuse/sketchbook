package lib.sketchbook.behavior

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lib.sketchbook.util.rememberSaveable
import org.junit.Assert.assertEquals
import org.junit.Test

class SaverBehaviorTest : BehaviorTest() {

    @Test
    fun `saveable geometry and color states survive state restoration`() {
        val tester = StateRestorationTester(composeRule)
        lateinit var dp: MutableState<androidx.compose.ui.unit.Dp>
        lateinit var sp: MutableState<androidx.compose.ui.unit.TextUnit>
        lateinit var dpOffset: MutableState<DpOffset>
        lateinit var offset: MutableState<Offset>
        lateinit var intOffset: MutableState<IntOffset>
        lateinit var color: MutableState<Color>
        tester.setContent {
            dp = rememberSaveable(1.dp, key = "dp")
            sp = rememberSaveable(1.sp, key = "sp")
            dpOffset = rememberSaveable(DpOffset(1.dp, 1.dp), key = "dpOffset")
            offset = rememberSaveable(Offset(1f, 1f), key = "offset")
            intOffset = rememberSaveable(key = "intOffset", init = IntOffset(1, 1))
            color = rememberSaveable(Color.Black, key = "color")
        }
        composeRule.runOnIdle {
            dp.value = 5.dp
            sp.value = 6.sp
            dpOffset.value = DpOffset(7.dp, 8.dp)
            offset.value = Offset(9f, 10f)
            intOffset.value = IntOffset(11, 12)
            color.value = Color(0xFF123456)
        }

        tester.emulateSavedInstanceStateRestore()

        composeRule.runOnIdle {
            assertEquals(5.dp, dp.value)
            assertEquals(6.sp, sp.value)
            assertEquals(DpOffset(7.dp, 8.dp), dpOffset.value)
            assertEquals(Offset(9f, 10f), offset.value)
            assertEquals(IntOffset(11, 12), intOffset.value)
            assertEquals(Color(0xFF123456), color.value)
        }
    }
}
