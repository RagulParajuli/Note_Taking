## Note Taking (Jetpack Compose, Room, Firebase Auth)

An Android notes app built with Kotlin and Jetpack Compose. It supports email/password and Google sign-in via Firebase Authentication, and stores notes locally using Room. Notes can be created, edited, deleted (with undo), searched, pinned, archived, and color‑coded.

### Features
- **Authentication**: Email/password and Google sign-in
- **Notes CRUD**: Create, edit, delete notes
- **Undo delete**: Snackbar with UNDO action
- **Search**: Title/content text search
- **Pin & Archive**: Basic organization
- **Color tags**: Choose a background color per note
- **Modern UI**: Jetpack Compose + Material 3

### Tech Stack
- **Language**: Kotlin (JDK 11)
- **UI**: Jetpack Compose, Material 3
- **Architecture**: MVVM + Repository
- **Navigation**: Navigation Compose
- **Persistence**: Room (with `TypeConverters` for `Date`)
- **Auth**: Firebase Authentication (Email/Password, Google)
- **Min SDK**: 24
- **Target/Compile SDK**: 36

### Screens
- Signup/Login (email/password and Google)
- Home: list of notes with swipe-to-delete and undo
- Editor: create and edit notes with color selection

---

## Project Structure
```text
app/
  src/main/java/com/ragul/notetaking/
    MainActivity.kt                 # Sets up Compose NavHost and routes
    Router.kt                       # Route constants and helpers
    Authentication/
      LoginScreen.kt
      SignupScreen.kt
    ui/
      screens/
        HomeScreen.kt              # List, swipe-to-delete, undo, sign out
        NoteEditorScreen.kt        # Create/Edit note, color selector
      components/
        SwipeableNoteItem.kt
        NoteItem.kt
        ColorSelector.kt
      theme/                       # Compose theme
    data/
      model/
        Note.kt                    # Room entity
        User.kt                    # Local user entity
      dao/
        NoteDao.kt
        UserDao.kt
      database/
        NoteDatabase.kt            # Room DB (fallbackToDestructiveMigration)
      repository/
        NoteRepository.kt
        UserRepository.kt
      util/
        Converters.kt              # Date converters
    ui/viewmodel/
      NoteViewModel.kt             # Notes state, undo, pin/archive
      AuthViewModel.kt             # FirebaseAuth + local user store
```

Gradle highlights:
- Compose BOM: `2024.09.00`
- Room: `2.6.1`
- Navigation Compose: `2.9.3`
- Firebase Auth: `24.0.1`
- Play Services Auth: `21.2.0`

---

## Getting Started

## How It Works

### Navigation
`MainActivity.kt` wires a `NavHost` with routes from `Router.kt`:
- `signup`, `login`, `home`, `create_note`, `edit_note/{noteId}`
- Start destination is decided by `AuthViewModel.isSignedIn()`.

### Authentication
`AuthViewModel` uses `FirebaseAuth` for sign-in and stores a minimal user in Room through `UserRepository`. Google sign-in uses the ID token from `GoogleSignInClient` and `GoogleAuthProvider`.

### Data Layer
- Entities: `Note`, `User`
- DAO: `NoteDao` offers LiveData queries for list/archived/search and suspend functions for CRUD.
- DB: `NoteDatabase` with `fallbackToDestructiveMigration()` (no manual migrations yet).
- Repository: `NoteRepository` exposes DAO functions and LiveData streams.

### ViewModel
- `NoteViewModel` exposes a `StateFlow<List<Note>>` for all notes, supports insert/update/delete, undo delete via a cached note, toggle pin/archive, and search.

### UI
- Home: `LazyColumn` of notes using `SwipeableNoteItem`, delete triggers snackbar with UNDO.
- Editor: create or edit; color selection via `ColorSelector`; save with FAB.

---

## Configuration & Environment
- Min SDK 24, Target/Compile SDK 36
- Kotlin JVM target 11
- Compose enabled via `buildFeatures { compose = true }`
- Room database name: `note_database`

Permissions in `AndroidManifest.xml`:
- `INTERNET` (required for Firebase)
- Legacy `READ/WRITE_EXTERNAL_STORAGE` are declared with `tools:ignore="ScopedStorage"` but are not required for core features on modern Android. You can remove them if unused.

---

## Development Notes
- Room uses `fallbackToDestructiveMigration()`; updating schema will wipe local data unless proper migrations are added.
- `NoteViewModel` mixes LiveData (from DAO) and `StateFlow` for UI consumption. Consider moving DAO to Flow for a fully flow-based pipeline.
- `AuthViewModel.isSignedIn()` relies on the locally cached user. If you need online verification, also check `FirebaseAuth.getInstance().currentUser`.

---

## Testing
Basic test scaffolds are present. Run from Android Studio or:
```bash
./gradlew testDebug
./gradlew connectedAndroidTest
```

---

## Troubleshooting
- Google sign-in fails with DEVELOPER_ERROR:
  - Ensure correct SHA‑1 is added in Firebase and the device has Google Play Services.
- `default_web_client_id` missing:
  - Re-sync after adding `google-services.json` and ensure the Google Services Gradle plugin is applied in `app/build.gradle.kts`.
- Cannot sign in:
  - Verify providers are enabled in Firebase Console and that the device has internet access.

---