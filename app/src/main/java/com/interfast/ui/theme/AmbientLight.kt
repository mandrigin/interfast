package com.interfast.ui.theme

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/**
 * Returns whether the dark theme should be active based on the system's
 * screen-brightness setting. Below [DARK_THRESHOLD] → dark; above
 * [LIGHT_THRESHOLD] → light. Hysteresis between the two avoids flicker.
 *
 * Falls back to [isSystemInDarkTheme] for the very first frame before the
 * brightness value is read.
 */
@Composable
fun rememberAmbientDarkTheme(): Boolean {
    val context = LocalContext.current
    val resolver = context.contentResolver
    val systemFallback = isSystemInDarkTheme()

    fun read(): Int = try {
        Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
    } catch (e: Settings.SettingNotFoundException) {
        if (systemFallback) DARK_THRESHOLD - 1 else LIGHT_THRESHOLD + 1
    }

    var brightness by remember { mutableStateOf(read()) }

    DisposableEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                brightness = read()
            }
        }
        resolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
            true,
            observer,
        )
        onDispose { resolver.unregisterContentObserver(observer) }
    }

    var dark by remember { mutableStateOf(brightness < (DARK_THRESHOLD + LIGHT_THRESHOLD) / 2) }
    LaunchedEffect(brightness) {
        when {
            brightness < DARK_THRESHOLD && !dark -> dark = true
            brightness > LIGHT_THRESHOLD && dark -> dark = false
        }
    }
    return dark
}

// Brightness range is 0..255 by convention.
private const val DARK_THRESHOLD = 100
private const val LIGHT_THRESHOLD = 156
