# Medicine Reminder App

A comprehensive Android application designed to help users manage their medicine schedules with customizable reminders and an elderly-friendly interface.

## Technology Stack

- **Platform:** Android
- **Language:** Kotlin
- **Minimum SDK:** 23 (Android 6.0)
- **Target SDK:** Latest (34)
- **Architecture:** MVVM (Model-View-ViewModel)

### Key Dependencies

- **UI Framework:** Jetpack Compose
- **Database:** Room Persistence Library
- **Asynchronous Programming:** Kotlin Coroutines
- **Image Loading:** Coil
- **State Management:** Kotlin Flow & StateFlow
- **Navigation:** Jetpack Navigation Compose

## Project Structure

```
app/src/main/
├── java/com/example/medicinereminder/
│   ├── data/
│   │   ├── dao/              # Data Access Objects
│   │   ├── database/         # Room Database Configuration
│   │   ├── entity/           # Data Entities
│   │   └── repository/       # Repository Pattern Implementation
│   ├── ui/
│   │   ├── components/       # Reusable UI Components
│   │   ├── screens/          # App Screens
│   │   ├── state/           # UI State Definitions
│   │   └── theme/           # App Theme Configuration
│   ├── viewmodel/           # ViewModels
│   ├── util/                # Utility Classes
│   └── MedicineReminderApp.kt
```

## Features

### Medicine Management
- Add medicines with detailed information
- Upload and store medicine images
- Edit existing medicine details
- Delete medicines and associated schedules
- View medicine list with detailed cards

### Schedule Management
- Create flexible medicine schedules:
  - Hourly reminders (1, 2, or 4-hour intervals)
  - Daily reminders at specific times
  - Custom duration (number of days)
  - Set daily time windows (e.g., 8 AM to 8 PM)
- Edit and delete schedules
- Toggle schedule activation

### Notification System
- Precise time-based notifications
- Rich notifications with:
  - Medicine image
  - Dosage information
  - Detailed instructions
  - Expandable view
- Respect system Do Not Disturb settings

### User Interface
- Material 3 Design
- Dark/Light theme support
- Elderly-friendly large text and buttons
- Clear visual hierarchy
- Responsive layout
- Support for multiple languages

## Use Cases

1. **Regular Medicine Schedule**
   ```
   Set daily reminder at 9 PM for 30 days
   ```

2. **Multiple Daily Doses**
   ```
   Set reminders every 4 hours from 8 AM to 8 PM for 7 days
   ```

3. **Frequent Medication**
   ```
   Set hourly reminders from 8 AM to 8 PM for 10 days
   ```

4. **Custom Intervals**
   ```
   Set 2-hourly reminders from 8 AM to 8 PM for 8 days
   ```

## Installation

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator or device

## Permissions Required

- `POST_NOTIFICATIONS`: For showing medicine reminders
- `SCHEDULE_EXACT_ALARM`: For precise alarm timing
- `READ_EXTERNAL_STORAGE`: For medicine images
- `WRITE_EXTERNAL_STORAGE`: For storing medicine images

## Contributing

Feel free to submit issues and enhancement requests.

## License

[Your License Choice]
