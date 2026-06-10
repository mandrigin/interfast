package com.interfast

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.interfast.ui.rear.RearPanel
import com.interfast.ui.scrubber.ScrubberScreen
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.LocalSurfaceTokens
import com.interfast.ui.theme.rememberAmbientDarkTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val dark = rememberAmbientDarkTheme()
            InterfastTheme(darkTheme = dark) {
                val tokens = LocalSurfaceTokens.current
                var showRear by rememberSaveable { mutableStateOf(false) }

                // The rear is modal state: system back flips home, it never
                // exits the app from the manual.
                BackHandler(enabled = showRear) { showRear = false }

                val rotation by animateFloatAsState(
                    targetValue = if (showRear) 180f else 0f,
                    animationSpec = tween(durationMillis = 600),
                    label = "flip",
                )
                // The card shows its rear face past 90° — that exact moment,
                // not the flip's start or end, is when the bars must swap to
                // paper. Anything else reads as a glitch.
                val rearFaceVisible = rotation > 90f

                val view = LocalView.current
                LaunchedEffect(rearFaceVisible, dark) {
                    val window = this@MainActivity.window
                    val paper = android.graphics.Color.parseColor("#EFE9DC")
                    val bg = if (rearFaceVisible) paper else tokens.background.toArgb()
                    window.statusBarColor = bg
                    window.navigationBarColor = bg
                    WindowCompat.getInsetsController(window, view).apply {
                        isAppearanceLightStatusBars = rearFaceVisible || !dark
                        isAppearanceLightNavigationBars = rearFaceVisible || !dark
                    }
                }

                FlippableUnit(
                    rotation = rotation,
                    front = {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = tokens.background,
                        ) {
                            FrontContent(onFlipToRear = { showRear = true })
                        }
                    },
                    rear = {
                        RearPanel(
                            edition = "N° " + LocalDate.now().dayOfYear.toString().padStart(4, '0'),
                            onFlipBack = { showRear = false },
                        )
                    },
                )
            }
        }
    }
}

/**
 * Card flip between the deck and its printed back. The rear composable is
 * pre-mirrored so it reads correctly once the card passes 90°. Rotation is
 * hoisted so the caller can sync window chrome to the visible face.
 */
@Composable
private fun FlippableUnit(
    rotation: Float,
    front: @Composable () -> Unit,
    rear: @Composable () -> Unit,
) {
    val density = LocalDensity.current.density
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 18f * density
            },
    ) {
        if (rotation <= 90f) {
            front()
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f },
            ) {
                rear()
            }
        }
    }
}

@Composable
private fun FrontContent(onFlipToRear: () -> Unit) {
    val context = LocalContext.current
    var notificationsGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationsGranted = granted
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !notificationsGranted
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    ScrubberScreen(
        notificationsGranted = notificationsGranted,
        onFlipToRear = onFlipToRear,
    )
}
