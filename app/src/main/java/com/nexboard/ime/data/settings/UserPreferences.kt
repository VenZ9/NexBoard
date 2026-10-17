package com.nexboard.ime.data.settings

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    AMOLED
}

enum class KeyboardMode {
    NORMAL,
    ONE_HANDED_LEFT,
    ONE_HANDED_RIGHT,
    SPLIT
}

data class UserPreferences(
    val soundEnabled: Boolean = true,
    val systemSoundRespected: Boolean = true,
    val soundVolume: Float = 1.0f,
    val hapticEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val keyboardMode: KeyboardMode = KeyboardMode.NORMAL
)
