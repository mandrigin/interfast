# Interfast Design System

## Design Directions

Three distinct approaches, each synthesizing our four influences:
- Nothing Phone (dot matrix, glyph interface, minimalism)
- Swiss Design (grid, asymmetry, sans-serif hierarchy)
- Bauhaus (geometric forms, primary colors, function-form)
- Cypherpunk (monospace, terminal, data-forward)

---

## Direction A: "Terminal Brutalism"

### Concept
Heavy cypherpunk influence with Swiss grid rigor. The interface feels like a mission-critical control system - raw, functional, unapologetic. Monospace typography dominates. Data is king.

### Color Palette

| Role | Name | Hex | Rationale |
|------|------|-----|-----------|
| Primary | Terminal Green | `#00FF41` | Classic CRT phosphor green, signals "active" |
| Secondary | Amber Warning | `#FFB000` | Retro terminal amber, alerts |
| Accent | Cold Cyan | `#00E5FF` | Technical precision, progress |
| Alert | Blood Red | `#FF0040` | Urgent, stop, attention |
| Surface | True Black | `#000000` | Maximum contrast terminal |

### Typography

| Role | Font | Weight | Size |
|------|------|--------|------|
| Display | JetBrains Mono | Bold | 64sp/48sp |
| Heading | JetBrains Mono | Medium | 20sp/16sp |
| Body | JetBrains Mono | Regular | 14sp/12sp |

*Single font family, monospace only. Maximum cypherpunk commitment.*

### Key Screen: Timer
```
┌────────────────────────────────────────┐
│ > FAST.ACTIVE                    [···] │
│ ══════════════════════════════════════ │
│                                        │
│         1 6 : 4 2 : 0 8               │
│         ─────────────────             │
│         1 8 : 0 0 : 0 0               │
│                                        │
│  ████████████████████░░░░░░░  92.4%   │
│                                        │
│  ┌─────────┐  ┌─────────┐             │
│  │ > PAUSE │  │ > STOP  │             │
│  └─────────┘  └─────────┘             │
│                                        │
│ ══════════════════════════════════════ │
│ STREAK:012 | WEEK:098% | TOTAL:1842hr │
└────────────────────────────────────────┘
```

### Widget Designs

**2x2**: ASCII progress indicator, time in mono, minimal
```
┌──────────────────┐
│ [████████░░] 84% │
│    16:42:08      │
│                  │
│ > FASTING        │
└──────────────────┘
```

**4x1**: Single line, maximum data density
```
┌──────────────────────────────────────────────────┐
│ > FAST 16:42:08/18:00 [████████░░] 92% | STR:12 │
└──────────────────────────────────────────────────┘
```

**4x2**: Full terminal panel
```
┌──────────────────────────────────────────────────┐
│ INTERFAST v1.0                    STATUS: ACTIVE │
│ ════════════════════════════════════════════════ │
│ TIME: 16:42:08    TARGET: 18:00:00    PROG: 92% │
│ [████████████████████████████████████████░░░░░] │
│ ════════════════════════════════════════════════ │
│ > PAUSE    > STOP    > RESET           STR: 012 │
└──────────────────────────────────────────────────┘
```

### Self-Critique

**Strengths**:
- Extremely distinctive, memorable identity
- Maximum data clarity through monospace
- Strong cypherpunk authenticity
- Excellent dark mode contrast

**Weaknesses**:
- May feel intimidating to mainstream users
- Monospace-only limits typographic expression
- Green-on-black can cause eye strain over time
- Sacrifices Swiss elegance for terminal rawness
- Nothing Phone influence is weak (no dot matrix patterns)

**Score**: 7/10 - Bold but potentially alienating

---

## Direction B: "Glyph Matrix"

### Concept
Nothing Phone's dot matrix language meets Swiss precision. Timer displays use dot-matrix style numerals. Clean geometric forms from Bauhaus. Cypherpunk data density but with breathing room.

### Color Palette

| Role | Name | Hex | Rationale |
|------|------|-----|-----------|
| Primary | Glyph Red | `#FF3B30` | Nothing Phone's signature red glyph |
| Secondary | Matrix White | `#FAFAFA` | Clean, stark, Swiss clarity |
| Accent | Signal Cyan | `#00D4FF` | Progress, completion states |
| Alert | Phosphor | `#39FF14` | Success, streaks, positivity |
| Surface | Void | `#0A0A0A` | Deep black, not pure |

