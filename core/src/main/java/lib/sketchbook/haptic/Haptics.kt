package lib.sketchbook.haptic

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.hapticfeedback.*
import androidx.compose.ui.node.*
import androidx.compose.ui.platform.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun HapticFeedback.click() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    }
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
    var last by remember(key) { mutableStateOf(value) }
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
    var last by remember(key) { mutableStateOf(value) }
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

fun Modifier.hapticIndication(interactionSource: InteractionSource) = composed {
    HapticIndicationModifier(interactionSource, LocalHapticFeedback.current)
}

@Composable
fun rememberMutableInteractionSource(): MutableInteractionSourceState = remember {
    MutableInteractionSourceState()
}

class MutableInteractionSourceState {
    private val sources = arrayOfNulls<MutableInteractionSource>(12)

    private fun source(index: Int): MutableInteractionSource =
        sources[index] ?: MutableInteractionSource().also { sources[index] = it }

    operator fun component1(): MutableInteractionSource = source(0)

    operator fun component2(): MutableInteractionSource = source(1)

    operator fun component3(): MutableInteractionSource = source(2)

    operator fun component4(): MutableInteractionSource = source(3)

    operator fun component5(): MutableInteractionSource = source(4)

    operator fun component6(): MutableInteractionSource = source(5)

    operator fun component7(): MutableInteractionSource = source(6)

    operator fun component8(): MutableInteractionSource = source(7)

    operator fun component9(): MutableInteractionSource = source(8)

    operator fun component10(): MutableInteractionSource = source(9)

    operator fun component11(): MutableInteractionSource = source(10)

    operator fun component12(): MutableInteractionSource = source(11)
}

class HapticIndicationModifier(
    private val interactionSource: InteractionSource,
    private val haptics: HapticFeedback
) : ModifierNodeElement<VibrateNode>() {

    override fun create(): VibrateNode = VibrateNode(interactionSource, haptics)

    override fun update(node: VibrateNode) {
        node.interactionSource = interactionSource
        node.haptics = haptics
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HapticIndicationModifier

        if (interactionSource != other.interactionSource) return false
        if (haptics != other.haptics) return false

        return true
    }

    override fun hashCode(): Int {
        var result = interactionSource.hashCode()
        result = 31 * result + haptics.hashCode()
        return result
    }

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
    interactionSource: InteractionSource,
    var haptics: HapticFeedback
) : Modifier.Node() {

    var interactionSource: InteractionSource = interactionSource
        set(value) {
            if (field == value) return
            field = value
            collect()
        }

    private var collecting: Job? = null

    override fun onAttach() {
        collect()
    }

    private fun collect() {
        collecting?.cancel()
        if (!isAttached) return
        collecting = coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Press) haptics.tick()
            }
        }
    }
}
