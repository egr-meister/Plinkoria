# Plinkoria

A fully offline Android casual game in Kotlin + Jetpack Compose with two modes: **Quiz** and **Matching Pairs**.

- Package / applicationId: `com.plinkoria.game`
- minSdk 24 · compileSdk 36 · targetSdk 36 · JDK 17 · portrait only

---

## Features

**Quiz — "Circles, balls & spheres".** 3 difficulty levels — **Easy** (Bouncy Basics), **Medium** (Round Science), **Hard** (Sphere Masters) — × 10 questions, 4 answers each, exactly one correct (answer order is shuffled every round). Up to 3 stars per level (≥40% / ≥70% / 10/10). One question at a time with a progress bar and `n/10`. Tapping an answer highlights the correct option green (with a ✓ glyph) and a wrong pick red (with a ✗ glyph), then auto-advances after ~0.75 s. Best score per difficulty is saved. Pause overlay: Continue / Restart / Menu. Results panel: `Score: X/10` with Continue / Restart / Menu.

**Matching Pairs.** 9 levels of increasing difficulty:

| Level | Pairs | Columns | Seconds |
|---|---|---|---|
| 1 | 2 | 2 | 60 |
| 2 | 3 | 2 | 70 |
| 3 | 4 | 2 | 70 |
| 4 | 6 | 3 | 80 |
| 5 | 6 | 3 | 80 |
| 6 | 8 | 4 | 100 |
| 7 | 8 | 4 | 100 |
| 8 | 10 | 4 | 110 |
| 9 | 12 | 4 | 120 |

The deck is `pairs` icons drawn at random from all 19 themed items, duplicated and shuffled. Flip two cards; matches stay open, mismatches flip back after 0.7 s. The countdown pauses while paused (and automatically when the app goes to the background). Clear every pair → **Well Done**; time runs out → **Out of Time**. Best matched pairs per level is saved. Level 1 is always unlocked; every next level unlocks when the previous one is cleared.

**Layout.** The Pairs board never scrolls: portrait cards stretch to fill the free area (ratio kept between 0.62 and 1.0), from 2×2 up to 4×6. In the Quiz, any free space under the answers becomes a character stage (three heroes that react to right/wrong answers); on short screens it hides itself and the quiz scrolls instead.

**Screens.** Main · Menu · Quiz categories · Quiz · Quiz results · Levels (9 bubbles + Game Rules) · Pairs game · Pairs results · Results summary · Settings · Game Rules.

**Settings.** Sound toggle (persisted, applied live), privacy, rate, reset progress with confirmation.

## Offline-only

There is no networking code and **no `INTERNET` permission** in the manifest. No runtime permissions, no services, WorkManager, alarms, notifications or keep-awake. "Rate" and "Full policy" just fire an `ACTION_VIEW` intent to the Play Store / browser — the app itself never touches the network.

## Local storage & DataStore

All progress lives in **one JSON string** inside **DataStore Preferences** (`plinkoria_store`, key `game_data_json`):

```kotlin
@Serializable data class GameData(
    val quizBest: Map<String, Int> = emptyMap(),   // categoryId -> best (0..10)
    val pairsBest: Map<String, Int> = emptyMap(),  // "1".."9" -> best matched pairs
    val settings: Settings = Settings()
)
@Serializable data class Settings(val soundEnabled: Boolean = true)
```

`GameSerializer` (context-free, `ignoreUnknownKeys = true`, `coerceInputValues = true`) never throws on read: empty storage, missing key, blank/corrupted/wrong-typed JSON, unknown keys, missing fields → safe defaults, and impossible values are clamped. DataStore uses a `ReplaceFileCorruptionHandler`, and read errors are caught in the repository flow.

## Privacy

No data collection, no accounts, no ads, analytics, attribution or tracking. See [`PRIVACY.md`](PRIVACY.md) (also linked from Settings — the link works once this repo is public).

## Not integrated (by design)

Firebase · Crashlytics · Google Play Games · AdMob or any ads SDK · analytics · attribution (AppsFlyer, Adjust, …) · push (OneSignal, FCM) · in-app purchases / billing · in-app review API · Retrofit/OkHttp/Ktor · Room · DI frameworks · WebView · maps · native libraries.

## Visual concept — "Neon Peg Cascade"

An arcade Plinko board at night: glossy violet-and-gold pieces glowing over a deep purple void, neon magenta and plasma cyan accents.