### Typography

| Role | Font | Weight | Size |
|------|------|--------|------|
| Display | Space Grotesk | Bold | 72sp/56sp |
| Heading | Space Grotesk | Medium | 24sp/18sp |
| Body | Inter | Regular | 14sp |
| Data | JetBrains Mono | Medium | 14sp/12sp |

*Space Grotesk brings geometric sans character. JetBrains Mono for data only.*

### Key Screen: Timer
```
┌────────────────────────────────────────┐
│                                        │
│  FASTING                    ●          │
│  ─────────────────────────────────     │
│                                        │
│           ┌─────────────────┐          │
│           │  ● ● ● ● ● ●    │          │
│           │  16:42:08       │          │
│           │  ────────       │          │
│           │  18:00:00       │          │
│           └─────────────────┘          │
│                                        │
│        ○○○○○○○○●●●●●●●●○○○○           │
│                 92%                    │
│                                        │
│     ┌──────────┐    ┌──────────┐      │
│     │  PAUSE   │    │   END    │      │
│     └──────────┘    └──────────┘      │
│                                        │
│  ─────────────────────────────────     │
│  12 day streak        This week: 98%  │
└────────────────────────────────────────┘
```

*Dot matrix-inspired progress indicator (○●). Clean geometric buttons.*

### Widget Designs

**2x2**: Dot ring progress, centered time
```
┌──────────────────┐
│    ○ ○ ○ ●       │
│   ○       ●      │
│  ○  16:42  ●     │
│   ○       ●      │
│    ○ ● ● ●       │
└──────────────────┘
```

**4x1**: Dot progress bar, asymmetric layout
```
┌──────────────────────────────────────────────────┐
│  ●  FASTING    16:42:08    ○○○○○○●●●●●●    92%  │
└──────────────────────────────────────────────────┘
```

**4x2**: Full display with glyph patterns
```
┌──────────────────────────────────────────────────┐
│  INTERFAST                          ● FASTING   │
│  ───────────────────────────────────────────    │
│         16:42:08  /  18:00:00                   │
│                                                  │
│  ○ ○ ○ ○ ○ ○ ○ ○ ○ ● ● ● ● ● ● ● ● ● ○ ○ ○ ○   │
│                                                  │
│  12d streak        98% week      [ ▶ ] [ ■ ]   │
└──────────────────────────────────────────────────┘
```

### Self-Critique

**Strengths**:
- Best synthesis of Nothing Phone aesthetic
- Dot matrix patterns are unique and memorable
- Good balance of all four influences
- Swiss clarity maintained
- JetBrains Mono for data preserves cypherpunk element

**Weaknesses**:
- Dot matrix rendering may be costly on widgets
- Custom dot glyphs need careful implementation
- Slightly less data-dense than Direction A
- May need careful spacing to not feel cluttered

**Score**: 8.5/10 - Strong synthesis, practical concerns

---

## Direction C: "Bauhaus Control"

### Concept
Bauhaus primary colors and geometric rigor dominate. Swiss grid is absolute. Interface feels like a control panel designed by Moholy-Nagy. Bold color blocks define state. Cypherpunk influence through data typography only.

### Color Palette

| Role | Name | Hex | Rationale |
|------|------|-----|-----------|
| Primary | Bauhaus Red | `#E53935` | Classic Bauhaus primary |
| Secondary | Bauhaus Blue | `#1E88E5` | Classic Bauhaus primary |
| Accent | Bauhaus Yellow | `#FDD835` | Classic Bauhaus primary |
| Success | Signal Green | `#43A047` | Modern addition for completion |
| Surface | Off Black | `#121212` | Material dark, not pure |

### Typography

| Role | Font | Weight | Size |
|------|------|--------|------|
| Display | Archivo Black | Regular | 80sp/64sp |
| Heading | Archivo | SemiBold | 24sp/18sp |
| Body | Work Sans | Regular | 14sp |
| Data | IBM Plex Mono | Medium | 14sp/12sp |

*Archivo's geometric confidence. Work Sans for readable body.*

