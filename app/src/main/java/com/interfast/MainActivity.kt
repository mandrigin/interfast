package com.interfast

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.interfast.ui.scrubber.ScrubberScreen
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.LocalSurfaceTokens
import com.interfast.ui.theme.rememberAmbientDarkTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val dark = rememberAmbientDarkTheme()
            InterfastTheme(darkTheme = dark) {
                val tokens = LocalSurfaceTokens.current
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = tokens.background,
                ) {
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

                    ScrubberScreen(notificationsGranted = notificationsGranted)
                }
            }
        }
    }
}
