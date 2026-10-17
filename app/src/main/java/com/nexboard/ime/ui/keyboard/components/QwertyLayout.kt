package com.nexboard.ime.ui.keyboard.components

import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexboard.ime.service.KeyboardActionHandler
import com.nexboard.ime.ui.keyboard.ShiftState
import com.nexboard.ime.ui.keyboard.SymbolsState
import com.nexboard.ime.ui.theme.LocalKeyboardColors

@Composable
fun QwertyLayout(
    actionHandler: KeyboardActionHandler,
    shiftState: ShiftState,
    symbolsState: SymbolsState,
    onToggleShift: () -> Unit,
    onResetTemporaryShift: () -> Unit,
    onToggleSymbols: () -> Unit,
    onSetSymbolsState: (SymbolsState) -> Unit,
    editorInfo: EditorInfo?,
    isSplit: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalKeyboardColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        when (symbolsState) {
            SymbolsState.LETTERS -> {
                LettersLayout(
                    actionHandler = actionHandler,
                    shiftState = shiftState,
                    onToggleShift = onToggleShift,
                    onResetTemporaryShift = onResetTemporaryShift,
                    onToggleSymbols = onToggleSymbols,
                    editorInfo = editorInfo,
                    isSplit = isSplit
                )
            }
            SymbolsState.SYMBOLS -> {
                SymbolsLayout(
                    actionHandler = actionHandler,
                    onSwitchToLetters = { onSetSymbolsState(SymbolsState.LETTERS) },
                    onSwitchToMoreSymbols = { onSetSymbolsState(SymbolsState.MORE_SYMBOLS) },
                    editorInfo = editorInfo,
                    isSplit = isSplit
                )
            }
            SymbolsState.MORE_SYMBOLS -> {
                MoreSymbolsLayout(
                    actionHandler = actionHandler,
                    onSwitchToLetters = { onSetSymbolsState(SymbolsState.LETTERS) },
                    onSwitchToSymbols = { onSetSymbolsState(SymbolsState.SYMBOLS) },
                    editorInfo = editorInfo,
                    isSplit = isSplit
                )
            }
        }
    }
}

