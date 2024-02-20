package lib.sketchbook

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Light",
    group = "display",
    showSystemUi = true,
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = Devices.PIXEL_7
)
@Preview(
    name = "Dark",
    group = "display",
    showSystemUi = true,
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_7
)
annotation class SketchbookScreenPreview

@Preview(
    name = "Medium Font",
    group = "validation",
    fontScale = 1.5f
)
@Preview(
    name = "Large Font",
    group = "validation",
    fontScale = 2f
)
annotation class SketchbookValidation

@Preview(
    name = "Light",
    group = "display",
    showSystemUi = false,
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark",
    group = "display",
    showSystemUi = false,
    showBackground = true,
    backgroundColor = 0xFF000000,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class SketchbookComponentPreview