# Interfast — Principles

One page. If a proposed change conflicts with this page, the change is wrong.

## What this app is

A kitchen timer for intermittent fasting. One screen. You drag a tape deck to
when your last meal ended, arm the milestone hours you care about, tap
ACTIVATE, and put the phone away. Exact alarms fire at each milestone. That is
the entire product.

## What this app refuses to do

- **No streaks.** Loss aversion is not a feature. Breaking a fast at hour 14
  instead of 16 is a normal, healthy decision, not a failure to log.
- **No history, no stats, no charts.** The phone already knows the time; you
  already know how you feel. `RUNTIME ∅ — WE DON'T COUNT.`
- **No onboarding, no protocol picker.** "Fast until you're hungry, then eat"
  needs no setup wizard.
- **No settings screen.** The only configuration is which milestones you arm,
  and that lives on the one screen. The back of the unit prints the config
  switches — STREAKS, STATS, GUILT — permanently in the OFF position.
- **No accounts, no network, no tracking.** All state is five DataStore keys
  on the device.

## Behavioral contract

1. **Set and forget — truly.** After the last armed milestone fires, the app
   disarms itself ("the tape rewinds"). Tomorrow never requires cleaning up
   yesterday.
2. **STOP is guilt-free.** Every alert carries a STOP action that cancels the
   rest, with no confirmation and no commentary.
3. **Armed means locked.** While active, the schedule cannot be edited —
   deactivate first. Modes are physical.
4. **Alarms are exact and survive** reboots and app updates.
5. **Everything on screen is true.** Readouts show real data (alarms armed,
   next fire time, Δ from now) — no decoration dressed up as telemetry.
6. **Accessible by hand or by ear.** Every control works under TalkBack: rows
   are toggles, the tape deck exposes time-adjustment actions, state changes
   announce themselves.

## Look and feel

Teenage Engineering hardware × Nothing's dot-matrix print × Swiss type
discipline. The front is a device; the back is its printed operator's manual
and component silkscreen (tap the edition stamp to flip). Light/dark follows
screen brightness — like a backlight, not a setting. Alerts are dithered
low-res banners, the OP-1 LCD look. JetBrains Mono for data, Space Grotesk for
the hero, GlyphRed as the single accent.
