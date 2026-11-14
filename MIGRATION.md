# Migrating to 2.0.0

## Breaking changes

- `minSdk` is 26 (was 21).
- Image loading uses Coil 3 (was Coil 2). Replace `coil.compose.*` types with `coil3.compose.*` where you use them with `lib.sketchbook.Image`; the Coil artifacts are `io.coil-kt.coil3:*`.
- `Theme` provides a glass press indication through `LocalIndication`, replacing the haptic-only indication. Clickable content under `Theme` shows an AGSL press highlight on API 33+ and a ripple below, and ticks once on press.
- Haptic feedback fires on press only; release and cancel no longer tick.
- Typography uses the true font weights (Medium, SemiBold, Bold, ExtraBold map to their own Sora files); text previously rendered with shifted weights looks different.
- The `key` parameter of the `rememberSaveable` helpers (`Dp`, `TextUnit`, `DpOffset`, `Offset`, `IntOffset`, `Color`) is ignored; saved state is keyed by position and `inputs`. `TickOnChange` and `ClickOnChange` no longer save state across process death.
- `SketchbookTopAppBar` counts its content padding once, so the default bar is 12dp shorter.

## Other changes

- Material 3 controls (buttons, icon buttons, floating action buttons, chips, checkbox, radio button, switch, tabs, segmented buttons) use the theme press indication instead of their own ripple.
- Many components were added; see the component list in [README.md](README.md).
- The public API is otherwise unchanged apart from additions.