| Token | HEX | Usage |
|---|---|---|
| Night Void | `#120429` | scrims, deepest gradient stop |
| Deep Violet | `#1E0A45` | app + splash background, panel bottoms |
| Royal Purple | `#4A16A8` | panel tops, card faces |
| Neon Magenta | `#FF2FA8` | accent borders, progress, card backs |
| Plasma Cyan | `#35E3FF` | timer, secondary accent, peg glints |
| Peg Gold | `#FFC93D` | titles, scores, badges |
| Soft White | `#FFFFFF` | all button labels and body text |
| Correct / Wrong | `#2EE59D` / `#FF4D5E` | answer & low-time feedback (always paired with a glyph or text) |

**Unique home layout ("Peg Cascade").** Not a mascot → title → stats → button stack. The logo hangs, slightly tilted, over a Compose-drawn triangular peg board; the orb character zig-zags down the pegs and drops into the ornate **START** plate placed bottom-right. The pilot mascot stands cropped against the bottom-left edge and the robot peeks in from the right — an asymmetric arcade-cabinet composition. The Levels screen continues the motif with a staggered 3×3 "peg" layout of round bubbles.

**Buttons.** Image plate + label drawn in code in white bold text. `btn_plate` (simple bar) is reused for all regular buttons; `btn_menu` (ornate neon frame, transparent centre) is used for START / QUIZ / MATCHING PAIRS at ~0.7 screen width with `Modifier.aspectRatio(900/354)` and a Compose gradient painted behind the frame.

**Modal panels** (pause, results, rules, dialogs) are drawn in Compose — rounded rect, purple gradient, magenta→cyan→gold border — and grow with content. No frame image is ever stretched.

**Typography.** Material 3 with the system font (no bundled fonts): ExtraBold display/headline, Bold titles and labels, Medium body.

**Accessibility.** White-on-purple labels with a soft text shadow (high contrast); every touch target ≥ 48 dp; content descriptions on every image button, card ("Card 3, Star, matched"), level bubble ("Level 4, locked, 6 pairs, 80 seconds") and score; state is never color-only (✓/✗ glyphs, "Correct!/Not quite!" live region, lock glyph + "Locked" text); font scaling is honoured with sp units, and only fixed-shape plates cap the scale at 1.3× so labels never break the art.

### Assets (`app/src/main/res/drawable-nodpi`)

All UI drawables go through the single `ui/Assets.kt` object. Replace a file with the **same name** to re-skin — no code change.

| File | Source art |
|---|---|
| `bg_main.jpg` | background (opaque JPG) |
| `logo.png` | Plinkoria logo |
| `mascot_a.png` | robot buddy (hero_1) — Easy quiz host, Levels, fallback |
| `mascot_b.png` | neon pilot standing (hero_4) — home screen |
| `mascot_c.png` | neon pilot presenting (hero_2) — Menu, Rules, perfect/win results |
| `mascot_d.png` | crystal orb spirit (hero_5) — Hard quiz host, Pairs header, reset dialog |
| `orb.png` | eyed orb (hero_3) — falling ball on home, Medium quiz host, card backs |
| `btn_menu.png` | ornate neon frame plate (transparent) |
| `btn_plate.png` | simple plate |
| `plate_round.png` | level bubbles, card backs, A–D answer balls |
| `icon_back.png`, `icon_pause.png` | round back / pause buttons |
| `item_01`…`item_19.png` | key, lightning, shield, gift, heart, bell, puzzle piece, glossy ball, star, rocket, trophy, crystal, golden ring, gear, hourglass, padlock, neon ring, target, magnet — all used as Pairs cards **and** as UI icons (`Assets.iconTrophy`, `iconHourglass`, `iconLock`, …) |

PNG assets were trimmed, downscaled and palette-compressed (whole `res/` ≈ 1.4 MB).

### App icon

Adaptive icon: background = blurred crop of the supplied icon art (violet/magenta haze), foreground = the Plinkoria logo inside the 66 dp safe zone, plus a monochrome layer (orb + peg row) for Android 13 themed icons. Legacy square/round PNGs for mdpi…xxxhdpi. A 512×512 Play Store icon is in `store/play_icon_512.png`.

### Splash

AndroidX `core-splashscreen` (works on API 24+, native on 31+): Deep Violet `#1E0A45` background with a lightweight vector mark (gold-rimmed magenta orb over a row of pegs). No bitmap, no delay.

