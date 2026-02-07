# Changelog

All notable changes to Interfast will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

#### UX Improvements
- **Haptic Feedback System**: Tactile feedback for all interactions
  - Light tick for minor interactions
  - Heavy click for start/stop fast
  - Success pattern for fast completion
  - Double tap for milestones

- **Shake-to-Start Gesture**: Quick-start fasting with a phone shake
  - Configurable sensitivity
  - Debounce to prevent accidental triggers
  - Can be enabled/disabled in settings

- **Onboarding Flow**: First-time user experience
  - Welcome screen explaining intermittent fasting benefits
  - Protocol selection with visual comparison
  - Notification permission setup
  - Quick start to first fast

#### Notifications
- **Smart Motivational Notifications**: Context-aware milestone messages
  - Time-of-day appropriate messaging
  - Rotating messages to prevent fatigue
  - Encouraging tone at difficult fasting points
  - Celebration messages for completion

- **Streak Reminders**: Daily nudge to maintain consistency

#### Accessibility
- **TalkBack Support**: Full screen reader compatibility
  - Semantic content descriptions for all elements
  - Meaningful state announcements for timer
  - Proper focus management

- **Large Text Support**: Respects system font scaling
- **High Contrast**: Automatic detection and color adjustment
- **Minimum Touch Targets**: All interactive elements >= 48dp

#### Data & Privacy
- **Data Export**: Export your fasting history
  - JSON format for backup/restore
  - CSV format for spreadsheet analysis
  - Share via system share sheet

#### Device Integration
- **Nothing Phone Glyph Preparation**: Infrastructure for LED integration
  - Device detection for Nothing Phone models
  - Progress display framework
  - Breathing animation for active state
  - Celebration pattern for completion

#### Documentation
- **CONTRIBUTING.md**: Comprehensive contribution guidelines
- **CHANGELOG.md**: This file
- **IMPROVEMENT_LOG.md**: Development session notes

#### OSS Infrastructure
- **F-Droid Metadata**: Ready for F-Droid submission
  - Full description
  - Short description
  - Proper metadata structure

### Changed
- Notification worker upgraded to SmartNotificationWorker
- UserPreferences extended with shake-to-start and onboarding flags

### Technical
- Added `kotlinx.serialization` dependency for data export
- Added sensor permission for shake detection
- Created utility package for UI helpers

## [1.0.0] - Initial Release

### Features
- Fasting timer with dot-matrix progress ring
- Protocol selection (16:8, 18:6, 20:4, 23:1, Custom)
- History tracking with calendar view
- Statistics dashboard
- Milestone notifications (25%, 50%, 75%, 100%)
- Home screen widgets (2x2, 4x1, 4x2)

### Design
- Nothing Phone inspired aesthetics
- Swiss design grid system
- Bauhaus geometric forms
- Cypherpunk data display

### Technical
- Jetpack Compose UI
- Room database for persistence
- DataStore for preferences
- WorkManager for background notifications
- Glance for widgets
- Hilt for dependency injection

---

## Version History

| Version | Date | Highlights |
|---------|------|------------|
| 1.0.0 | TBD | Initial release |

## Upgrade Notes

### From Pre-release to 1.0.0
- Database schema stable
- No migration required
- Preferences preserved

---

*For detailed technical changes, see git commit history.*
