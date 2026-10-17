package com.nexboard.ime.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "nexboard_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val SYSTEM_SOUND_RESPECTED = booleanPreferencesKey("system_sound_respected")
        val SOUND_VOLUME = floatPreferencesKey("sound_volume")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val KEYBOARD_MODE = stringPreferencesKey("keyboard_mode")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.userDataStore.data
        .map { preferences ->
            val soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true
            val systemSoundRespected = preferences[PreferencesKeys.SYSTEM_SOUND_RESPECTED] ?: true
            val soundVolume = preferences[PreferencesKeys.SOUND_VOLUME] ?: 1.0f
            val hapticEnabled = preferences[PreferencesKeys.HAPTIC_ENABLED] ?: true
            val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val keyboardModeStr = preferences[PreferencesKeys.KEYBOARD_MODE] ?: KeyboardMode.NORMAL.name

            val themeMode = try {
                ThemeMode.valueOf(themeModeStr)
            } catch (_: Exception) {
                ThemeMode.SYSTEM
            }

            val keyboardMode = try {
                KeyboardMode.valueOf(keyboardModeStr)
            } catch (_: Exception) {
                KeyboardMode.NORMAL
            }

            UserPreferences(
                soundEnabled = soundEnabled,
                systemSoundRespected = systemSoundRespected,
                soundVolume = soundVolume,
                hapticEnabled = hapticEnabled,
                themeMode = themeMode,
                keyboardMode = keyboardMode
            )
        }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.userDataStore.edit { it[PreferencesKeys.SOUND_ENABLED] = enabled }
    }

    suspend fun updateSystemSoundRespected(respected: Boolean) {
        context.userDataStore.edit { it[PreferencesKeys.SYSTEM_SOUND_RESPECTED] = respected }
    }

    suspend fun updateSoundVolume(volume: Float) {
        context.userDataStore.edit { it[PreferencesKeys.SOUND_VOLUME] = volume }
    }

    suspend fun updateHapticEnabled(enabled: Boolean) {
        context.userDataStore.edit { it[PreferencesKeys.HAPTIC_ENABLED] = enabled }
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        context.userDataStore.edit { it[PreferencesKeys.THEME_MODE] = mode.name }
    }

    suspend fun updateKeyboardMode(mode: KeyboardMode) {
        context.userDataStore.edit { it[PreferencesKeys.KEYBOARD_MODE] = mode.name }
    }
}
