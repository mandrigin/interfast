package com.interfast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.interfast.data.preferences.UserPreferences
import com.interfast.ui.navigation.InterfastNavigation
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val darkModePreference by userPreferences.darkMode.collectAsState(initial = "dark")
            val isSystemDark = isSystemInDarkTheme()

            val useDarkTheme = when (darkModePreference) {
                "dark" -> true
                "light" -> false
                "system" -> isSystemDark
                else -> true
            }

            InterfastTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = if (useDarkTheme) InterfastColors.VoidBlack else InterfastColors.PureWhite
                ) {
                    InterfastNavigation()
                }
            }
        }
    }
}
