package com.nexboard.ime.ui.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexboard.ime.data.settings.KeyboardMode
import com.nexboard.ime.data.settings.ThemeMode
import com.nexboard.ime.data.settings.UserPreferences
import com.nexboard.ime.data.settings.UserPreferencesRepository
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    userPreferencesRepo: UserPreferencesRepository,
    currentPreferences: UserPreferences,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var isEnabledInSettings by remember { mutableStateOf(false) }
    var isDefaultKeyboard by remember { mutableStateOf(false) }
    var testInputText by remember { mutableStateOf("") }

    fun refreshImeStatus() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val enabledMethods = imm?.enabledInputMethodList ?: emptyList()
        isEnabledInSettings = enabledMethods.any { it.packageName == context.packageName }

        val defaultIme = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        ) ?: ""
        isDefaultKeyboard = defaultIme.contains(context.packageName)
    }

    LaunchedEffect(Unit) {
        refreshImeStatus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column {
                Text(
                    text = "NexBoard",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Material You Keyboard with Custom iPhone Keypress Sound",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Setup Wizard Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Setup & Activation",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Step 1: Enable
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isEnabledInSettings) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isEnabledInSettings) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                        Column {
                            Text("1. Enable NexBoard", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("Activate in system settings", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }

                    Button(
                        onClick = {
                            context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEnabledInSettings) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (isEnabledInSettings) "Enabled" else "Enable", fontSize = 12.sp)
                    }
                }

                HorizontalDivider()

                // Step 2: Choose Default
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isDefaultKeyboard) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isDefaultKeyboard) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                        Column {
                            Text("2. Set as Default", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("Make NexBoard active IME", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }

                    Button(
                        onClick = {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.showInputMethodPicker()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDefaultKeyboard) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (isDefaultKeyboard) "Selected" else "Select", fontSize = 12.sp)
                    }
                }

                HorizontalDivider()

                // Step 3: Interactive Testing Area
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("3. Test Keyboard Below", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    OutlinedTextField(
                        value = testInputText,
                        onValueChange = { testInputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Tap here to test keypress sound & panels...") }
                    )
                }
            }
        }

        // Sound & Haptics Preferences
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.VolumeUp, null, tint = MaterialTheme.colorScheme.primary)
                    Text("Sound & Haptics", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }

                // Sound Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Custom iPhone Keypress Sound", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("Plays res/raw/keypress.wav with maxStreams=5", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    }
                    Switch(
                        checked = currentPreferences.soundEnabled,
                        onCheckedChange = { scope.launch { userPreferencesRepo.updateSoundEnabled(it) } }
                    )
                }

                // Respect System Sound Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Respect System Sound Settings", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("Mute when phone is on silent / vibrate", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    }
                    Switch(
                        checked = currentPreferences.systemSoundRespected,
                        onCheckedChange = { scope.launch { userPreferencesRepo.updateSystemSoundRespected(it) } }
                    )
                }

                // Volume Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sound Volume", fontSize = 13.sp)
                        Text("${(currentPreferences.soundVolume * 100).toInt()}%", fontSize = 13.sp)
                    }
                    Slider(
                        value = currentPreferences.soundVolume,
                        onValueChange = { scope.launch { userPreferencesRepo.updateSoundVolume(it) } },
                        valueRange = 0.1f..1.0f
                    )
                }

                HorizontalDivider()

                // Haptic Feedback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Haptic Feedback", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("Vibrate lightly on keypress", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    }
                    Switch(
                        checked = currentPreferences.hapticEnabled,
                        onCheckedChange = { scope.launch { userPreferencesRepo.updateHapticEnabled(it) } }
                    )
                }
            }
        }

        // Theme Preferences (Monet + AMOLED)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Palette, null, tint = MaterialTheme.colorScheme.primary)
                    Text("Theming & Appearance", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }

                ThemeModeOption(
                    title = "System (Material You Monet)",
                    subtitle = "Dynamic palette adapting to wallpaper",
                    selected = currentPreferences.themeMode == ThemeMode.SYSTEM,
                    onSelect = { scope.launch { userPreferencesRepo.updateThemeMode(ThemeMode.SYSTEM) } }
                )

                ThemeModeOption(
                    title = "Light Theme",
                    subtitle = "Clean high-contrast light theme",
                    selected = currentPreferences.themeMode == ThemeMode.LIGHT,
                    onSelect = { scope.launch { userPreferencesRepo.updateThemeMode(ThemeMode.LIGHT) } }
                )

                ThemeModeOption(
                    title = "Dark Theme",
                    subtitle = "Standard dark grey theme",
                    selected = currentPreferences.themeMode == ThemeMode.DARK,
                    onSelect = { scope.launch { userPreferencesRepo.updateThemeMode(ThemeMode.DARK) } }
                )

                ThemeModeOption(
                    title = "Pure AMOLED Black (#000000)",
                    subtitle = "True pitch black for maximum OLED battery savings",
                    selected = currentPreferences.themeMode == ThemeMode.AMOLED,
                    onSelect = { scope.launch { userPreferencesRepo.updateThemeMode(ThemeMode.AMOLED) } }
                )
            }
        }

        // Layout Mode
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.primary)
                    Text("Keyboard Layout Mode", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }

                KeyboardModeOption(
                    title = "Standard Full Width",
                    selected = currentPreferences.keyboardMode == KeyboardMode.NORMAL,
                    onSelect = { scope.launch { userPreferencesRepo.updateKeyboardMode(KeyboardMode.NORMAL) } }
                )

                KeyboardModeOption(
                    title = "One-Handed (Left Aligned)",
                    selected = currentPreferences.keyboardMode == KeyboardMode.ONE_HANDED_LEFT,
                    onSelect = { scope.launch { userPreferencesRepo.updateKeyboardMode(KeyboardMode.ONE_HANDED_LEFT) } }
                )

                KeyboardModeOption(
                    title = "One-Handed (Right Aligned)",
                    selected = currentPreferences.keyboardMode == KeyboardMode.ONE_HANDED_RIGHT,
                    onSelect = { scope.launch { userPreferencesRepo.updateKeyboardMode(KeyboardMode.ONE_HANDED_RIGHT) } }
                )

                KeyboardModeOption(
                    title = "Split Keyboard (Tablets & Foldables)",
                    selected = currentPreferences.keyboardMode == KeyboardMode.SPLIT,
                    onSelect = { scope.launch { userPreferencesRepo.updateKeyboardMode(KeyboardMode.SPLIT) } }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ThemeModeOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun KeyboardModeOption(
    title: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
