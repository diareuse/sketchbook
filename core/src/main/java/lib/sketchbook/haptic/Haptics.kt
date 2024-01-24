package lib.sketchbook.haptic

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.ViewConfiguration
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

fun HapticFeedback.click() {
    performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
}

fun HapticFeedback.tick() {
    performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
}

fun HapticFeedback.fastTick() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
    } else {
        performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }
}

fun HapticFeedback.performHapticFeedback(type: Int) {
    performHapticFeedback(HapticFeedbackType(type))
}

fun HapticFeedback.confirm() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }
}

fun HapticFeedback.reject() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        performHapticFeedback(HapticFeedbackConstants.REJECT)
    }
}

@Composable
fun <T> TickOnChange(value: T, key: Any? = Unit) {
    var last by rememberSaveable(key) { mutableStateOf(value) }
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(value) {
        if (last != value) {
            haptics.tick()
            last = value
        }
    }
}

@Composable
fun <T> ClickOnChange(value: T, key: Any? = Unit) {
    var last by rememberSaveable(key) { mutableStateOf(value) }
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(value) {
        if (last != value) {
            haptics.click()
            last = value
        }
    }
}

@Composable
fun rememberVibrateIndication(indication: Indication = rememberRipple()): Indication {
    return remember { VibrateIndication(indication) }
}

class VibrateIndication(
    private val origin: Indication
) : Indication {

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val haptic = LocalHapticFeedback.current
        val timeout = ViewConfiguration.getLongPressTimeout().toLong()
        LaunchedEffect(interactionSource) {
            var lastPress = 0L
            interactionSource.interactions.collect {
                when (it) {
                    is PressInteraction.Press -> {
                        lastPress = System.currentTimeMillis()
                        haptic.fastTick()
                    }

                    is PressInteraction.Release -> {
                        if (timeout > System.currentTimeMillis() - lastPress)
                            haptic.click()
                    }
                }
            }
        }
        return origin.rememberUpdatedInstance(interactionSource)
    }

}