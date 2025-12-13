# Counturu - Beautiful Countdown Timer App

A stunning native Android countdown timer app built with Kotlin and Jetpack Compose featuring glassmorphic design and smooth animations.

## Features

### 🎨 Design Philosophy
- **Glassmorphic/Liquid Glass Design** throughout the entire app
- **Smooth Animations** for all interactions using Jetpack Compose animation APIs
- **Material Design 3** with blur effects and translucency
- **Dark Mode Support** with manual toggle in settings

### 📱 App Structure

#### Bottom Navigation with 3 Tabs:
1. **Favs Tab** - Display your favorite counters
2. **Home Tab** - View all counters with floating action button
3. **Settings Tab** - Account info, app info, developer message, and dark mode toggle

### ✨ Core Features

#### Counter Cards
- Glassmorphic card design with blur effects
- Display event title and optional image
- Real-time countdown showing: Days, Hours, Minutes, Seconds
- Updates every second using `LaunchedEffect`
- Smooth `AnimatedVisibility` for appear/disappear
- **Swipeable Actions**:
  - Swipe right → Add to favorites (star icon)
  - Swipe left → Delete counter (trash icon)
- Tap to view counter details

#### Floating Action Button (FAB)
- Positioned at bottom-right corner (Material Design standard)
- Animated scale and rotation on tap
- Opens glassmorphic modal for creating new counter

#### Create Counter Dialog
- Full-screen glassmorphic overlay with blur effect
- Form fields:
  - Event title (TextField)
  - Date picker with calendar dialog
  - Time picker with time dialog
  - Reminder switch
  - Image picker for event photos
- Smooth slide-up animation using `AnimatedVisibility`
- Swipe down or cancel button to dismiss
- Save button to create counter

#### Data Persistence
- **Room Database** for storing counters
- Entity fields: id, title, targetDateTime, isFavorite, imageUri, hasReminder
- **Repository Pattern** with ViewModel
- **Flow** for reactive data updates

#### Dark Mode
- Support for system dark theme and manual toggle
- Adaptive glassmorphic effects for light/dark themes
- Material3 dynamic theming

#### Animations
- Spring animations for button presses (`animateFloatAsState`, `animateDpAsState`)
- `AnimatedVisibility` for modals and list items
- `AnimatedContent` for countdown number changes
- Color transitions with `animateColorAsState`

## Technical Stack

### Architecture & Libraries
- **Kotlin** - Modern programming language for Android
- **Jetpack Compose** - Declarative UI framework
- **Room Database** - Local data persistence
- **ViewModel + StateFlow/Flow** - State management
- **Coroutines** - Asynchronous programming
- **Material3** - Material Design components
- **Coil** - Image loading library
- **Navigation Compose** - Navigation between screens
- **WorkManager** - Background task scheduling (for reminders)

### Architecture Pattern
- **MVVM (Model-View-ViewModel)**
- **Repository Pattern** for data access
- **Clean Architecture** principles

## Project Structure

```
app/src/main/java/com/example/counturu/
├── data/
│   ├── Counter.kt           # Entity class
│   ├── CounterDao.kt        # Database access object
│   ├── CounterDatabase.kt   # Room database
│   └── CounterRepository.kt # Repository pattern
├── viewmodel/
│   └── CounterViewModel.kt  # ViewModel for state management
├── ui/
│   ├── components/
│   │   ├── CounterCard.kt           # Glassmorphic counter card
│   │   └── CreateCounterDialog.kt   # Create counter modal
│   ├── screens/
│   │   ├── FavsScreen.kt    # Favorites screen
│   │   ├── HomeScreen.kt    # Home screen
│   │   └── SettingsScreen.kt # Settings screen
│   └── theme/
│       ├── Color.kt         # Color definitions
│       ├── Theme.kt         # Theme configuration
│       └── Type.kt          # Typography
├── navigation/
│   └── Screen.kt            # Navigation routes
├── utils/
│   └── TimeUtils.kt         # Time calculation utilities
└── MainActivity.kt          # Main activity

```

## Requirements

- **Android Studio** Hedgehog or later
- **Minimum SDK**: API 31 (Android 12) - Required for native blur support
- **Target SDK**: API 36
- **Kotlin**: 2.0.21
- **Gradle**: 8.13.2

## Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd counturu
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the counturu folder

3. **Sync Gradle**
   - Android Studio should automatically sync Gradle
   - If not, click "Sync Now" in the notification bar

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press Shift+F10

## Permissions

The app requires the following permissions:
- `READ_MEDIA_IMAGES` - For selecting images for counters
- `POST_NOTIFICATIONS` - For reminder notifications

## Key Implementation Details

### Glassmorphic Effect
- Semi-transparent backgrounds using `Color.copy(alpha = 0.7f)`
- Elevated cards with shadow using `CardDefaults.cardElevation()`
- Gradient overlays using `Brush.verticalGradient()`
- Blur effects available on Android 12+ using `Modifier.blur()`

### Real-time Countdown
- Uses `LaunchedEffect` with a `while` loop
- Updates every second using `delay(1000)`
- Calculates time remaining: days, hours, minutes, seconds
- Displays "Event has passed" when countdown completes

### Swipe Gestures
- Implemented using `SwipeToDismissBox` from Material3
- Custom background with animated icons
- Color transitions based on swipe direction

### State Management
- ViewModel holds app state using StateFlow
- Room database provides Flow of data
- UI automatically updates when data changes
- Single source of truth pattern

## Future Enhancements

- [ ] Implement WorkManager for scheduled notifications
- [ ] Add counter categories/tags
- [ ] Export/Import counters
- [ ] Widget support for home screen
- [ ] Share countdown on social media
- [ ] Sound effects for animations
- [ ] Custom themes and color schemes
- [ ] Multiple reminder options
- [ ] Recurring counters

## License

This project is created for educational purposes.

## Credits

Created with ❤️ using Jetpack Compose and Material Design 3.

---

**Enjoy counting down to your special moments with Counturu! 🎉**

