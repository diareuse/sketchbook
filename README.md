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
- Effects and backgrounds: animated background, glass border, border beam, divider
- Existing: color slider, rocker counter, field row, image

## Theming and glass

`Theme` provides colors, typography, shapes, the glass renderer, a haptic feedback implementation and a press indication (AGSL highlight on API 33+, ripple below) that is purely visual. Pass `dynamicColor = true` to use the system color scheme.

Glass samples a source registered with `Modifier.glassSource()`. Without a source, or inside dialogs, menus, sheets and tooltips, glass falls back to a tint. With a source the renderer depends on the API level:

| API | Glass |
| --- | --- |
| 33+ | Refraction, blur, tint and specular rim |
| 31-32 | Blur and tint |
| 26-30 | Tint only |

Glass components clip, press and rim with one Haze `RoundedCornerShape` carrying the same corner sizes as the theme shape, so every layer of a control matches on all API levels. Glass controls are at least 48dp.

`SketchbookScaffold` registers its content as the glass source for its bars, so glass placed inside the scaffold content itself does not refract; place a separate `glassSource()` sibling or use glass in the bars.

## Border beam

`Modifier.borderBeam` draws an animated multi-colour gradient comet along the outline of any shape and works on any composable.

```kotlin
Box(
    Modifier
        .size(160.dp)
        .borderBeam(
            shape = RoundedCornerShape(24.dp),
            colors = BeamPalette.colorful,
            style = BeamStyle.Large,
            direction = BeamDirection.Clockwise
        )
)
```

Below API 33 the beam falls back to stroked path segments.

## Haptics

Changing a value (switch, checkbox, radio button, segmented button, filter chip) performs the platform confirm feedback (toggle on or off on API 34+; below that, a click when turning on and a tick when turning off); clicking a button, icon button or floating action button performs a context click. Pressing a disabled control performs a reject (denial) feedback, falling back to a long press below API 30. Pressing down alone does not vibrate.


## Catalog and tests

The `:catalog` module is a sample app listing every component by group, with one detail screen each.

```sh
./gradlew :catalog:installDebug
./gradlew :catalog:testDebugUnitTest
./gradlew :core:testDebugUnitTest :core:verifyRoborazziDebug :core:apiCheck
```

JDK 21 is required. Screenshots are re-recorded with `./gradlew :core:recordRoborazziDebug`.

See [MIGRATION.md](MIGRATION.md) for upgrading to 2.0.0.