### Key Screen: Timer
```
┌────────────────────────────────────────┐
│ ████████████████████████ FASTING ████ │
│                                        │
│                                        │
│              16:42                     │
│              ────                      │
│              18:00                     │
│                                        │
│ ┌──────────────────────────────────┐   │
│ │▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░│   │
│ └──────────────────────────────────┘   │
│             92%                        │
│                                        │
│ ┌────────────────┐ ┌────────────────┐ │
│ │     PAUSE      │ │      END       │ │
│ │    ●           │ │      ■         │ │
│ └────────────────┘ └────────────────┘ │
│                                        │
│ ████ 12 DAYS ████ ████ 98% WEEK ████ │
└────────────────────────────────────────┘
```

*Bold color blocks as section dividers. Geometric button icons.*

### Widget Designs

**2x2**: Color block state, bold time
```
┌──────────────────┐
│████ FASTING ████│
│                  │
│     16:42       │
│      92%        │
│                  │
└──────────────────┘
```

**4x1**: Color bar with embedded data
```
┌──────────────────────────────────────────────────┐
│ █ FASTING │  16:42:08  │ ▓▓▓▓▓▓▓▓░░ 92% │ 12d █ │
└──────────────────────────────────────────────────┘
```

**4x2**: Panel composition
```
┌──────────────────────────────────────────────────┐
│ ████████████████████ INTERFAST ████████████████ │
│                                                  │
│           16:42:08 / 18:00:00                   │
│  ┌──────────────────────────────────────────┐   │
│  │▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░│   │
│  └──────────────────────────────────────────┘   │
│  12d STREAK        98% WEEK       [▶][■][↻]    │
└──────────────────────────────────────────────────┘
```

### Self-Critique

**Strengths**:
- Strongest Bauhaus expression
- Bold, confident, memorable
- Color blocking creates clear visual hierarchy
- Very accessible color system

**Weaknesses**:
- Primary colors may feel dated or childish to some
- Weakest Nothing Phone integration
- Less distinctive than dot matrix approach
- Could feel like 1980s educational software
- Cypherpunk influence minimal

**Score**: 7.5/10 - Strong single-influence, weak synthesis

---

## Direction Selection: B - "Glyph Matrix"

### Rationale

Direction B best synthesizes all four design influences:

1. **Nothing Phone**: The dot matrix progress indicators and circular glyph patterns directly reference the Glyph Interface aesthetic. The use of stark red-on-black mirrors Nothing's brand language.

2. **Swiss Design**: Clean grid system, asymmetric but balanced layouts, typographic hierarchy with Space Grotesk as the geometric sans anchor.

3. **Bauhaus**: The circular progress elements are pure Bauhaus geometry. The limited color palette with functional color assignment follows form-function unity.

4. **Cypherpunk**: JetBrains Mono for all data display maintains the technical, data-forward sensibility. The overall stark aesthetic feels like a tool, not an app.

### Additional Considerations

- **Memorability**: The dot matrix elements create a distinctive visual signature
- **Scalability**: Dots work at widget size down to watch faces
- **Implementation**: Custom dot rendering is achievable with Compose Canvas
- **Accessibility**: High contrast maintained, dot patterns enhance not replace info

---

## Final Design System

### Color Palette

```kotlin
// Primary Palette
val GlyphRed = Color(0xFFFF3B30)     // Active fasting, primary CTAs
val SignalCyan = Color(0xFF00D4FF)   // Eating window, progress complete
val PhosphorGreen = Color(0xFF39FF14) // Success, streaks, positive
val AmberWarning = Color(0xFFFFB800)  // Milestones, warnings
val VoidBlack = Color(0xFF0A0A0A)    // Primary background

// Neutral Palette
val PureWhite = Color(0xFFFFFFFF)    // Primary text
val Gray80 = Color(0xFFCCCCCC)       // Secondary text
val Gray40 = Color(0xFF666666)       // Tertiary text
val Gray15 = Color(0xFF262626)       // Elevated surfaces
val Gray10 = Color(0xFF1A1A1A)       // Cards
```

### Typography Scale

```kotlin
// Display - Space Grotesk
val displayLarge = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 72.sp,
    letterSpacing = (-2).sp
)

val displayMedium = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Bold,
    fontSize = 56.sp,
    letterSpacing = (-1.5).sp
)

// Headings - Space Grotesk
val headlineLarge = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Medium,
    fontSize = 24.sp,
    letterSpacing = (-0.5).sp
)

val headlineMedium = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp
)

// Body - Inter
val bodyLarge = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp
)

val bodyMedium = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp
)

// Data - JetBrains Mono
val dataLarge = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    letterSpacing = 0.5.sp
)

val dataSmall = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Regular,
    fontSize = 12.sp,
    letterSpacing = 0.5.sp
)
```

