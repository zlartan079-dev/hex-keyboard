# Hex Keyboard

A honeycomb-style Android keyboard (IME) with letters in plain alphabetical
order (A-Z) instead of QWERTY, matching the hex-key visual style you shared.

## Layout

- Row 1: A B C D E F
- Row 2: G H I J K L M
- Row 3: Shift, N O P, Backspace
- Row 4: Q R S T U V
- Row 5: W X Y Z
- Row 6: Emoji, 123 (symbols toggle), Space, Enter

Tapping "123" swaps to a numbers/punctuation page; "ABC" swaps back.
Shift is a one-shot caps toggle (auto-lowercases after the next letter).

## How to build

### Option A — Android Studio (needs a computer)

1. Install **Android Studio** (free, from developer.android.com).
2. Choose **Open** and select this `HexKeyboard` folder (the one containing
   `settings.gradle`).
3. Let Gradle sync (first sync downloads the Android Gradle Plugin —
   needs internet).
4. Plug in your Android phone (with USB debugging on) or start an emulator.
5. Click **Run ▶**. This installs the app, which includes a small launcher
   screen with two buttons: "Enable keyboard" and "Switch keyboard."

### Option B — Phone only, via GitHub Actions

No computer needed. GitHub's servers compile the APK for you; you just
download the finished file. A workflow file (`.github/workflows/build-apk.yml`)
is already included — it builds automatically on every push.

1. **Get the project onto GitHub.** Easiest way from a phone:
   - Install **Termux** from F-Droid (not the old Play Store version —
     it's outdated). f-droid.org/packages/com.termux
   - In Termux: `termux-setup-storage`, then `pkg install git`
   - Download this project's zip in your phone browser (already saved to
     your Downloads), then in Termux:
     `cd ~/storage/downloads && unzip HexKeyboard.zip -d ~/HexKeyboard && cd ~/HexKeyboard`
   - Create a new **empty** repo on github.com (or the GitHub app) called
     e.g. `hex-keyboard`.
   - Back in Termux:
     ```
     git init
     git add .
     git commit -m "Initial commit"
     git branch -M main
     git remote add origin https://github.com/<your-username>/hex-keyboard.git
     git push -u origin main
     ```
     GitHub will ask you to log in (a personal access token works better
     than a password — GitHub's app can generate one, or use `gh auth login`
     if you install the `gh` package in Termux).

2. **Watch it build.** In your phone browser, go to your repo → the
   **Actions** tab. You'll see "Build debug APK" running (takes 2-5 min).

3. **Download the APK.** Once it finishes (green check), open that run →
   scroll to **Artifacts** → tap `HexKeyboard-debug-apk` to download a zip
   containing `app-debug.apk`.

4. **Install it.** Open the downloaded zip with a file manager, extract
   `app-debug.apk`, tap it, and allow "install from unknown sources" if
   prompted.

No Termux at all is also possible: create the repo on github.com, then
use GitHub's web "Add file → Upload files" to upload each file (a bit
tedious for nested folders, so Termux + git is the smoother route), or
open the repo in a browser-based **GitHub Codespace** and upload/extract
the zip there before committing.

## How to enable it on your phone

1. Open the "Hex Keyboard" app once — tap **Enable keyboard**, then turn on
   "Hex Keyboard" in the system Input Methods list.
2. Tap **Switch keyboard**, or long-press the space bar in any text field
   and pick "Hex Keyboard" from the picker.

## Files of interest

- `HexKeyboardService.java` — the actual IME (`InputMethodService`); talks
  to whatever app is focused via `InputConnection`.
- `HexKeyboardView.java` — draws the hexagon grid and handles touch;
  the alphabetical layout lives here as `LETTER_ROWS` / `SYMBOL_ROWS`.
- `HexKey.java` — simple data class for one key.
- `SetupActivity.java` — the launcher screen (not required for typing,
  just for enabling/switching to the keyboard).

## Tuning ideas

- Row heights / hexagon size: edit `layoutKeys()` in `HexKeyboardView`.
- Colors: `res/values/colors.xml`.
- Add swipe-to-type, long-press accents, or a real emoji panel by
  extending `onTouchEvent` / `onKey` in the same two files.
