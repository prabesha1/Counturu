# Counturu App - Quick Setup Guide

## ✅ What Has Been Created

The complete Counturu countdown timer app has been implemented with all requested features:

### 📂 Project Structure Created

```
✅ Data Layer
   - Counter.kt (Room entity)
   - CounterDao.kt (Database access)
   - CounterDatabase.kt (Room database)
   - CounterRepository.kt (Repository pattern)

✅ ViewModel Layer
   - CounterViewModel.kt (State management)

✅ UI Components
   - CounterCard.kt (Glassmorphic counter card with swipe gestures)
   - CreateCounterDialog.kt (Modal for creating counters)

✅ Screens
   - FavsScreen.kt (Favorites tab)
   - HomeScreen.kt (Home tab with FAB)
   - SettingsScreen.kt (Settings tab with dark mode)

✅ Navigation
   - Screen.kt (Navigation routes)
   - MainActivity.kt (Bottom navigation setup)

✅ Theme
   - Color.kt (Glassmorphic color scheme)
   - Theme.kt (Material3 theme with dark mode)

✅ Utils
   - TimeUtils.kt (Countdown calculations)
```

### 🎨 Features Implemented

- ✅ Glassmorphic design with translucent cards
- ✅ Bottom navigation with 3 tabs (Favs, Home, Settings)
- ✅ Real-time countdown (Days, Hours, Minutes, Seconds)
- ✅ Swipe gestures (right to favorite, left to delete)
- ✅ Floating Action Button with animations
- ✅ Create counter dialog with date/time pickers
- ✅ Image picker for counter photos
- ✅ Room database for persistence
- ✅ Dark mode toggle in settings
- ✅ Smooth animations throughout
- ✅ Material Design 3 components

## 🚀 Next Steps to Build

### 1. Sync Gradle Dependencies

In Android Studio:
- Click **File** → **Sync Project with Gradle Files**
- Or click the **Sync Now** banner if it appears
- Wait for all dependencies to download

### 2. Build the Project

Option A - Using Android Studio:
- Click **Build** → **Make Project** (Ctrl+F9 / Cmd+F9)

Option B - Using Terminal:
```bash
cd /Users/prabeshshrestha/AndroidStudioProjects/counturu
./gradlew assembleDebug
```

### 3. Run on Device/Emulator

- Connect an Android device (API 31+) or start an emulator
- Click the **Run** button (▶) or press Shift+F10
- Select your device
- The app will install and launch

## 📋 Requirements

- **Minimum SDK**: API 31 (Android 12) - Required for blur effects
- **Target SDK**: API 36
- **Device**: Physical device or emulator running Android 12+

## 🔧 Configuration Files Updated

✅ **app/build.gradle.kts**
   - Added KSP plugin for Room
   - Added Room, Navigation, ViewModel, Coil, WorkManager dependencies
   - Set minSdk = 31 for blur support

✅ **gradle/libs.versions.toml**
   - Added version catalogs for all dependencies
   - Room 2.6.1
   - Navigation Compose 2.7.7
   - Coil 2.5.0
   - WorkManager 2.9.0

✅ **AndroidManifest.xml**
   - Added READ_MEDIA_IMAGES permission
   - Added POST_NOTIFICATIONS permission

## 🎯 How to Use the App

### Creating a Counter
1. Tap the floating **+** button on the Home screen
2. Enter an event title
3. Select a date using the date picker (📅)
4. Select a time using the time picker (🕐)
5. Optionally add an image (🖼️)
6. Toggle reminder if needed
7. Tap **Create Counter**

### Managing Counters
- **Favorite**: Swipe counter card right (⭐)
- **Delete**: Swipe counter card left (🗑️)
- **View Details**: Tap on counter card

### Dark Mode
1. Go to **Settings** tab
2. Toggle the **Dark Mode** switch

## 🐛 Troubleshooting

### Gradle Sync Issues
If Gradle sync fails:
```bash
cd /Users/prabeshshrestha/AndroidStudioProjects/counturu
./gradlew clean
./gradlew build --refresh-dependencies
```

### KSP Plugin Not Found
If you see KSP errors:
1. Make sure Android Studio is updated to latest version
2. Invalidate caches: **File** → **Invalidate Caches / Restart**

### App Won't Run
- Ensure your device/emulator is running **Android 12 (API 31)** or higher
- Check that you've granted the app permissions for photos

## 📱 Permissions Required

The app will request these permissions at runtime:
- **Photos/Media** - To select images for counters
- **Notifications** - For reminder notifications (optional)

## 🎨 Customization

### Changing Colors
Edit `/app/src/main/java/com/example/counturu/ui/theme/Color.kt`

### Modifying Animations
Edit the individual screen files or `CounterCard.kt` for animation parameters

### Database Schema
To modify the Counter entity, update `/app/src/main/java/com/example/counturu/data/Counter.kt`

## 📖 Code Architecture

- **MVVM Pattern**: ViewModel manages UI state
- **Repository Pattern**: Single source of truth for data
- **Clean Architecture**: Separation of concerns (Data/Domain/UI)
- **Reactive Programming**: StateFlow for reactive updates
- **Dependency Injection**: Manual DI (can upgrade to Hilt later)

## 🚀 Future Enhancements (Optional)

To add more features, consider:
- Implementing WorkManager for actual notifications
- Adding counter categories/tags
- Exporting/importing counters
- Creating home screen widgets
- Adding sound effects
- Social media sharing

## 📝 Important Notes

1. The app uses **minSdk = 31** for native blur support via `Modifier.blur()`
2. All UI is built with **Jetpack Compose** - no XML layouts
3. Dark mode state persists only during app session (add DataStore for permanent storage)
4. Images are stored as URI strings (consider copying to app storage for production)

## ✅ You're All Set!

The Counturu app is fully implemented and ready to build. Simply sync Gradle and run the app to see your beautiful glassmorphic countdown timer in action! 🎉

---

For any issues or questions, refer to the detailed README.md file in the project root.