### Spacing System

```kotlin
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
    val xxxl = 64.dp
}
```

### Component Specifications

#### Dot Progress Indicator
- Dot diameter: 8dp (standard), 6dp (compact), 4dp (widget)
- Dot gap: 4dp (standard), 3dp (compact), 2dp (widget)
- Active color: GlyphRed (fasting), SignalCyan (eating)
- Inactive color: Gray15
- Ring variants: 12, 24, or 36 dots depending on size

#### Timer Display
- Time format: HH:MM:SS (fasting), HH:MM (target)
- Separator: Colon, pulsing animation when active
- Font: Display Large for current, Display Medium for target

#### Action Buttons
- Height: 56dp (primary), 48dp (secondary)
- Corner radius: 12dp
- Background: Gray15 (default), GlyphRed (primary action)
- Text: Heading Medium, all caps
- Icon: 24dp, left-aligned with 12dp padding

#### Cards
- Background: Gray10
- Corner radius: 16dp
- Padding: 16dp
- Shadow: None (flat design)
- Border: 1dp Gray15 (optional, for emphasis)

### Motion Specifications

```kotlin
object Motion {
    val durationFast = 150
    val durationStandard = 250
    val durationEmphasis = 400

    val easingStandard = FastOutSlowInEasing
    val easingDecelerate = LinearOutSlowInEasing
    val easingAccelerate = FastOutLinearInEasing
}
```

#### Defined Animations
1. **Timer Tick**: Colon pulse (opacity 1.0 -> 0.4 -> 1.0, 500ms)
2. **State Change**: Cross-fade with scale (250ms)
3. **Progress Update**: Dot color transition (150ms each, staggered)
4. **Button Press**: Scale down to 0.95, back to 1.0 (150ms)
5. **Screen Transition**: Shared element for timer, fade for rest

### Iconography

All icons: 24x24dp canvas, 2dp stroke, round caps, no fill

| Icon | Description |
|------|-------------|
| play | Right-pointing triangle |
| pause | Two vertical bars |
| stop | Square |
| reset | Circular arrow |
| settings | Single gear |
| history | Clock with counterclockwise arrow |
| stats | Three ascending bars |
| protocol | Clock face with segments |
| notification | Bell outline |
| streak | Flame outline |
| check | Single checkmark |
| close | X mark |

---

## Screen Designs

### Timer Screen (Home)

```
┌────────────────────────────────────────┐
│                                        │
│  16:8                           ●      │
│                                        │
│           ○ ○ ○ ○ ○ ○ ○ ●              │
│         ○               ●              │
│        ○                 ●             │
│       ○    1 6 : 4 2     ●            │
│       ○    ─ ─ ─ ─ ─     ●            │
│       ○    1 8 : 0 0     ●            │
│        ○                 ●             │
│         ○               ●              │
│           ● ● ● ● ● ● ●               │
│                                        │
│              92.3%                     │
│                                        │
│   ┌─────────────┐  ┌─────────────┐    │
│   │   ▐▐ PAUSE  │  │    ■ END    │    │
│   └─────────────┘  └─────────────┘    │
│                                        │
├────────────────────────────────────────┤
│ ◐ 12d    ║  ≡ 98%    ║  Σ 1,842h     │
└────────────────────────────────────────┘
```

### Protocol Selection

```
┌────────────────────────────────────────┐
│  ← PROTOCOL                            │
│  ─────────────────────────────────     │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │ ● 16:8            SELECTED       │ │
│  │   16h fast · 8h eat              │ │
│  │   ○○○○○○○○○○○○○○○○●●●●●●●●      │ │
│  └──────────────────────────────────┘ │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │ ○ 18:6                           │ │
│  │   18h fast · 6h eat              │ │
│  │   ○○○○○○○○○○○○○○○○○○●●●●●●      │ │
│  └──────────────────────────────────┘ │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │ ○ 20:4                           │ │
│  │   20h fast · 4h eat              │ │
│  │   ○○○○○○○○○○○○○○○○○○○○●●●●      │ │
│  └──────────────────────────────────┘ │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │ ○ 23:1              OMAD         │ │
│  │   23h fast · 1h eat              │ │
│  │   ○○○○○○○○○○○○○○○○○○○○○○○●      │ │
│  └──────────────────────────────────┘ │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │ ＋ CUSTOM                        │ │
│  │   Create your own protocol       │ │
│  └──────────────────────────────────┘ │
│                                        │
└────────────────────────────────────────┘
```

