# Interfast - Specification Document

## Overview

Interfast is a minimal, typographically bold intermittent fasting timer app that synthesizes:
- **Nothing Phone aesthetics**: Dot matrix typography, glyph interface patterns, stark minimalism
- **Swiss poster design**: Rigid grid systems, asymmetric layouts, bold sans-serif hierarchy
- **Bauhaus principles**: Geometric forms, primary colors, function-form unity
- **Cypherpunk visual language**: Monospace fonts, terminal aesthetics, data-forward display

## Core Features

### 1. Fasting Timer
- **Primary Display**: Large countdown/countup timer with progress visualization
- **States**: Fasting, Eating Window, Paused
- **Quick Actions**: Start/Stop, Pause/Resume, Reset
- **Background Operation**: Timer continues when app is closed

### 2. Protocol Selection
| Protocol | Fasting | Eating | Description |
|----------|---------|--------|-------------|
| 16:8 | 16h | 8h | Standard intermittent fasting |
| 18:6 | 18h | 6h | Extended daily fast |
| 20:4 | 20h | 4h | Warrior diet |
| 23:1 | 23h | 1h | OMAD (One Meal A Day) |
| Custom | User-defined | User-defined | Flexible protocol |

### 3. Fasting History
- **Daily Log**: Each fast recorded with start/end times, duration, completion %
- **Calendar View**: Monthly overview with completion indicators
- **Streak Tracking**: Current streak, longest streak, total fasts

### 4. Statistics Dashboard
- **Weekly Summary**: Average fasting hours, completion rate
- **Monthly Trends**: Visual chart of fasting patterns
- **Lifetime Stats**: Total hours fasted, total fasts completed

### 5. Notifications
- **Fasting Start Reminder**: Configurable time
- **Fasting End Alert**: When eating window opens
- **Milestone Alerts**: 25%, 50%, 75%, 100% completion
- **Streak Reminders**: Don't break the chain

### 6. Home Screen Widgets
- **2x2 Compact**: Timer + progress ring
- **4x1 Banner**: Timer + quick actions
- **4x2 Dashboard**: Timer + stats + actions

---

## Design System

### Color Palette (5 Colors Max)

| Role | Name | Hex | Usage |
|------|------|-----|-------|
| Primary | Signal Red | `#FF3B30` | Active fasting state, CTAs |
| Secondary | Electric Cyan | `#00D4FF` | Eating window, progress |
| Accent | Phosphor Green | `#39FF14` | Success, completion, streaks |
| Warning | Amber | `#FFB800` | Warnings, milestones |
| Surface | Void Black | `#0A0A0A` | Backgrounds |

**Neutral Palette**:
- Pure White: `#FFFFFF` - Primary text
- Gray 80: `#CCCCCC` - Secondary text
- Gray 40: `#666666` - Tertiary text
- Gray 15: `#262626` - Elevated surfaces
- Gray 10: `#1A1A1A` - Cards

### Typography

| Role | Font | Weight | Size | Usage |
|------|------|--------|------|-------|
| Display | Space Grotesk | Bold | 72sp/48sp | Timer digits |
| Heading | Space Grotesk | Medium | 24sp/20sp/16sp | Section heads |
| Body | Inter | Regular/Medium | 14sp/12sp | Body text, labels |
| Mono | JetBrains Mono | Regular | 14sp/12sp | Stats, data, timestamps |

### Grid System
- **Base Unit**: 8dp
- **Margins**: 24dp (outer), 16dp (inner)
- **Gutters**: 16dp
- **Touch Targets**: Minimum 48dp

### Iconography
- **Style**: Geometric, single-stroke weight (2dp)
- **Grid**: 24x24dp canvas, 20x20dp live area
- **Corners**: 2dp radius for terminals
- **Library**: Custom minimal set (15-20 icons)

### Motion Design
- **Duration**: 200ms (micro), 300ms (standard), 500ms (emphasis)
- **Easing**: FastOutSlowIn for enters, SlowOutFastIn for exits
- **Purpose**: Only for state changes and feedback, never decorative

---

## Technical Architecture

### Stack
```
Kotlin 1.9+
Jetpack Compose (UI)
Material You (Dynamic theming)
Hilt (Dependency Injection)
Room (Local Database)
DataStore (Preferences)
WorkManager (Background Tasks)
Glance (Widgets)
```

### MVVM Architecture

```
┌─────────────────────────────────────────────────────┐
│                    UI Layer                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │
│  │  Screens    │  │  Widgets    │  │ Components  │ │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘ │
│         └────────────────┼────────────────┘        │
│                          ▼                          │
│  ┌──────────────────────────────────────────────┐  │
│  │              ViewModels                       │  │
│  │  TimerVM | HistoryVM | StatsVM | SettingsVM  │  │
│  └──────────────────────┬───────────────────────┘  │
└─────────────────────────┼───────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────┐
│                  Domain Layer                       │
│  ┌────────────┐  ┌────────────┐  ┌──────────────┐  │
│  │ UseCases   │  │ Repository │  │   Models     │  │
│  └────────────┘  └────────────┘  └──────────────┘  │
└─────────────────────────┼───────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────┐
│                   Data Layer                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────┐  │
│  │   Room   │  │DataStore │  │  WorkManager     │  │
│  │    DB    │  │  Prefs   │  │  (Notifications) │  │
│  └──────────┘  └──────────┘  └──────────────────┘  │
└─────────────────────────────────────────────────────┘
```

