package lib.sketchbook

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.input.VisualTransformation
import lib.sketchbook.theme.Theme
import lib.sketchbook.theme.contentColorFor

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
) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = label,
        enabled = enabled,
        placeholder = placeholder,
        supportingText = supportingText,
        maxLines = 1,
        singleLine = singleLine,
        isError = isError,
        readOnly = readOnly,
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Theme.color.contentColorFor(compositeColor),
            unfocusedTextColor = Theme.color.contentColorFor(compositeColor).copy(.8f),
            disabledTextColor = Theme.color.contentColorFor(compositeColor).copy(.5f),
            focusedLabelColor = Theme.color.contentColorFor(compositeColor),
            unfocusedLabelColor = Theme.color.contentColorFor(compositeColor).copy(.8f),
            disabledLabelColor = Theme.color.contentColorFor(compositeColor).copy(.5f),
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Theme.color.overlay.background.copy(.1f)
                .compositeOver(compositeColor),
            focusedContainerColor = Theme.color.overlay.background.copy(.15f)
                .compositeOver(compositeColor),
            errorContainerColor = Theme.color.overlay.error.copy(.3f).compositeOver(compositeColor),
            disabledContainerColor = Theme.color.overlay.background.copy(.05f)
                .compositeOver(compositeColor)
        ),
        shape = Theme.container.button,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

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