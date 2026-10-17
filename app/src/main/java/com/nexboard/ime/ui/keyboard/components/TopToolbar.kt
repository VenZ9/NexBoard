package com.nexboard.ime.ui.keyboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexboard.ime.data.settings.KeyboardMode
import com.nexboard.ime.ui.keyboard.KeyboardPanel
import com.nexboard.ime.ui.theme.LocalKeyboardColors

@Composable
fun TopToolbar(
    currentPanel: KeyboardPanel,
    currentMode: KeyboardMode,
    onPanelSelect: (KeyboardPanel) -> Unit,
    onModeSelect: (KeyboardMode) -> Unit,
    onSwitchIme: () -> Unit,
    onCloseKeyboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalKeyboardColors.current
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(colors.surface)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Main feature buttons: [Emoji], [GIFs], [Text Editing], [Clipboard]
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Keyboard toggle (when inside subpanels)
            if (currentPanel != KeyboardPanel.QWERTY) {
                ToolbarButton(
                    icon = Icons.Default.Keyboard,
                    contentDescription = "Keyboard",
                    isSelected = false,
                    onClick = { onPanelSelect(KeyboardPanel.QWERTY) }
                )
            }

            ToolbarButton(
                icon = Icons.Default.Mood,
                contentDescription = "Emoji",
                isSelected = currentPanel == KeyboardPanel.EMOJI,
                onClick = {
                    onPanelSelect(
                        if (currentPanel == KeyboardPanel.EMOJI) KeyboardPanel.QWERTY else KeyboardPanel.EMOJI
                    )
                }
            )

            ToolbarButton(
                icon = Icons.Default.Gif,
                contentDescription = "GIFs",
                isSelected = currentPanel == KeyboardPanel.GIF,
                onClick = {
                    onPanelSelect(
                        if (currentPanel == KeyboardPanel.GIF) KeyboardPanel.QWERTY else KeyboardPanel.GIF
                    )
                }
            )

            ToolbarButton(
                icon = Icons.Default.Edit,
                contentDescription = "Text Editing",
                isSelected = currentPanel == KeyboardPanel.TEXT_EDIT,
                onClick = {
                    onPanelSelect(
                        if (currentPanel == KeyboardPanel.TEXT_EDIT) KeyboardPanel.QWERTY else KeyboardPanel.TEXT_EDIT
                    )
                }
            )

            ToolbarButton(
                icon = Icons.Default.Assignment,
                contentDescription = "Clipboard",
                isSelected = currentPanel == KeyboardPanel.CLIPBOARD,
                onClick = {
                    onPanelSelect(
                        if (currentPanel == KeyboardPanel.CLIPBOARD) KeyboardPanel.QWERTY else KeyboardPanel.CLIPBOARD
                    )
                }
            )
        }

        // Action buttons: Switch IME, Layout Mode Menu, Hide Keyboard
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onSwitchIme,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Switch Input Method",
                    tint = colors.actionKeyText.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Layout Modes",
                        tint = colors.actionKeyText.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Standard Mode") },
                        leadingIcon = { Icon(Icons.Default.StayCurrentPortrait, null) },
                        onClick = {
                            onModeSelect(KeyboardMode.NORMAL)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("One-Handed (Left)") },
                        leadingIcon = { Icon(Icons.Default.StayCurrentPortrait, null) },
                        onClick = {
                            onModeSelect(KeyboardMode.ONE_HANDED_LEFT)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("One-Handed (Right)") },
                        leadingIcon = { Icon(Icons.Default.StayCurrentPortrait, null) },
                        onClick = {
                            onModeSelect(KeyboardMode.ONE_HANDED_RIGHT)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Split Keyboard") },
                        leadingIcon = { Icon(Icons.Default.Splitscreen, null) },
                        onClick = {
                            onModeSelect(KeyboardMode.SPLIT)
                            showMenu = false
                        }
                    )
                }
            }

            IconButton(
                onClick = onCloseKeyboard,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Hide Keyboard",
                    tint = colors.actionKeyText.copy(alpha = 0.8f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun ToolbarButton(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalKeyboardColors.current
    val background = if (isSelected) colors.accent.copy(alpha = 0.25f) else colors.surface
    val tint = if (isSelected) colors.accent else colors.actionKeyText

    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}