### Database Schema

```sql
-- Fast Sessions
CREATE TABLE fast_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    protocol_type TEXT NOT NULL,
    fasting_hours INTEGER NOT NULL,
    eating_hours INTEGER NOT NULL,
    started_at INTEGER NOT NULL,
    ended_at INTEGER,
    completed_at INTEGER,
    status TEXT NOT NULL, -- 'active', 'completed', 'cancelled'
    completion_percentage REAL DEFAULT 0
);

-- User Preferences (via DataStore)
-- protocol, notifications, theme, etc.
```

### WorkManager Jobs

| Job | Trigger | Purpose |
|-----|---------|---------|
| `FastingReminderWorker` | Scheduled | Daily start reminder |
| `MilestoneNotificationWorker` | Time-based | 25/50/75/100% alerts |
| `WidgetUpdateWorker` | Periodic (15min) | Refresh widget data |
| `StreakCheckWorker` | Daily | Calculate/notify streaks |

---

## Widget Specifications

### 2x2 Compact Widget
```
┌────────────────────────┐
│     ┌─────────┐        │
│     │ ●●●●●●  │  16:42 │
│     │ ●    ●  │  ───── │
│     │ ●●●●●●  │  18:00 │
│     └─────────┘        │
│       72%              │
└────────────────────────┘
```
- **Content**: Progress ring, time remaining, target
- **Interaction**: Tap opens app
- **States**: Fasting (red ring), Eating (cyan ring), Inactive (gray)

### 4x1 Banner Widget
```
┌──────────────────────────────────────────────────┐
│  ●  FASTING     16:42:08        [▶] [■]         │
└──────────────────────────────────────────────────┘
```
- **Content**: Status indicator, label, timer, quick actions
- **Interaction**: Play/Pause, Stop buttons, tap to open
- **States**: Visual color coding for fasting/eating

### 4x2 Dashboard Widget
```
┌──────────────────────────────────────────────────┐
│  INTERFAST                          ● FASTING   │
│  ─────────────────────────────────────────────  │
│        16:42:08 / 18:00:00                      │
│  ░░░░░░░░░░░░░░░░░░░░████████████░░░░░░░░░░░░░  │
│  ─────────────────────────────────────────────  │
│  STREAK: 12d    WEEK: 98%      [▶] [■] [↻]     │
└──────────────────────────────────────────────────┘
```
- **Content**: Full timer, progress bar, streak, weekly %, actions
- **Interaction**: All quick actions, tap sections to open relevant screen

---

## Screen Specifications

### 1. Timer Screen (Home)
- Large timer display (center, dominant)
- Progress ring around timer
- Protocol label above timer
- Start/Pause/Stop buttons below
- Current status indicator
- Quick protocol switch

### 2. Protocols Screen
- List of presets (16:8, 18:6, 20:4, 23:1)
- Custom protocol builder
- Visual comparison of protocols
- Active protocol highlight

### 3. History Screen
- Calendar view (top)
- Daily log list (bottom, scrollable)
- Filter by status
- Streak display

### 4. Stats Screen
- Weekly summary cards
- Trend chart (line graph, minimal)
- Lifetime statistics
- Best records

### 5. Settings Screen
- Notification preferences
- Theme selection
- Data export
- About/Credits

---

## Success Criteria

### Functional
- [ ] Timer accurate to ±1 second over 24 hours
- [ ] Notifications fire within 1 minute of target
- [ ] Widgets update within 15 minutes
- [ ] Data persists across app kills
- [ ] Background operation stable

### Design
- [ ] All screens follow 8dp grid
- [ ] Typography hierarchy clear and consistent
- [ ] Color palette adhered to strictly
- [ ] Animations purposeful, under 500ms
- [ ] Touch targets ≥48dp
- [ ] Contrast ratios WCAG AA compliant

### Technical
- [ ] Unit test coverage >80% for ViewModels
- [ ] UI tests for critical flows
- [ ] No ANRs in normal operation
- [ ] Memory usage <100MB typical
- [ ] Battery impact minimal (background)

### Quality Bar
> "A practical work of art" - The app must be both genuinely useful for daily fasting practice AND aesthetically distinctive, synthesizing its design influences into something novel and memorable.

---

## Project Structure

```
/app
├── build.gradle.kts
├── src/main
│   ├── java/com/interfast
│   │   ├── data/
│   │   │   ├── db/
│   │   │   ├── repository/
│   │   │   └── preferences/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   └── usecase/
│   │   ├── ui/
│   │   │   ├── theme/
│   │   │   ├── components/
│   │   │   ├── screens/
│   │   │   └── widgets/
│   │   ├── worker/
│   │   └── di/
│   └── res/
└── src/test
    └── java/com/interfast
```

---

*Specification v1.0 - Ready for Design Phase*
