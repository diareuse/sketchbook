package lib.sketchbook

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import lib.sketchbook.glass.asGlassShape
import lib.sketchbook.glass.glass
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

private const val UnfocusedTint = .75f
private const val FocusedTint = .85f
private const val DisabledTint = .5f
private const val GlassTint = .8f
private const val GlassDisabledTint = .5f
private const val ErrorOverlay = .3f
private val GlassElevation = 8.dp

private interface TextFieldContainer {
    @Composable
    fun shape(): Shape

    @Composable
    fun colors(compositeColor: Color): TextFieldColors

    @Composable
    fun Modifier.surface(compositeColor: Color, enabled: Boolean, isError: Boolean): Modifier
}

private object TintedTextFieldContainer : TextFieldContainer {
    @Composable
    override fun shape(): Shape = Theme.container.button

    @Composable
    override fun colors(compositeColor: Color): TextFieldColors = textFieldColors(
        compositeColor,
        unfocused = Theme.color.overlay.background.copy(.06f).compositeOver(compositeColor.copy(UnfocusedTint)),
        focused = Theme.color.overlay.background.copy(.1f).compositeOver(compositeColor.copy(FocusedTint)),
        error = Theme.color.overlay.error.copy(.3f).compositeOver(compositeColor.copy(UnfocusedTint)),
        disabled = Theme.color.overlay.background.copy(.03f).compositeOver(compositeColor.copy(DisabledTint))
    )

    @Composable
    override fun Modifier.surface(compositeColor: Color, enabled: Boolean, isError: Boolean): Modifier = this
}

private object GlassTextFieldContainer : TextFieldContainer {
    @Composable
    override fun shape(): Shape = Theme.container.button.asGlassShape()

    @Composable
    override fun colors(compositeColor: Color): TextFieldColors = textFieldColors(
        compositeColor,
        unfocused = Color.Transparent,
        focused = Color.Transparent,
        error = Color.Transparent,
        disabled = Color.Transparent
    )

    @Composable
    override fun Modifier.surface(compositeColor: Color, enabled: Boolean, isError: Boolean): Modifier {
        val tint = if (isError) Theme.color.overlay.error.copy(ErrorOverlay).compositeOver(compositeColor) else compositeColor
        return glass(
            shape(),
            GlassElevation,
            tint = tint,
            tintStrength = if (enabled) GlassTint else GlassDisabledTint
        )
    }
}

@Composable
private fun textFieldColors(
    compositeColor: Color,
    unfocused: Color,
    focused: Color,
    error: Color,
    disabled: Color
): TextFieldColors {
    val content = Theme.color.contentColorFor(compositeColor)
    return TextFieldDefaults.colors(
        focusedTextColor = content,
        unfocusedTextColor = content.copy(.8f),
        disabledTextColor = content.copy(.5f),
        focusedLabelColor = content,
        unfocusedLabelColor = content.copy(.8f),
        disabledLabelColor = content.copy(.5f),
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        unfocusedContainerColor = unfocused,
        focusedContainerColor = focused,
        errorContainerColor = error,
        disabledContainerColor = disabled
    )
}

@Composable
private fun TextFieldCore(
    container: TextFieldContainer,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    label: (@Composable () -> Unit)?,
    placeholder: (@Composable () -> Unit)?,
    supportingText: (@Composable () -> Unit)?,
    trailingIcon: (@Composable () -> Unit)?,
    leadingIcon: (@Composable () -> Unit)?,
    enabled: Boolean,
    readOnly: Boolean,
    singleLine: Boolean,
    isError: Boolean,
    visualTransformation: VisualTransformation,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    compositeColor: Color
) {
    TextField(
        modifier = with(container) { modifier.surface(compositeColor, enabled, isError) },
        value = value,
        onValueChange = onValueChange,
        label = label,
        enabled = enabled,
        placeholder = placeholder,
        supportingText = supportingText,
        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
        singleLine = singleLine,
        isError = isError,
        readOnly = readOnly,
        visualTransformation = visualTransformation,
        colors = container.colors(compositeColor),
        shape = container.shape(),
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

@Composable
fun SketchbookTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    compositeColor: Color = Theme.color.emphasis.background
) = TextFieldCore(
    TintedTextFieldContainer, value, onValueChange, modifier, label, placeholder, supportingText,
    trailingIcon, leadingIcon, enabled, readOnly, singleLine, isError, visualTransformation,
    keyboardOptions, keyboardActions, compositeColor
)

@Composable
internal fun SketchbookGlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    compositeColor: Color = Theme.color.emphasis.background
) = TextFieldCore(
    GlassTextFieldContainer, value, onValueChange, modifier, null, placeholder, null,
    trailingIcon, leadingIcon, enabled, false, true, false, VisualTransformation.None,
    keyboardOptions, keyboardActions, compositeColor
)

@SketchbookComponentPreview
@Composable
private fun SketchbookTextFieldPreview() = SketchbookPreviewLayout {
    SketchbookTextField("", {}, label = { Text("Label") })
}

@OptIn(ExperimentalComposeUiApi::class)
@SketchbookComponentPreview
@SketchbookValidation
@Composable
private fun SketchbookTextFieldFocusPreview() = SketchbookPreviewLayout {
    val (fr) = remember { FocusRequester.createRefs() }
    SideEffect {
        fr.requestFocus()
    }
    SketchbookTextField(
        "ABC",
        {},
        label = { Text("Label") },
        modifier = Modifier.focusRequester(fr)
    )
}