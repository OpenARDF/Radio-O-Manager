# Desktop Preparation Notes

These notes define the first desktop target before a user-visible desktop app is
added. They keep the desktop effort focused on a small event-admin beta instead
of full Android parity.

## Desktop Beta Boundary

The first desktop app should be a thin UI over shared event-domain services. It
should support event administration workflows that are useful away from the
finish table:

- create, open, edit, save, and export event files;
- manage races, categories, control points, aliases, competitors, readouts, and
  results;
- manually enter or edit readout-equivalent punch data;
- recalculate results using shared services;
- import/export supported event and result formats as they move into shared
  code.

The desktop beta should not include:

- live SPORTident reader download;
- Bluetooth or ticket printing;
- live result sending;
- Android Room database migration or shared SQL persistence;
- any promise that desktop can replace the Android race-day download workflow.

## Storage

Use file-backed project storage for the beta, most likely a `.rom.json` project
file. Shared SQL remains post-beta. A later SQL spike should compare Room KMP as
the baseline candidate against SQLDelight as the fallback/comparison option.

## UI And Module Shape

Prefer a small desktop app module that depends on `:shared`. Compose
Multiplatform Desktop is the default UI candidate because it keeps the app in
Kotlin and aligns naturally with the existing Kotlin Multiplatform foundation.

The desktop app should intentionally feel like the Android app, not like a new
product. Reuse the Android visual language where practical:

- preserve the same primary workflows and vocabulary: races, categories,
  competitors, readouts, results, aliases, and settings;
- reuse or port the existing Android vector icons for matching actions and tabs;
- use the Android theme colors as the starting desktop palette, including
  primary purple, secondary teal, white/black text defaults, grey disconnected
  state, orange reading state, green connected/read state, yellow warning state,
  and red error state;
- mirror the Android status-strip behavior for SI/readout state, even when the
  beta desktop app only shows simulated or manually entered readout state;
- keep dialogs, table rows, edit forms, and result/readout status colors close
  enough that Android users recognize the desktop screens immediately.

Desktop ergonomics can adapt to larger screens, menus, keyboard shortcuts, and
resizable windows, but those adaptations should extend the Android interface
rather than inventing a separate desktop visual identity.

The desktop app should keep platform concerns thin:

- file pickers and local filesystem permissions in the desktop module;
- event validation, formatting, placement, and import/export policy in shared
  code;
- desktop-only diagnostics and packaging metadata outside Android code.

## Packaging Direction

Use a jDeploy-based packaging path unless a focused packaging spike finds a
concrete blocker. This mirrors the SerialSlinger approach and should make
release operations familiar.

Packaging should eventually provide:

- launchable desktop artifacts for macOS, Windows, and Linux;
- version/build metadata tied to Git tags;
- a repeatable package validation command;
- a packaged-app smoke scenario that opens, edits, saves, reopens, and exports a
  sample event.

Keep Hydraulic Conveyor as a comparison option if jDeploy cannot satisfy a
specific requirement. Keep raw `jpackage` as a low-level fallback, not the
preferred release workflow.

## First Implementation Slices

1. Done: add golden-file coverage for the existing full race export shape.
2. Done: add a desktop app module with a minimal launch window and no event editing.
   The shell uses Compose Desktop, Android-derived colors, Android navigation
   vocabulary, and a non-editing status strip.
3. In progress: add file-backed open/save for a shared project envelope.
   The shared `.rom.json` envelope now has a tested JSON codec; desktop file
   filesystem wiring and current-project session state now live in the desktop
   app module. File picker and menu wiring remain next.
4. Add the first event-admin screen backed by shared models and services.
5. Add jDeploy metadata only after the desktop app can complete a real smoke
   scenario.
