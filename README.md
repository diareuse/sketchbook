# Sketchbook

Jetpack Compose design system with a glass look: Material 3 components restyled through a single `Theme`, translucent glass surfaces, an animated mesh background and haptic feedback.

## Install

```kotlin
dependencies {
    implementation("dia.sketchbook:compose:<version>")
}
```

Requires `minSdk 26`. The library module is `:core` (package `lib.sketchbook`).

## Usage

```kotlin
import lib.sketchbook.util.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(Modifier.fillMaxSize()) {
                SketchbookAnimatedBackground(Modifier.fillMaxSize().glassSource()) {}
                SketchbookButton(onClick = {}) { Text("Hello") }
            }
        }
    }
}
```

`lib.sketchbook.util.setContent` enables edge-to-edge and wraps the content in `Theme`. Elsewhere, wrap content in `Theme { }` yourself.

## Components

- Actions and inputs: buttons (filled, tonal, outlined, glass), icon buttons, floating action buttons, chips, checkbox, radio button, segmented buttons, switch, slider, text field, search bar
- Containers and navigation: card, list item, tab row, navigation bar, navigation rail, dialog, bottom sheet, menu, top app bar, scaffold
- Feedback and status: badge, progress, skeleton, snackbar, toast, tooltip, empty state, pull to refresh, pager indicator
- Effects and backgrounds: animated background, glass border, divider
- Existing: color slider, rocker counter, field row, image

## Theming and glass

`Theme` provides colors, typography, shapes, the glass renderer, a haptic feedback implementation and a press indication (AGSL highlight on API 33+, ripple below) that ticks once on press. Pass `dynamicColor = true` to use the system color scheme.

Glass samples a source registered with `Modifier.glassSource()`. Without a source, or inside dialogs, menus, sheets and tooltips, glass falls back to a tint. With a source the renderer depends on the API level:

| API | Glass |
| --- | --- |
| 33+ | Refraction, blur, tint and specular rim |
| 31-32 | Blur and tint |
| 26-30 | Tint only |

`SketchbookScaffold` registers its content as the glass source for its bars, so glass placed inside the scaffold content itself does not refract; place a separate `glassSource()` sibling or use glass in the bars.

## Catalog and tests

The `:catalog` module is a sample app listing every component by group, with one detail screen each.

```sh
./gradlew :catalog:installDebug
./gradlew :catalog:testDebugUnitTest
./gradlew :core:testDebugUnitTest :core:verifyRoborazziDebug :core:apiCheck
```

JDK 21 is required. Screenshots are re-recorded with `./gradlew :core:recordRoborazziDebug`.

See [MIGRATION.md](MIGRATION.md) for upgrading to 2.0.0.
