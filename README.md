# 🚀 NexBoard

A modern, fluid, and highly customizable Android keyboard (IME) built entirely with **Jetpack Compose**, **Kotlin Coroutines**, and **Material You**. Featuring tactile **custom iPhone keypress audio**, unlimited clipboard history, rich GIF support, D-pad text editing, and categorized emojis.

---

## ✨ Features

### 1. 🎵 Tactile iPhone Keypress Sound
- Custom iPhone audio loaded via `SoundPool` with `maxStreams = 5` for zero-latency, overlapping keypresses.
- Automatically respects system ringer mode (silent/vibrate) and system sound toggles.
- Optional crisp haptic feedback on every keystroke.

### 2. ⌨️ Core IME Engine & QWERTY Layout
- Native `InputMethodService` implementation powered by Jetpack Compose.
- Ergonomic rounded keys, number row, symbols mode (`?123` and `=\<`), dynamic IME action button (Search, Send, Go, Done).
- Suggestion strip above the keyboard for rapid word completion.

### 3. 🛠️ Quick Access Top Toolbar
Direct toggles above the keyboard:
- **[Emoji]**: Dedicated categorized grid powered by `androidx.emoji2` with "Recently Used" memory.
- **[GIFs]**: Search and browse trending animated GIFs (Tenor & Giphy API via Retrofit + Coil GIF decoders). Inserts directly via `InputConnection.commitContent()`.
- **[Text Editing]**: Precision cursor controller with directional D-pad (`KEYCODE_DPAD_*`), Select, Select All, Cut, Copy, Paste, Home, and End buttons.
- **[Clipboard]**: Unlimited persistent clipboard history with search, snippet pinning, and one-tap pasting (persisted using Jetpack DataStore).

### 4. 🎨 Material You & AMOLED Black Theming
- Dynamic Monet theming on Android 12+ (adapts seamlessly to wallpaper colors).
- High-contrast Light theme, dark theme, and true **Pure AMOLED Black (`#000000`)** mode for maximum OLED battery savings.

### 5. 📱 Adaptive Layout Modes
- **Standard**: Full-width keyboard.
- **One-Handed Mode**: Shift keys to the left or right with side controls for one-handed thumb typing.
- **Split Keyboard**: Separates keys into left and right clusters for tablets and foldable devices.

---

## 📦 How to Install the APK

NexBoard is configured with continuous integration via **GitHub Actions**. Every commit to `main` automatically compiles and packages a debug APK in the cloud.

### 📥 Downloading from GitHub Actions:
1. Navigate to the **[Actions tab](../../actions)** of this repository on GitHub.
2. Click on the latest workflow run: **Build NexBoard Debug APK**.
3. Scroll down to the **Artifacts** section at the bottom of the summary page.
4. Download the **`NexBoard-debug-apk`** zip file.
5. Extract the `.apk` file and install it on your Android device (Android 11+ / API 30+).

### ⚙️ Enabling NexBoard on Android:
1. Open the **NexBoard** app launcher.
2. Tap **Step 1: Enable NexBoard** -> Toggle on **NexBoard Keyboard** in system settings.
3. Tap **Step 2: Set as Default** -> Select **NexBoard** in the input method picker.
4. Tap **Step 3: Test Keyboard** to test keypress sound, toolbar, and themes.

---

## 🏗️ Project Architecture

```
NexBoard/
├── .github/workflows/
│   └── android-build.yml          # GitHub Actions cloud CI workflow
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml    # IME Service, MainActivity & FileProvider
│   │   ├── res/
│   │   │   ├── raw/
│   │   │   │   └── keypress.wav   # iPhone keypress audio
│   │   │   └── xml/
│   │   │       ├── method.xml     # InputMethod configuration & subtypes
│   │   │       └── file_paths.xml # FileProvider paths for GIF content
│   │   └── java/com/nexboard/ime/
│   │       ├── NexBoardApp.kt     # Bundled EmojiCompat initialization
│   │       ├── audio/             # SoundPool & Haptics manager
│   │       ├── data/              # Repositories (DataStore, Retrofit, Clipboard)
│   │       ├── service/           # NexBoardIME (LifecycleOwner & InputMethodService)
│   │       └── ui/                # Jetpack Compose UI, Themes, Panels & Settings
│   └── build.gradle.kts           # Compose, Retrofit, Coil, Emoji2 dependencies
└── settings.gradle.kts
```

---

## 🛠️ Tech Stack & Dependencies
- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose (BOM 2024.10.01) + Material 3
- **Min SDK**: 30 (Android 11) | **Target SDK**: 35 (Android 15)
- **Architecture**: MVVM with Coroutines & StateFlow
- **Storage**: Jetpack DataStore Preferences
- **Networking**: Retrofit 2.11 + OkHttp 4.12 + Gson
- **Image/GIF Loading**: Coil Compose 2.7 + Coil GIF Decoder
- **Emojis**: AndroidX Emoji2 Bundled 1.5.0
