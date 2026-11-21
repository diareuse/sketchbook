package lib.sketchbook.behavior

import lib.sketchbook.shader.animationsAllowed
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimationPolicyTest {

    @Test
    fun `animations are allowed at the default scale`() = assertTrue(animationsAllowed(1f))

    @Test
    fun `animations are allowed at a stretched scale`() = assertTrue(animationsAllowed(10f))

    @Test
    fun `animations are denied when the scale is zero`() = assertFalse(animationsAllowed(0f))

    @Test
    fun `animations are denied for a negative scale`() = assertFalse(animationsAllowed(-1f))
}
