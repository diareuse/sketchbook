package lib.sketchbook

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import lib.sketchbook.theme.Theme

@Composable
fun RowScope.SketchbookTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    weight: Float = 1f,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    compositeColor: Color = Theme.color.emphasis.background
): Unit = SketchbookTextField(
    value,
    onValueChange,
    modifier.weight(weight),
    label,
    placeholder,
    supportingText,
    trailingIcon,
    leadingIcon,
    enabled,
    isError,
    visualTransformation,
    keyboardOptions,
    keyboardActions,
    compositeColor
)

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
        singleLine = true,
        isError = isError,
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.colors(
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

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun SketchbookTextFieldPreview() = SketchbookPreviewLayout {
    SketchbookTextField("", {}, label = { Text("Label") })
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun SketchbookTextFieldFocusPreview() = SketchbookPreviewLayout {
    val (fr) = remember { FocusRequester.createRefs() }
    SideEffect {
        fr.requestFocus()
    }
    SketchbookTextField("", {}, label = { Text("Label") }, modifier = Modifier.focusRequester(fr))
}