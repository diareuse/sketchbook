package lib.sketchbook.haptic

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.hapticfeedback.*
import androidx.compose.ui.node.*
import androidx.compose.ui.platform.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
fun rememberVibrateIndication(): Indication {
    val feedback = LocalHapticFeedback.current
    return remember(feedback) { VibrateIndicationNodeFactory(feedback) }
}

class VibrateIndicationNodeFactory(
    private val feedback: HapticFeedback
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return VibrateNode(interactionSource, feedback)
    }

    override fun equals(other: Any?): Boolean = other === this

    override fun hashCode(): Int = -1
}

class VibrateNode(
    private val interactionSource: InteractionSource,
    private val feedback: HapticFeedback
) : Modifier.Node() {
    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> feedback.tick()
                    is PressInteraction.Release -> feedback.fastTick()
                    is PressInteraction.Cancel -> feedback.reject()
                }
            }
        }
    }
}