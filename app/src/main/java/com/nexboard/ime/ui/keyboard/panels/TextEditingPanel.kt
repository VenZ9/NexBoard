package com.nexboard.ime.ui.keyboard.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LastPage
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexboard.ime.service.DpadDirection
import com.nexboard.ime.service.KeyboardActionHandler
import com.nexboard.ime.ui.theme.LocalKeyboardColors

@Composable
fun TextEditingPanel(
    actionHandler: KeyboardActionHandler,
    onBackToKeyboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalKeyboardColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(colors.background)
            .padding(8.dp)
    ) {
        // Top Bar: Back to Keyboard and Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surface)
                    .clickable(onClick = onBackToKeyboard)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "ABC",
                        tint = colors.actionKeyText,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Keyboard",
                        color = colors.actionKeyText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            IconButton(
                onClick = { actionHandler.onBackspace() },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surface)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = colors.actionKeyText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Main layout: Left column with Action Buttons, Right column with D-Pad
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Action Buttons Grid (Select, Select All, Cut, Copy, Paste, Home, End)
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ActionTile(
                        label = "Select",
                        icon = null,
                        modifier = Modifier.weight(1f),
                        onClick = { actionHandler.onSelect() }
                    )
                    ActionTile(
                        label = "Select All",
                        icon = Icons.Default.SelectAll,
                        modifier = Modifier.weight(1f),
                        onClick = { actionHandler.onSelectAll() }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ActionTile(
                        label = "Cut",
                        icon = Icons.Default.ContentCut,
                        modifier = Modifier.weight(1f),
                        onClick = { actionHandler.onCut() }
                    )
                    ActionTile(
                        label = "Copy",
                        icon = Icons.Default.ContentCopy,
                        modifier = Modifier.weight(1f),
                        onClick = { actionHandler.onCopy() }
                    )
                    ActionTile(
                        label = "Paste",
                        icon = Icons.Default.ContentPaste,
                        modifier = Modifier.weight(1f),
                        onClick = { actionHandler.onPaste() }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ActionTile(
                        label = "Home",
                        icon = Icons.Default.FirstPage,
                        modifier = Modifier.weight(1f),
                        onClick = { actionHandler.onHome() }
                    )
                    ActionTile(
                        label = "End",
                        icon = Icons.Default.LastPage,
                        modifier = Modifier.weight(1f),
                        onClick = { actionHandler.onEnd() }
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // D-Pad Controller
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // UP
                    DpadButton(
                        icon = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Up",
                        onClick = { actionHandler.onDpad(DpadDirection.UP) }
                    )

                    // LEFT - CENTER - RIGHT
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DpadButton(
                            icon = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Left",
                            onClick = { actionHandler.onDpad(DpadDirection.LEFT) }
                        )

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(colors.accent)
                                .clickable { actionHandler.onSelect() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SEL",
                                color = androidx.compose.ui.graphics.Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DpadButton(
                            icon = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Right",
                            onClick = { actionHandler.onDpad(DpadDirection.RIGHT) }
                        )
                    }

                    // DOWN
                    DpadButton(
                        icon = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Down",
                        onClick = { actionHandler.onDpad(DpadDirection.DOWN) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionTile(
    label: String,
    icon: ImageVector?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = LocalKeyboardColors.current

    Box(
        modifier = modifier
            .height(44.dp)
            .shadow(1.5.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(colors.keyBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = colors.accent,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = label,
                color = colors.keyText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DpadButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    val colors = LocalKeyboardColors.current

    Box(
        modifier = Modifier
            .size(48.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(colors.actionKeyBackground)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = colors.actionKeyText,
            modifier = Modifier.size(28.dp)
        )
    }
}