@Composable
private fun LettersLayout(
    actionHandler: KeyboardActionHandler,
    shiftState: ShiftState,
    onToggleShift: () -> Unit,
    onResetTemporaryShift: () -> Unit,
    onToggleSymbols: () -> Unit,
    editorInfo: EditorInfo?,
    isSplit: Boolean
) {
    val colors = LocalKeyboardColors.current
    val isUpper = shiftState != ShiftState.UN_SHIFTED

    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val row3 = listOf("z", "x", "c", "v", "b", "n", "m")

    // Row 0: Numbers Row
    Row(modifier = Modifier.fillMaxWidth()) {
        val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        if (isSplit) {
            numbers.take(5).forEach { num ->
                KeyButton(
                    label = num,
                    height = 38.dp,
                    fontSize = 15.sp,
                    onClick = { actionHandler.onKey(num[0]) }
                )
            }
            Spacer(modifier = Modifier.weight(1.5f))
            numbers.drop(5).forEach { num ->
                KeyButton(
                    label = num,
                    height = 38.dp,
                    fontSize = 15.sp,
                    onClick = { actionHandler.onKey(num[0]) }
                )
            }
        } else {
            numbers.forEach { num ->
                KeyButton(
                    label = num,
                    height = 38.dp,
                    fontSize = 15.sp,
                    onClick = { actionHandler.onKey(num[0]) }
                )
            }
        }
    }

    // Row 1: Q W E R T Y U I O P
    Row(modifier = Modifier.fillMaxWidth()) {
        if (isSplit) {
            row1.take(5).forEach { char ->
                val display = if (isUpper) char.uppercase() else char
                KeyButton(
                    label = display,
                    onClick = {
                        actionHandler.onKey(display[0])
                        onResetTemporaryShift()
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1.5f))
            row1.drop(5).forEach { char ->
                val display = if (isUpper) char.uppercase() else char
                KeyButton(
                    label = display,
                    onClick = {
                        actionHandler.onKey(display[0])
                        onResetTemporaryShift()
                    }
                )
            }
        } else {
            row1.forEach { char ->
                val display = if (isUpper) char.uppercase() else char
                KeyButton(
                    label = display,
                    onClick = {
                        actionHandler.onKey(display[0])
                        onResetTemporaryShift()
                    }
                )
            }
        }
    }

    // Row 2: A S D F G H J K L
    Row(modifier = Modifier.fillMaxWidth()) {
        if (!isSplit) {
            Spacer(modifier = Modifier.weight(0.5f))
        }
        if (isSplit) {
            row2.take(5).forEach { char ->
                val display = if (isUpper) char.uppercase() else char
                KeyButton(
                    label = display,
                    onClick = {
                        actionHandler.onKey(display[0])
                        onResetTemporaryShift()
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1.5f))
            row2.drop(5).forEach { char ->
                val display = if (isUpper) char.uppercase() else char
                KeyButton(
                    label = display,
                    onClick = {
                        actionHandler.onKey(display[0])
                        onResetTemporaryShift()
                    }
                )
            }
        } else {
            row2.forEach { char ->
                val display = if (isUpper) char.uppercase() else char
                KeyButton(
                    label = display,
                    onClick = {
                        actionHandler.onKey(display[0])
                        onResetTemporaryShift()
                    }
                )
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }
    }

    // Row 3: Shift, Z X C V B N M, Backspace
    Row(modifier = Modifier.fillMaxWidth()) {
        // Shift Key
        val shiftBg = if (shiftState != ShiftState.UN_SHIFTED) colors.accent else colors.actionKeyBackground
        val shiftTint = if (shiftState != ShiftState.UN_SHIFTED) Color.White else colors.actionKeyText
        KeyButton(
            weight = 1.5f,
            backgroundColor = shiftBg,
            onClick = onToggleShift
        ) {
            Box(contentAlignment = Alignment.Center) {
                when (shiftState) {
                    ShiftState.UN_SHIFTED -> Icon(
                        imageVector = Icons.Outlined.ArrowUpward,
                        contentDescription = "Shift",
                        tint = shiftTint,
                        modifier = Modifier.size(20.dp)
                    )
                    ShiftState.SHIFTED -> Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Shift On",
                        tint = shiftTint,
                        modifier = Modifier.size(20.dp)
                    )
                    ShiftState.CAPS_LOCKED -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Caps Lock",
                            tint = shiftTint,
                            modifier = Modifier.size(18.dp)
                        )
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(2.dp)
                                .background(shiftTint)
                        )
                    }
                }
            }
        }

        if (isSplit) {
            row3.take(3).forEach { char ->
                val display = if (isUpper) char.uppercase() else char
                KeyButton(
                    label = display,
                    onClick = {
                        actionHandler.onKey(display[0])
                        onResetTemporaryShift()
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1.5f))
            row3.drop(3).forEach { char ->
                val display = if (isUpper) char.uppercase() else char
                KeyButton(
                    label = display,
                    onClick = {
                        actionHandler.onKey(display[0])
                        onResetTemporaryShift()
                    }
                )
            }
        } else {
            row3.forEach { char ->
                val display = if (isUpper) char.uppercase() else char
                KeyButton(
                    label = display,
                    onClick = {
                        actionHandler.onKey(display[0])
                        onResetTemporaryShift()
                    }
                )
            }
        }

        // Backspace Key
        KeyButton(
            weight = 1.5f,
            backgroundColor = colors.actionKeyBackground,
            onClick = { actionHandler.onBackspace() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace",
                tint = colors.actionKeyText,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    // Row 4: ?123, Comma, Space, Period, Enter/Action
    Row(modifier = Modifier.fillMaxWidth()) {
        KeyButton(
            label = "?123",
            weight = 1.5f,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            backgroundColor = colors.actionKeyBackground,
            textColor = colors.actionKeyText,
            onClick = onToggleSymbols
        )

        KeyButton(
            label = ",",
            weight = 1.0f,
            fontSize = 18.sp,
            onClick = { actionHandler.onKey(',') }
        )

        KeyButton(
            label = "space",
            weight = 4.5f,
            fontSize = 13.sp,
            onClick = { actionHandler.onSpace() }
        )

        KeyButton(
            label = ".",
            weight = 1.0f,
            fontSize = 18.sp,
            onClick = { actionHandler.onKey('.') }
        )

        EnterKey(
            editorInfo = editorInfo,
            weight = 2.0f,
            onClick = { actionHandler.onEnter() }
        )
    }
}

@Composable
private fun SymbolsLayout(
    actionHandler: KeyboardActionHandler,
    onSwitchToLetters: () -> Unit,
    onSwitchToMoreSymbols: () -> Unit,
    editorInfo: EditorInfo?,
    isSplit: Boolean
) {
    val colors = LocalKeyboardColors.current
    val row1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val row2 = listOf("@", "#", "$", "%", "&", "*", "-", "+", "(", ")")
    val row3 = listOf("!", "\"", "'", ":", ";", "/", "?")

    Row(modifier = Modifier.fillMaxWidth()) {
        row1.forEach { char ->
            KeyButton(label = char, onClick = { actionHandler.onText(char) })
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        row2.forEach { char ->
            KeyButton(label = char, onClick = { actionHandler.onText(char) })
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        KeyButton(
            label = "=\\<",
            weight = 1.5f,
            fontSize = 14.sp,
            backgroundColor = colors.actionKeyBackground,
            textColor = colors.actionKeyText,
            onClick = onSwitchToMoreSymbols
        )

        row3.forEach { char ->
            KeyButton(label = char, onClick = { actionHandler.onText(char) })
        }

        KeyButton(
            weight = 1.5f,
            backgroundColor = colors.actionKeyBackground,
            onClick = { actionHandler.onBackspace() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace",
                tint = colors.actionKeyText,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        KeyButton(
            label = "ABC",
            weight = 1.5f,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            backgroundColor = colors.actionKeyBackground,
            textColor = colors.actionKeyText,
            onClick = onSwitchToLetters
        )

        KeyButton(label = ",", weight = 1.0f, onClick = { actionHandler.onKey(',') })
        KeyButton(label = "space", weight = 4.5f, fontSize = 13.sp, onClick = { actionHandler.onSpace() })
        KeyButton(label = ".", weight = 1.0f, onClick = { actionHandler.onKey('.') })

        EnterKey(
            editorInfo = editorInfo,
            weight = 2.0f,
            onClick = { actionHandler.onEnter() }
        )
    }
}

@Composable
private fun MoreSymbolsLayout(
    actionHandler: KeyboardActionHandler,
    onSwitchToLetters: () -> Unit,
    onSwitchToSymbols: () -> Unit,
    editorInfo: EditorInfo?,
    isSplit: Boolean
) {
    val colors = LocalKeyboardColors.current
    val row1 = listOf("~", "`", "|", "^", "_", "=", "{", "}", "[", "]")
    val row2 = listOf("<", ">", "€", "£", "¥", "₹", "¢", "§", "°", "·")
    val row3 = listOf("«", "»", "©", "®", "™", "…", "¿")

    Row(modifier = Modifier.fillMaxWidth()) {
        row1.forEach { char ->
            KeyButton(label = char, onClick = { actionHandler.onText(char) })
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        row2.forEach { char ->
            KeyButton(label = char, onClick = { actionHandler.onText(char) })
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        KeyButton(
            label = "?123",
            weight = 1.5f,
            fontSize = 14.sp,
            backgroundColor = colors.actionKeyBackground,
            textColor = colors.actionKeyText,
            onClick = onSwitchToSymbols
        )

        row3.forEach { char ->
            KeyButton(label = char, onClick = { actionHandler.onText(char) })
        }

        KeyButton(
            weight = 1.5f,
            backgroundColor = colors.actionKeyBackground,
            onClick = { actionHandler.onBackspace() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace",
                tint = colors.actionKeyText,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        KeyButton(
            label = "ABC",
            weight = 1.5f,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            backgroundColor = colors.actionKeyBackground,
            textColor = colors.actionKeyText,
            onClick = onSwitchToLetters
        )

        KeyButton(label = ",", weight = 1.0f, onClick = { actionHandler.onKey(',') })
        KeyButton(label = "space", weight = 4.5f, fontSize = 13.sp, onClick = { actionHandler.onSpace() })
        KeyButton(label = ".", weight = 1.0f, onClick = { actionHandler.onKey('.') })

        EnterKey(
            editorInfo = editorInfo,
            weight = 2.0f,
            onClick = { actionHandler.onEnter() }
        )
    }
}

@Composable
private fun RowScope.EnterKey(
    editorInfo: EditorInfo?,
    weight: Float,
    onClick: () -> Unit
) {
    val colors = LocalKeyboardColors.current
    val imeAction = editorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE

    KeyButton(
        weight = weight,
        backgroundColor = colors.accent,
        onClick = onClick
    ) {
        when (imeAction) {
            EditorInfo.IME_ACTION_SEARCH -> Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            EditorInfo.IME_ACTION_SEND -> Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            EditorInfo.IME_ACTION_GO -> Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            EditorInfo.IME_ACTION_DONE -> Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Done",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            else -> Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                contentDescription = "Enter",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
