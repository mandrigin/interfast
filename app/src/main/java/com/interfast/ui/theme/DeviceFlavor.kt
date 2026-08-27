package com.interfast.ui.theme

import android.os.Build

/**
 * Hardware nods. The deck keeps one look on every device; a Fairphone unit
 * earns a single glow mark acknowledging its Essential Key — nothing else
 * moves. Detection is lazy so unit tests never touch android.os.Build.
 */
object DeviceFlavor {
    // TEMP QA — render the Fairphone nod on the emulator for screenshots.
    private const val QA_FORCE_FAIRPHONE = false

    val isFairphone: Boolean by lazy {
        QA_FORCE_FAIRPHONE || Build.MANUFACTURER.equals("Fairphone", ignoreCase = true)
    }

    /** Short model tag for the edition stamp ("FP6"), or null off-Fairphone. */
    val fairphoneTag: String? by lazy {
        if (QA_FORCE_FAIRPHONE) return@lazy "FP6"
        if (!isFairphone) return@lazy null
        val device = Build.DEVICE ?: ""
        when {
            device.startsWith("FP6", ignoreCase = true) -> "FP6"
            device.startsWith("FP5", ignoreCase = true) -> "FP5"
            device.startsWith("FP4", ignoreCase = true) -> "FP4"
            else -> "FP"
        }
    }
}
