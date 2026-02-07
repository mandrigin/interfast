# Contributing to Interfast

Thank you for your interest in contributing to Interfast! This document provides guidelines and information for contributors.

## Code of Conduct

Be kind, be respectful, be constructive. We're building something useful together.

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- A physical Android device or emulator (API 26+)

### Setup

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/interfast.git
   cd interfast
   ```
3. Open in Android Studio
4. Sync Gradle files
5. Download fonts (see README.md)
6. Build and run

### Project Structure

```
app/
├── data/               # Data layer
│   ├── db/            # Room database
│   ├── repository/    # Data repositories
│   ├── preferences/   # DataStore preferences
│   └── export/        # Data export utilities
├── domain/            # Domain layer
│   └── model/         # Domain models
├── device/            # Device-specific integrations
├── ui/                # Presentation layer
│   ├── theme/         # Design system
│   ├── components/    # Reusable components
│   ├── screens/       # Screen composables + ViewModels
│   ├── navigation/    # Navigation graph
│   ├── widgets/       # Glance widgets
│   └── util/          # UI utilities
├── worker/            # WorkManager workers
└── di/                # Hilt modules
```

## How to Contribute

### Reporting Bugs

1. Check existing issues to avoid duplicates
2. Use the bug report template
3. Include:
   - Android version
   - Device model
   - Steps to reproduce
   - Expected vs actual behavior
   - Relevant logs if available

### Suggesting Features

1. Check existing issues and discussions
2. Use the feature request template
3. Explain the use case, not just the solution
4. Consider privacy implications

### Submitting Code

#### Branch Naming

- `feature/description` - New features
- `fix/description` - Bug fixes
- `docs/description` - Documentation
- `refactor/description` - Code refactoring

#### Commit Messages

Follow conventional commits:

```
type(scope): brief description

Longer explanation if needed.

Closes #123
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

#### Pull Request Process

1. Create a feature branch from `main`
2. Make your changes
3. Add/update tests if applicable
4. Ensure all tests pass: `./gradlew test`
5. Update documentation if needed
6. Submit PR with clear description
7. Address review feedback

### Code Style

#### Kotlin

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable names
- Document public APIs with KDoc
- Prefer immutability

#### Compose

- Keep composables small and focused
- Use `remember` for expensive computations
- Follow the [Compose API guidelines](https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-api-guidelines.md)
- Use preview annotations for UI development

#### Architecture

- ViewModels expose state via StateFlow
- Screens are stateless composables
- Repository pattern for data access
- Dependency injection via Hilt

## Design Guidelines

Interfast follows a specific design language. Before making UI changes, review:

- [DESIGN_SYSTEM.md](docs/DESIGN_SYSTEM.md) - Design tokens and components
- [SPEC.md](docs/SPEC.md) - Feature specification

### Key Principles

1. **Minimal**: Every element must earn its place
2. **Typographic**: Text is the interface
3. **Dark-first**: Designed for dark mode
4. **Accessible**: Works for everyone

### Color Palette

| Color | Hex | Usage |
|-------|-----|-------|
| Glyph Red | `#FF3B30` | Primary actions, fasting state |
| Signal Cyan | `#00D4FF` | Eating window, secondary |
| Phosphor Green | `#39FF14` | Success, streaks |
| Amber Warning | `#FFB800` | Warnings, milestones |
| Void Black | `#0A0A0A` | Background |

## Testing

### Unit Tests

```bash
./gradlew test
```

Focus areas:
- ViewModel logic
- Repository methods
- Domain model calculations

### UI Tests

```bash
./gradlew connectedAndroidTest
```

Use Compose testing APIs for UI verification.

### Manual Testing

Before submitting:
- [ ] Timer accuracy over extended periods
- [ ] Notifications fire correctly
- [ ] Widget updates properly
- [ ] Data persists across app kills
- [ ] Accessibility with TalkBack

## Privacy Considerations

Interfast is privacy-first:

- **No analytics**: We don't track users
- **Local-only**: All data stays on device
- **No accounts**: No registration required
- **Export**: Users can export their data

When contributing, ensure:
- No external network calls for tracking
- No collection of personal information
- User data remains on-device
- Export functionality is maintained

## F-Droid Compatibility

Interfast aims for F-Droid inclusion. This means:

- No proprietary dependencies
- No tracking/analytics libraries
- Reproducible builds
- Apache-2.0 license compatibility

If adding a dependency, verify F-Droid compatibility first.

## Questions?

- Open a GitHub Discussion for questions
- Check existing issues for similar topics
- Review the documentation first

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.

---

*Thank you for helping make Interfast better!*
