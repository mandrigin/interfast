package com.interfast.ui.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlin.math.sqrt

/**
 * Shake detector for quick-start gesture.
 *
 * Design rationale: Users should be able to start a fast with minimal friction.
 * Pick up phone, shake, fast starts. No unlocking, no navigating.
 *
 * The shake detection uses accelerometer data with configurable sensitivity.
 * A debounce mechanism prevents accidental triggers.
 */
class ShakeDetector(
    private val context: Context,
    private val onShake: () -> Unit,
    private val shakeThreshold: Float = SHAKE_THRESHOLD_DEFAULT,
    private val shakeDebounceDurationMs: Long = SHAKE_DEBOUNCE_MS
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShakeTime: Long = 0
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f
    private var lastUpdate: Long = 0
    private var isFirstReading = true

    /**
     * Start listening for shake events.
     */
    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        isFirstReading = true
    }

    /**
     * Stop listening for shake events.
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val currentTime = System.currentTimeMillis()

        // Skip first reading to establish baseline
        if (isFirstReading) {
            lastX = event.values[0]
            lastY = event.values[1]
            lastZ = event.values[2]
            lastUpdate = currentTime
            isFirstReading = false
            return
        }

        // Rate limit sensor processing
        val timeDelta = currentTime - lastUpdate
        if (timeDelta < SENSOR_UPDATE_INTERVAL_MS) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Calculate acceleration change
        val deltaX = x - lastX
        val deltaY = y - lastY
        val deltaZ = z - lastZ

        lastX = x
        lastY = y
        lastZ = z
        lastUpdate = currentTime

        // Calculate magnitude of acceleration change
        val acceleration = sqrt(
            deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ
        ) / timeDelta * 10000

        // Check if shake threshold exceeded
        if (acceleration > shakeThreshold) {
            // Debounce: ignore shakes within debounce window
            if (currentTime - lastShakeTime > shakeDebounceDurationMs) {
                lastShakeTime = currentTime
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    companion object {
        // Default shake sensitivity (higher = less sensitive)
        const val SHAKE_THRESHOLD_DEFAULT = 800f

        // Minimum time between shake triggers
        const val SHAKE_DEBOUNCE_MS = 1500L

        // Sensor sampling interval
        private const val SENSOR_UPDATE_INTERVAL_MS = 100L
    }
}

/**
 * Composable hook for shake detection with lifecycle awareness.
 *
 * Usage:
 * ```
 * ShakeToStart(
 *     enabled = shakeEnabled,
 *     onShake = { viewModel.startFast() }
 * )
 * ```
 */
@Composable
fun ShakeToStart(
    enabled: Boolean,
    onShake: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val shakeDetector = remember(context, onShake) {
        ShakeDetector(context, onShake)
    }

    DisposableEffect(lifecycleOwner, enabled) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (enabled) {
                        shakeDetector.start()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    shakeDetector.stop()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        // Start immediately if already resumed and enabled
        if (enabled && lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            shakeDetector.start()
        }

        onDispose {
            shakeDetector.stop()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * State holder for shake detection settings.
 */
@Composable
fun rememberShakeState(
    initialEnabled: Boolean = false
): ShakeState {
    var enabled by remember { mutableStateOf(initialEnabled) }
    return remember(enabled) {
        ShakeState(
            enabled = enabled,
            setEnabled = { enabled = it }
        )
    }
}

data class ShakeState(
    val enabled: Boolean,
    val setEnabled: (Boolean) -> Unit
)
