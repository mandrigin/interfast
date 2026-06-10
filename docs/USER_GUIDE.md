# Interfast — User Guide

The whole app is one screen and the back of that screen. This page is longer
than the product.

## The front: the deck

```
┌──────────────────────────────────────┐
│ ■ INTERFAST                N° 0161 ⟲ │ ← tap the stamp to flip the unit over
│                                      │
│ holding.                             │ ← state: start a fast. / armed. /
│ ALARMS SET. FORGET THE PHONE.        │   holding. / flowing. / done.
│                                      │
│ ●●○○○            [NOW]  TAPE · LIVE  │ ← one LED per milestone: red = armed,
│ ┌──────────────────────────────────┐ │   green = reached
│ │            20:09                 │ │ ← fast start time — drag the tape
│ │   ◎  ──┴──┴──┼──┴──┴──  ◎        │ │   1 tick = 1 minute · long-press
│ │      20:00       20:30           │ │   (or NOW) snaps to current time
│ └──────────────────────────────────┘ │
│ ALARMS 02   NEXT 08:09   Δ −46m      │ ← armed count · next alarm · offset
│                                      │
│ TARGETS // 02                        │
│ 01 [✓] 12H → 08:09 +1D               │ ← tap to arm/disarm a milestone
│ 02 [✓] 16H → 12:09 +1D               │
│ 03 [ ] 18H → 14:09 +1D               │
│ ...                                  │
│ ████████████████░░░░░░░░             │ ← 24h strip, lit to longest target
│ ┌──────────────────────────────────┐ │
│ │            ACTIVATE              │ │
│ └──────────────────────────────────┘ │
└──────────────────────────────────────┘
```

### Using it

1. **Drag the tape** to when your last meal ended (or tap **NOW**).
2. **Tap the milestone hours** you want to be told about.
3. **ACTIVATE.** The deck locks, the reels roll, the phone does the rest.

### What happens next

- An exact alarm fires at each armed milestone with a dithered banner and a
  **STOP** action. STOP cancels the rest — no confirmation, no judgment.
- After the **last** milestone, the app disarms itself ("tape rewound").
  There is nothing to reset tomorrow.
- Alarms survive reboots and app updates.

### Good to know

- While active the schedule is locked — DEACTIVATE to edit.
- Light/dark follows your screen brightness, like a backlight.
- Past milestones show struck-through and dimmed; reached ones go green DONE.
- Everything works under TalkBack: rows are checkboxes, the tape deck has
  ±5 min / ±1 h / set-to-now actions.

## The back: the manual

Tap the **N° edition stamp** (top right) to flip the unit over. The back is
printed, Pocket-Operator style: the app's real component diagram, this manual
in legend form, and the config switches — STREAKS, STATS, GUILT — permanently
in the OFF position. Tap anywhere to flip back.

## FAQ

**Where are my stats?** There are none. `RUNTIME ∅ — WE DON'T COUNT.`

**I ate at hour 14 instead of 16.** Then hour 14 was the right length today.
Tap STOP on the next alert, or don't — the tape rewinds itself either way.

**Something about the footer feels tappable.** ×5.
