# Changelog

All notable changes to Interfast will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.1] — 2026-08-27

The Fairphone nod retuned to the Essential Key's actual light.

### Changed
- The nod color is now the Essential Key light bar's glow lime
  (`#E3EC9C`), replacing the earlier teal. The brand-row square and the
  edition stamp (`N° 0239 · FP6 ⟲`) print in it on Fairphone units —
  the same hue in dark and light skins, like luminous paint on the case.

## [1.2.0] — 2026-08-27

The deck now lands whole on every screen. On mid-rangers like the Fairphone 6
(372×828 dp usable) the old fixed-size layout overflowed by ~100 dp and forced
an awkward scroll through an instrument that is supposed to be a single
screen. Size is now chosen from the space the window actually offers.

### Added
- **Adaptive fit tiers** (`FitTier` / `LayoutFit`): the deck reads its usable
  height after system bars and selects REGULAR / COMPACT / TIGHT regimes —
  hero size, deck height, reels, clock size, row paddings, button height and
  section gaps all move together. The single-screen rule holds from ~320 dp
  wide pocket phones to tall flagships.
- **Fairphone nod**: on Fairphone units the brand row carries one teal square
  (the Essential Key's color) and the edition stamp is printed with the model
  tag (`N° 0239 · FP6`). Nothing else changes — the deck stays red/black.

### Changed
- Wheel tick labels are anchored above the deck's bottom edge (never amputated
  when the deck runs short), computed from a measured label probe.
- Hour-row index column widened so "01".."05" survive large font scales
  unwrapped.
- README screenshots re-shot on Fairphone 6 geometry.

### Fixed
- No more scroll on Fairphone 6-class screens in any state: idle, armed,
  light or dark — brand row to footer visible at once.
- Large font scales (1.5×) still work: the scroll becomes a deliberate
  fallback instead of an accident, and no control is clipped.

## [1.1.0] — 2026-06-10

Design audit pass: the three surfaces a user actually touches — the morning
after, the notification, the first drag — brought up to the standard of the
aesthetic.

### Added
- **Auto-disarm**: after the last armed milestone fires the app deactivates
  itself, keeping DONE badges until the next activation. "Set and forget" is
  now literally true; the final alert says "tape rewound" and carries no STOP.
- **Rear panel**: tap the N° edition stamp to flip the unit over — a
  Pocket-Operator-style printed back with the app's real component diagram,
  the TX-1 operator's manual, and config switches (STREAKS / STATS / GUILT)
  silkscreened permanently OFF.
- **Accessibility, for real**: hour rows are TalkBack toggles with full
  spoken descriptions; the tape deck exposes ±5 min / ±1 h / set-to-now
  actions; ACTIVATE announces why it is disabled; TAPE LIVE/IDLE is a live
  region.
- **Scrubber affordances**: a NOW chip, and a one-time ‹ DRAG › hint that
  dismisses forever after the first scrub.
- **Honest readout**: the deck's bottom row now shows ALARMS (count),
  NEXT (next fire time), and Δ in h/m — POS/MIN decoration removed.
- **Hero sublabels** teach the vocabulary (holding. / flowing. / armed. /
  done.), and an amber hint appears when no targets are armed.
- Alarms are re-registered after app updates (MY_PACKAGE_REPLACED).
- Tapping any notification opens the app.
- Reels spin while the tape is LIVE; LED pips show one per milestone —
  red armed, green reached.

### Fixed (review pass 2)
- System back on the rear panel flips to the front instead of exiting.
- Status/navigation bars swap to paper exactly when the flip crosses 90°.
- NOW chip and edition stamp hit areas enlarged (visuals unchanged).
- Rear panel print: non-breaking phrases (no more orphaned "MIN"), serial
  footer set on deliberate lines.
- The first scrub writes the hint-dismissed flag once, not once per tick.
- New launcher icon: the tape deck (reels, playhead, segment strip) on the
  dot grid, with a monochrome layer for Nothing OS themed icons.

### Changed
- Notification banner art: content inside safe margins (no more cropped
  numerals), words removed from the bitmap (title overlay + system header
  carry them), title block moved to the top so the 24-segment strip stays
  visible; system default heads-up (OEM shades crop custom ones).
- Light theme: dividers/progress track use a light neutral instead of
  near-black; disabled buttons use surface tokens in both themes.
- Microcopy: "start a fast." (was "start your IF."), "+1D" (was "TMR").
- Past rows dim to 45% with struck-through times.
- Edition number stays correct across midnight.

### Removed
- Dead code: unused `Accessibility.kt` facade, `NothingGlyphController`,
  `DotProgressIndicator`, unused button variants.
- Stale design docs moved to `docs/archive/`; `PRINCIPLES.md` added as the
  one-page source of truth.
- The dead `LOCKED_BOOT_COMPLETED` manifest action.

## [1.0.0] — 2026-05-07

Single-screen redesign: TE tape-deck scrubber, low-res dithered milestone
alerts, light/dark driven by screen brightness, exact alarms with boot
reschedule, milestone toggles with PAST/DONE states, haptic ticks, and the
footer easter egg. Everything earlier — protocol pickers, streaks, stats,
onboarding — was deleted on purpose (see `docs/PRINCIPLES.md`).
