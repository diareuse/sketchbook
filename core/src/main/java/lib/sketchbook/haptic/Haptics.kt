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

fun Modifier.hapticIndication(interactionSource: InteractionSource) = composed {
    HapticIndicationModifier(interactionSource, LocalHapticFeedback.current)
}

@Composable
fun rememberMutableInteractionSource(): MutableInteractionSourceState = remember {
    MutableInteractionSourceState()
}

class MutableInteractionSourceState {
    private val sources = mutableStateMapOf<Int, MutableInteractionSource>()

    operator fun component1(): MutableInteractionSource =
        sources.getOrPut(1, ::MutableInteractionSource)

    operator fun component2(): MutableInteractionSource =
        sources.getOrPut(2, ::MutableInteractionSource)

    operator fun component3(): MutableInteractionSource =
        sources.getOrPut(3, ::MutableInteractionSource)

    operator fun component4(): MutableInteractionSource =
        sources.getOrPut(4, ::MutableInteractionSource)

    operator fun component5(): MutableInteractionSource =
        sources.getOrPut(5, ::MutableInteractionSource)

    operator fun component6(): MutableInteractionSource =
        sources.getOrPut(6, ::MutableInteractionSource)

    operator fun component7(): MutableInteractionSource =
        sources.getOrPut(7, ::MutableInteractionSource)

    operator fun component8(): MutableInteractionSource =
        sources.getOrPut(8, ::MutableInteractionSource)

    operator fun component9(): MutableInteractionSource =
        sources.getOrPut(9, ::MutableInteractionSource)

    operator fun component10(): MutableInteractionSource =
        sources.getOrPut(10, ::MutableInteractionSource)

    operator fun component11(): MutableInteractionSource =
        sources.getOrPut(11, ::MutableInteractionSource)

    operator fun component12(): MutableInteractionSource =
        sources.getOrPut(12, ::MutableInteractionSource)
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
    var interactionSource: InteractionSource,
    var haptics: HapticFeedback
) : Modifier.Node() {
    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                println("interaction $interaction")
                when (interaction) {
                    is PressInteraction.Press -> haptics.tick()
                    is PressInteraction.Release -> haptics.fastTick()
                    is PressInteraction.Cancel -> haptics.reject()
                }
            }
        }
    }
}