### Quiz content

`content/QuizContent.kt` holds the circles/balls/spheres questions in three difficulties (`easy`, `medium`, `hard`; 10 each, 4 options, one correct) — clearly marked as replaceable. Keep the `id`s stable once shipped (they key saved best scores).

### Motion & polish

Twinkling sparkle field on every screen; mascots and items float and sway; the logo breathes under a spinning neon ring; the orb zig-zags down the peg board with a gold ring trail; quiz questions slide in; cards flip in 3D; results celebrate with a burst of themed items and a star rating. All animations are lightweight Compose transitions (no Lottie, no extra libs).

### Audio

`SoundManager` (SoundPool, created in `PlinkoriaApp`) plays `click` (buttons/back/pause/card flips), `correct`, `wrong`, `match`, `win`, `lose`. The six files in `res/raw/` are short synthesized tones — drop in replacements with the same names. The Settings toggle persists in DataStore and is synced to `SoundManager` at startup and live.

## Tech stack

Kotlin 2.0.21 · Jetpack Compose (BOM 2024.12.01) · Material 3 · Navigation Compose 2.8.5 (type-safe `@Serializable` routes) · Lifecycle/ViewModel 2.8.7 · Coroutines 1.9.0 + Flow · DataStore Preferences 1.1.1 · Kotlinx Serialization 1.7.3 · core-splashscreen 1.0.1 · AGP 8.7.3 · Gradle 8.9 (Kotlin DSL + `gradle/libs.versions.toml`) · JDK 17. Every imported package is a direct catalog dependency.

## Architecture

Simple MVVM, no DI framework.

```
PlinkoriaApp (Application)  ── creates once ──> GameRepository (DataStore, one JSON string)
                                               SoundManager (SoundPool)
MainActivity ── AppContainer via CompositionLocal ──> AppNavHost (typed routes)
Screens ── viewModel(factory = XViewModel.Factory(...)) ──> ViewModels
ViewModels expose immutable UI state as StateFlow; screens use collectAsStateWithLifecycle.
logic/GameLogic   — pure: level table, deck building, unlock/completion, counts
data/GameSerializer — pure: JSON encode/decode with safe fallbacks
```

Navigation safety: every typed route is decoded with a try/catch; unknown categories, out-of-range levels/scores, locked levels (e.g. after a reset) or undecodable args show a friendly fallback screen with **Back**. Back never strands the user (falls back to Menu). Quiz/Pairs state is per-destination ViewModel; after process death the route args are restored and the round restarts cleanly. Results are saved **before** navigation so leaving a screen can't drop a write.

```
app/src/main/java/com/plinkoria/game/
├── PlinkoriaApp.kt, MainActivity.kt
├── audio/SoundManager.kt
├── content/QuizContent.kt
├── data/GameData.kt, GameSerializer.kt, GameRepository.kt
├── logic/GameLogic.kt
└── ui/ Assets.kt, AppContainer.kt, AppLinks.kt
    ├── theme/ Color.kt, Type.kt, Theme.kt
    ├── components/ Buttons.kt, Panels.kt
    ├── navigation/ Routes.kt, AppNavHost.kt
    ├── viewmodel/ ProgressViewModel.kt, QuizViewModel.kt, PairsViewModel.kt
    └── screens/ Main, Menu, QuizCategory, Quiz, QuizResult, Levels, Pairs, PairsResult,
                 Results, Settings, Rules, PauseOverlay
```

## Open in Android Studio

1. Android Studio Ladybug (2024.2) or newer → **Open** → select the project folder.
2. Settings → Build Tools → Gradle → **Gradle JDK = 17**.
3. SDK Manager → install **Android 16 (API 36)** platform (and Build-Tools 36.0.0 for `apksigner`).
4. Sync. AGP 8.7.x predates official API 36 support, so `gradle.properties` contains `android.suppressUnsupportedCompileSdk=36` (expected, harmless).

## 16 KB memory page size

Pure Kotlin/Compose, **no native `.so` libraries** → compatible with 16 KB page-size devices. Still verify the AAB: CI lists any `.so` files; locally `unzip -l app-release.aab | grep '\.so$'` should print nothing.

## Builds

