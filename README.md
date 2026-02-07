# Interfast

A minimal, typographically bold intermittent fasting timer app for Android.

## Design Philosophy

Interfast synthesizes four distinct aesthetic influences into a cohesive visual language:

- **Nothing Phone**: Dot matrix progress indicators, glyph interface patterns, stark minimalism
- **Swiss Design**: Rigid grid systems, asymmetric layouts, bold sans-serif hierarchy
- **Bauhaus**: Geometric forms, limited color palette, function-form unity
- **Cypherpunk**: Monospace data display, terminal aesthetics, data-forward presentation

The result is an interface that feels both technical and approachable - a practical work of art.

## Features

### Core Functionality
- **Fasting Timer**: Accurate countdown with visual progress ring
- **Protocol Selection**: 16:8, 18:6, 20:4, 23:1 (OMAD), and custom protocols
- **History Tracking**: Calendar view with daily completion status
- **Statistics**: Streaks, completion rates, weekly trends
- **Smart Notifications**: Contextual, motivational milestone alerts

### Delightful Interactions
- **Haptic Feedback**: Tactile feedback for all interactions
- **Shake-to-Start**: Quick-start fasting with a phone shake
- **Onboarding Flow**: Guided first-time setup with fasting education

### Privacy First
- **Local-Only Data**: All data stored on device, never synced to cloud
- **Data Export**: Export to JSON (backup) or CSV (spreadsheet analysis)
- **No Tracking**: No analytics, no accounts, no data collection

### Home Screen Widgets
- **2x2 Compact**: Timer with dot progress ring
- **4x1 Banner**: Horizontal status bar with quick glance info
- **4x2 Dashboard**: Full-featured widget with timer, progress, and stats

### Accessibility
- **TalkBack Support**: Full screen reader compatibility
- **Large Text**: Respects system font scaling
- **High Contrast**: Works in all accessibility modes
- **Touch Targets**: Minimum 48dp for all interactive elements

## Tech Stack

- Kotlin 1.9+
- Jetpack Compose (UI)
- Material You (Dynamic theming support)
- Hilt (Dependency Injection)
- Room (Local Database)
- DataStore (Preferences)
- WorkManager (Background notifications)
- Glance (Home screen widgets)

## Architecture

The app follows MVVM architecture with clean separation of concerns:

```
app/
├── data/               # Data layer
│   ├── db/            # Room database entities and DAOs
│   ├── repository/    # Data repositories
│   └── preferences/   # DataStore preferences
├── domain/            # Domain layer
│   ├── model/         # Domain models
│   └── usecase/       # Business logic (if needed)
├── ui/                # Presentation layer
│   ├── theme/         # Design system (colors, typography, spacing)
│   ├── components/    # Reusable UI components
│   ├── screens/       # Screen composables and ViewModels
│   ├── navigation/    # Navigation graph
│   └── widgets/       # Glance widgets
├── worker/            # WorkManager workers
└── di/                # Hilt modules
```

## Building

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34

### Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Download fonts (see below)
5. Build and run

### Fonts

The design system requires three font families. Download from Google Fonts and place in `app/src/main/res/font/`:

1. **Space Grotesk** - [Download](https://fonts.google.com/specimen/Space+Grotesk)
   - `space_grotesk_regular.ttf`
   - `space_grotesk_medium.ttf`
   - `space_grotesk_bold.ttf`

2. **Inter** - [Download](https://fonts.google.com/specimen/Inter)
   - `inter_regular.ttf`
   - `inter_medium.ttf`
   - `inter_semibold.ttf`

3. **JetBrains Mono** - [Download](https://fonts.google.com/specimen/JetBrains+Mono)
   - `jetbrains_mono_regular.ttf`
   - `jetbrains_mono_medium.ttf`

Then update `app/src/main/java/com/interfast/ui/theme/Type.kt` to use the Font() declarations instead of system font fallbacks.

### Building

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing config)
./gradlew assembleRelease

# Run tests
./gradlew test
./gradlew connectedAndroidTest
```

## Design System

### Color Palette

| Color | Hex | Usage |
|-------|-----|-------|
| Glyph Red | `#FF3B30` | Active fasting, primary actions |
| Signal Cyan | `#00D4FF` | Eating window, progress |
| Phosphor Green | `#39FF14` | Success, streaks |
| Amber Warning | `#FFB800` | Milestones, warnings |
| Void Black | `#0A0A0A` | Background |

### Typography

- **Display**: Space Grotesk Bold (timer digits)
- **Headings**: Space Grotesk Medium
- **Body**: Inter Regular/Medium
- **Data**: JetBrains Mono (statistics, timestamps)

### Grid System

- Base unit: 8dp
- Margins: 24dp (outer), 16dp (inner)
- Touch targets: minimum 48dp

## Documentation

- [Specification](docs/SPEC.md) - Full feature specification
- [Design System](docs/DESIGN_SYSTEM.md) - Design documentation
- [User Guide](docs/USER_GUIDE.md) - End user guide
- [Design Critique Log](docs/DESIGN_CRITIQUE_LOG.md) - Design iteration notes
- [Changelog](docs/CHANGELOG.md) - Version history
- [Improvement Log](docs/IMPROVEMENT_LOG.md) - Development notes

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines.

## License

Apache License 2.0 - See [LICENSE](LICENSE) for details.

---

*A practical work of art.*
