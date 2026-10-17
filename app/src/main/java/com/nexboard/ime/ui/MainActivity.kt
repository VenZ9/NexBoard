package com.nexboard.ime.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.nexboard.ime.data.settings.UserPreferences
import com.nexboard.ime.data.settings.UserPreferencesRepository
import com.nexboard.ime.ui.settings.SettingsScreen
import com.nexboard.ime.ui.theme.NexBoardTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userPreferencesRepo = UserPreferencesRepository(applicationContext)

        setContent {
            val preferences by userPreferencesRepo.userPreferencesFlow
                .collectAsState(initial = UserPreferences())

            NexBoardTheme(themeMode = preferences.themeMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SettingsScreen(
                        userPreferencesRepo = userPreferencesRepo,
                        currentPreferences = preferences,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