```bash
# Debug (debug-signed, for development only)
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest       # unit tests

# Release — non-minified (R8 off), signed with your PKCS12 keystore
export ANDROID_KEYSTORE_PATH=/absolute/path/plinkoria-release.p12
export ANDROID_KEYSTORE_PASSWORD='...'
export ANDROID_KEY_ALIAS='plinkoria'
export ANDROID_KEY_PASSWORD='...'
./gradlew :app:assembleRelease :app:bundleRelease
```

If any of the four variables is missing, a release task **fails with a clear message** — it never falls back to the debug key.

### Generate a PKCS12 keystore

```bash
keytool -genkeypair -v \
  -storetype PKCS12 -keystore plinkoria-release.p12 \
  -alias plinkoria -keyalg RSA -keysize 4096 -validity 10000 \
  -dname "CN=Plinkoria, O=egr-meister, C=BY"
# PKCS12: store password and key password are the same value.
base64 -i plinkoria-release.p12 | tr -d '\n' > keystore.b64   # for the GitHub secret
```

Back up the `.p12` and passwords somewhere safe — losing them means you can't update the app (unless you use Play App Signing with an upload key). Never commit them (`.gitignore` blocks `*.p12`, `*.jks`, `keystore.b64`).

### GitHub Secrets

Repository → Settings → Secrets and variables → Actions → New repository secret:

| Secret | Value |
|---|---|
| `ANDROID_KEYSTORE_BASE64` | contents of `keystore.b64` |
| `ANDROID_KEYSTORE_PASSWORD` | store password |
| `ANDROID_KEY_ALIAS` | `plinkoria` |
| `ANDROID_KEY_PASSWORD` | key password (same as store for PKCS12) |

### GitHub Actions

`.github/workflows/android-build.yml` runs on push to `main` and manually (`workflow_dispatch`): JDK 17 → SDK Platform 36 + Build-Tools 36.0.0 → Gradle 8.9 via `gradle/actions/setup-gradle` (a committed wrapper is not required, though one is included) → unit tests → decodes the keystore into the runner temp dir → `assembleRelease` + `bundleRelease` → `apksigner verify --print-certs` (**fails** on `CN=Android Debug` or any verification failure) → AAB `jarsigner` check → 16 KB/native-lib check → uploads **APK** (testing) and **AAB** (Google Play) as artifacts. Secrets are never echoed; the decoded keystore is deleted at the end. No emulator smoke test.

### APK vs AAB

- **APK** — install directly on test devices (`adb install -r`).
- **AAB** — the only artifact you upload to **Google Play**.

**A green CI build is not proof the app launches.** Always verify locally.

### Local release verification

```bash
apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk   # must NOT show CN=Android Debug
adb install -r app/build/outputs/apk/release/app-release.apk
adb logcat --pid=$(adb shell pidof -s com.plinkoria.game)
```

Checklist: splash → home animates; Start → Menu; all three quizzes play to results, best score updates; Pairs level 1 → win → level 2 unlocks; lose by timeout; pause/continue/restart/menu in both modes; Back on every screen; sound toggle (also after app restart); Reset (confirm → progress gone, levels relocked); rotate/lock device and background/foreground mid-game; enable "Don't keep activities" and repeat; large font size (Settings → Display); TalkBack pass.

### Staged R8

1. Ship the first release with `isMinifyEnabled = false` and `isShrinkResources = false` (current state) and verify it with the checklist above.
2. Then set both to `true` in `app/build.gradle.kts`. `proguard-rules.pro` already keeps Kotlinx-Serialization classes (incl. the `@Serializable` navigation routes and `GameData`).
3. Rebuild, run the **same checklist again** on the minified APK (pay attention to navigation and saved progress), then upload the new AAB.

## Data reset

Settings → **Reset Progress** → confirm. This clears quiz bests and cleared levels and restores default settings (sound on). Alternatively: Android Settings → Apps → Plinkoria → Storage → Clear storage, or uninstall.

## Known limitations

- Quiz content is English only (replace/extend in `QuizContent.kt`).
- An in-progress quiz/pairs round is not persisted — after process death the round restarts (saved bests are unaffected).
- Timer resolution is 100 ms; the countdown keeps running only while the game screen is in the foreground.
- SFX are synthesized placeholder tones.
- Rate/Privacy links need a browser or the Play Store app; otherwise a friendly message is shown. The privacy URL points to `PRIVACY.md` in the `egr-meister/Plinkoria` GitHub repo.
- `android.suppressUnsupportedCompileSdk=36` is required until the project moves to an AGP that officially supports API 36.