### History Screen

```
┌────────────────────────────────────────┐
│  HISTORY                   FEB 2026   │
│  ─────────────────────────────────     │
│                                        │
│   M   T   W   T   F   S   S           │
│  ─────────────────────────────────     │
│   ●   ●   ●   ●   ●   ○   ●           │
│   ●   ●   ◐   ●   ●   ●   ●           │
│   ●   ●   ●   ●   ○                   │
│                                        │
│  ─────────────────────────────────     │
│  ● complete  ◐ partial  ○ missed      │
│                                        │
├────────────────────────────────────────┤
│  TODAY                                 │
│  ┌──────────────────────────────────┐ │
│  │  06:00 → 22:00          16:8     │ │
│  │  ████████████████████████░░  92% │ │
│  │  IN PROGRESS                     │ │
│  └──────────────────────────────────┘ │
│                                        │
│  YESTERDAY                             │
│  ┌──────────────────────────────────┐ │
│  │  06:00 → 22:00          16:8     │ │
│  │  ████████████████████████████ ✓  │ │
│  │  COMPLETED · 16h 12m             │ │
│  └──────────────────────────────────┘ │
│                                        │
│  WED, FEB 5                            │
│  ┌──────────────────────────────────┐ │
│  │  07:30 → 20:45          16:8     │ │
│  │  ████████████████████░░░░░░░░░░  │ │
│  │  ENDED EARLY · 13h 15m           │ │
│  └──────────────────────────────────┘ │
└────────────────────────────────────────┘
```

### Stats Screen

```
┌────────────────────────────────────────┐
│  STATS                                 │
│  ─────────────────────────────────     │
│                                        │
│  THIS WEEK                             │
│  ┌──────────────────────────────────┐ │
│  │                              98% │ │
│  │  ┌─┐ ┌─┐ ┌─┐ ┌─┐ ┌─┐ ┌─┐ ░░    │ │
│  │  │█│ │█│ │▓│ │█│ │█│ │█│ ░░    │ │
│  │  │█│ │█│ │█│ │█│ │█│ │█│ ░░    │ │
│  │  └─┘ └─┘ └─┘ └─┘ └─┘ └─┘ ░░    │ │
│  │   M   T   W   T   F   S   S     │ │
│  │                                  │ │
│  │  AVG: 16.2h    TOTAL: 97.4h     │ │
│  └──────────────────────────────────┘ │
│                                        │
│  RECORDS                               │
│  ┌────────────┐  ┌────────────┐       │
│  │ 12         │  │ 27         │       │
│  │ CURRENT    │  │ LONGEST    │       │
│  │ STREAK     │  │ STREAK     │       │
│  └────────────┘  └────────────┘       │
│                                        │
│  ┌────────────┐  ┌────────────┐       │
│  │ 1,842      │  │ 247        │       │
│  │ TOTAL      │  │ COMPLETED  │       │
│  │ HOURS      │  │ FASTS      │       │
│  └────────────┘  └────────────┘       │
│                                        │
│  MONTHLY TREND                         │
│  ┌──────────────────────────────────┐ │
│  │      ___/\___/\_                 │ │
│  │  ___/          \____             │ │
│  │ /                                │ │
│  │ O  N  D  J  F                    │ │
│  └──────────────────────────────────┘ │
└────────────────────────────────────────┘
```

---

## Widget Final Designs

### 2x2 Compact
- Dot ring progress (12 dots)
- Time in center (Space Grotesk Bold, 24sp)
- Status dot top-right (color indicates state)

### 4x1 Banner
- Status dot + label left
- Time center (Space Grotesk Bold, 18sp)
- Linear dot progress right
- Tap: Opens timer screen

### 4x2 Dashboard
- Header: App name + status indicator
- Timer: Large centered display
- Progress: Full-width dot bar
- Footer: Streak + Week % + quick action buttons
- Actions: Play/Pause, Stop (icon buttons)

---

*Design System v1.0 - Ready for Implementation*
