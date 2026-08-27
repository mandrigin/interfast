package com.interfast.ui.theme

import android.content.Context
import android.database.ContentObserver
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Display
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

// The float brightness key (API 31+) — some ROMs write the slider here
// instead of the classic int.
private const val SCREEN_BRIGHTNESS_FLOAT_KEY = "screen_brightness_float"

/**
 * Returns whether the dark theme should be active based on the screen's
 * ACTUAL brightness. Below [DARK_THRESHOLD] → dark; above [LIGHT_THRESHOLD]
 * → light. Hysteresis between the two avoids flicker.
 *
 * Why not just read the settings value? On Android 12+ with adaptive
 * brightness (the default on Fairphone/Nothing/Pixel), the real backlight
 * follows the light sensor and the slider's short-term curve, and the
 * classic `screen_brightness` key can sit untouched at a stale value while
 * the panel goes dim. So the reading order is:
 *
 *  1. API 35+: `Display.getBrightnessInfo().brightness` — the current
 *     actual backlight, adaptive-aware (display looked up via
 *     DisplayManager, since a bare context's `display` can be null).
 *  2. `screen_brightness_float` (0..1), where the ROM writes it.
 *  3. The classic `screen_brightness` int — accurate only when adaptive
 *     brightness is OFF (the slider then writes this key directly).
 *
 * When only the classic int is available (adaptive ON), the ambient light
 * sensor breaks the tie: a dim room means the adaptive curve is holding the
 * panel dim too — go dark. A ContentObserver catches slider changes; a
 * 1 Hz heartbeat catches everything else.
 *
 * Falls back to [isSystemInDarkTheme] for the very first frame before any
 * reading lands.
 */
@Composable
fun rememberAmbientDarkTheme(): Boolean {
    val context = LocalContext.current
    val resolver = context.contentResolver
    val systemFallback = isSystemInDarkTheme()

    var brightness by remember {
        mutableStateOf(
            readBrightness(context)
                ?: if (systemFallback) DARK_THRESHOLD - 1 else LIGHT_THRESHOLD + 1
        )
    }
    var lux by remember { mutableStateOf<Float?>(null) }
    var dark by remember { mutableStateOf(brightness < (DARK_THRESHOLD + LIGHT_THRESHOLD) / 2) }

    DisposableEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                readBrightness(context)?.let { brightness = it }
            }
        }
        resolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
            true,
            observer,
        )
        resolver.registerContentObserver(
            Settings.System.getUriFor(SCREEN_BRIGHTNESS_FLOAT_KEY),
            true,
            observer,
        )

        val sensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                lux = event.values.firstOrNull()
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        light?.let {
            sensorManager.registerListener(
                sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        onDispose {
            resolver.unregisterContentObserver(observer)
            sensorManager.unregisterListener(sensorListener)
        }
    }

    // Adaptive dimming moves the backlight without a settings write; the
    // heartbeat is what keeps the night skin honest on such devices.
    LaunchedEffect(Unit) {
        while (true) {
            readBrightness(context)?.let { brightness = it }
            delay(1_000)
        }
    }

    LaunchedEffect(brightness, lux) {
        dark = decideDark(brightness, lux, dark)
    }
    return dark
}

/**
 * The theme decision, pure and unit-testable. [brightness255] is the best
 * backlight estimate we have; [lux] the ambient light reading, when the
 * device has a sensor.
 */
fun decideDark(brightness255: Int, lux: Float?, currentlyDark: Boolean): Boolean = when {
    brightness255 < DARK_THRESHOLD -> true

    brightness255 > LIGHT_THRESHOLD && lux != null && lux < DARK_LUX -> true

    brightness255 > LIGHT_THRESHOLD && lux != null && lux > LIGHT_LUX -> false

    else -> currentlyDark
}

/**
 * Best-effort read of the current backlight as a 0..255 int, in the
 * priority order of the class doc. Null → caller keeps the last value.
 */
private fun readBrightness(context: Context): Int? {
    if (Build.VERSION.SDK_INT >= 35) {
        reflectDisplayBrightness(context)?.let { return it }
    }
    try {
        val f = Settings.System.getFloat(
            context.contentResolver, SCREEN_BRIGHTNESS_FLOAT_KEY, -1f
        )
        if (f >= 0f) return (f * 255f).toInt()
    } catch (_: Exception) {
        // No float key on this ROM — fall through.
    }
    return try {
        Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
    } catch (_: Exception) {
        null
    }
}

/**
 * `Display#getBrightnessInfo` is public API only from SDK 35, and the
 * project still compiles against SDK 34 — until the toolchain moves, this
 * is a tiny reflective peek. The display is fetched from DisplayManager:
 * a bare context's `display` is often null even inside an activity. Any
 * shape surprise → null and the settings fallbacks take over.
 */
// Logged once per process: the probe result never changes at runtime.
private var probeFailureLogged = false

private fun reflectDisplayBrightness(context: Context): Int? = try {
    val dm = context.getSystemService(Context.DISPLAY_SERVICE) as android.hardware.display.DisplayManager
    val display: Display? = dm.getDisplay(Display.DEFAULT_DISPLAY)
    if (display == null) {
        null
    } else {
        val info = Display::class.java.getMethod("getBrightnessInfo").invoke(display)
        val value = info.javaClass.getField("brightness").getFloat(info)
        (value * 255f).toInt()
    }
} catch (t: Throwable) {
    if (!probeFailureLogged) {
        probeFailureLogged = true
        android.util.Log.w(
            "AmbientLight",
            "brightnessInfo unavailable (${t.javaClass.simpleName}); falling back to settings/sensor",
        )
    }
    null
}

// Brightness range is 0..255 by convention.
private const val DARK_THRESHOLD = 100
private const val LIGHT_THRESHOLD = 156

// Ambient light sensor bands for the adaptive-brightness tiebreak: below
// 10 lux is a dim room; above 250 lux is a decisively lit one.
internal const val DARK_LUX = 10f
internal const val LIGHT_LUX = 250f
