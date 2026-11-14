# Migrating to 2.0.0

## Breaking changes

- `minSdk` is 26 (was 21).
- Image loading uses Coil 3 (was Coil 2). Replace `coil.compose.*` types with `coil3.compose.*` where you use them with `lib.sketchbook.Image`; the Coil artifacts are `io.coil-kt.coil3:*`.
- `Theme` provides a glass press indication through `LocalIndication`, replacing the haptic-only indication. Clickable content under `Theme` shows an AGSL press highlight on API 33+ and a ripple below, and ticks once on press.
- The press-down tick is removed. Haptics now confirm a change (toggle on or off for switches, checkboxes, radio buttons, segmented buttons and filter chips; context click for buttons, icon buttons and floating action buttons) and deny a press on a disabled control with a reject feedback.
- Typography uses the true font weights (Medium, SemiBold, Bold, ExtraBold map to their own Sora files); text previously rendered with shifted weights looks different.
- The `key` parameter of the `rememberSaveable` helpers (`Dp`, `TextUnit`, `DpOffset`, `Offset`, `IntOffset`, `Color`) is ignored; saved state is keyed by position and `inputs`. `TickOnChange` and `ClickOnChange` no longer save state across process death.
- `SketchbookTopAppBar` counts its content padding once, so the default bar is 12dp shorter.
- `SketchbookAnimatedBackground` and `meshGradientBackground` draw drifting light blobs over a base color instead of a four-corner gradient. `colors` no longer fill the whole area; the new `baseColor` parameter shows between the blobs. Defaults are fixed pastel (light) and jewel-tone (dark) palettes, chosen by the luminance of the Material background color; they no longer follow theme colors. Both functions gain a trailing `Color` parameter, so their JVM signatures change: recompile consumers.

## Other changes

- Buttons, icon buttons, floating action buttons and chips are glass by default; outlined variants and chips have no border.
- Glass shapes use Haze's `RoundedCornerShape` with the theme's corner sizes, so clip, press highlight and rim always match.
- Glass controls are 48dp minimum, like plain controls.
- `SketchbookCheckbox` and `SketchbookRadioButton` are drawn by the library (squircle box with a thick check, filled disc with a dot); their `colors` parameter is ignored.
- `SketchbookSwitch` uses a green track when on and a red track when off with a white thumb, including when disabled.
- `SketchbookSegmentedButton` and the multi-choice variant draw one glass row without borders between segments.
- Progress indicators no longer draw a background track; `trackColor` is ignored.
- `specularBorder` now draws a gradient beam instead of a single-colour glint; `borderBeam` is the new general modifier.
- `SketchbookPullToRefresh` pulls on non-scrollable content as well as scrollable content.
- Material 3 controls (buttons, icon buttons, floating action buttons, chips, checkbox, radio button, switch, tabs, segmented buttons) use the theme press indication instead of their own ripple.
- Many components were added; see the component list in [README.md](README.md).
- The public API is otherwise unchanged apart from additions.
