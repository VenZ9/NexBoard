package com.nexboard.ime.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.nexboard.ime.data.settings.ThemeMode

data class NexKeyboardColors(
    val background: Color,
    val surface: Color,
    val keyBackground: Color,
    val keyText: Color,
    val actionKeyBackground: Color,
    val actionKeyText: Color,
    val accent: Color
)

val LocalKeyboardColors = staticCompositionLocalOf {
    NexKeyboardColors(
        background = KeyboardBgDark,
        surface = KeyboardBgDark,
        keyBackground = KeyboardKeyDark,
        keyText = Color.White,
        actionKeyBackground = KeyboardActionKeyDark,
        actionKeyText = Color.White,
        accent = AccentBlue
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = KeyboardBgDark,
    surface = KeyboardBgDark
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = KeyboardBgLight,
    surface = KeyboardBgLight
)

private val AmoledColorScheme = darkColorScheme(
    primary = AccentBlueVariant,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = KeyboardBgAmoled,
    surface = KeyboardBgAmoled
)

@Composable
fun NexBoardTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.AMOLED -> true
    }

    val context = LocalContext.current
    val supportsDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = when {
        themeMode == ThemeMode.AMOLED -> AmoledColorScheme
        supportsDynamic && isDark -> dynamicDarkColorScheme(context)
        supportsDynamic && !isDark -> dynamicLightColorScheme(context)
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val keyboardColors = when {
        themeMode == ThemeMode.AMOLED -> NexKeyboardColors(
            background = KeyboardBgAmoled,
            surface = Color(0xFF0D0D0D),
            keyBackground = KeyboardKeyAmoled,
            keyText = Color.White,
            actionKeyBackground = KeyboardActionKeyAmoled,
            actionKeyText = Color(0xFFE0E0E0),
            accent = AccentBlueVariant
        )
        isDark -> NexKeyboardColors(
            background = colorScheme.surface,
            surface = colorScheme.surfaceVariant,
            keyBackground = KeyboardKeyDark,
            keyText = colorScheme.onSurface,
            actionKeyBackground = KeyboardActionKeyDark,
            actionKeyText = colorScheme.onSurfaceVariant,
            accent = colorScheme.primary
        )
        else -> NexKeyboardColors(
            background = KeyboardBgLight,
            surface = colorScheme.surfaceVariant,
            keyBackground = KeyboardKeyLight,
            keyText = colorScheme.onSurface,
            actionKeyBackground = KeyboardActionKeyLight,
            actionKeyText = colorScheme.onSurfaceVariant,
            accent = colorScheme.primary
        )
    }

    CompositionLocalProvider(LocalKeyboardColors provides keyboardColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
