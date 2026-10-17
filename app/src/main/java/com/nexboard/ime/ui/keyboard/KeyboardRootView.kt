package com.nexboard.ime.ui.keyboard

import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nexboard.ime.data.clipboard.ClipboardRepository
import com.nexboard.ime.data.settings.KeyboardMode
import com.nexboard.ime.data.settings.UserPreferencesRepository
import com.nexboard.ime.service.KeyboardActionHandler
import com.nexboard.ime.ui.keyboard.components.QwertyLayout
import com.nexboard.ime.ui.keyboard.components.SuggestionStrip
import com.nexboard.ime.ui.keyboard.components.TopToolbar
import com.nexboard.ime.ui.keyboard.panels.ClipboardPanel
import com.nexboard.ime.ui.keyboard.panels.EmojiPanel
import com.nexboard.ime.ui.keyboard.panels.GifPanel
import com.nexboard.ime.ui.keyboard.panels.TextEditingPanel
import com.nexboard.ime.ui.theme.LocalKeyboardColors
import com.nexboard.ime.ui.theme.NexBoardTheme

@Composable
fun KeyboardRootView(
    actionHandler: KeyboardActionHandler,
    userPreferencesRepo: UserPreferencesRepository,
    clipboardRepo: ClipboardRepository,
    editorInfo: EditorInfo?,
    modifier: Modifier = Modifier
) {
    val viewModel = remember {
        KeyboardViewModel(
            userPreferencesRepo = userPreferencesRepo,
            clipboardRepo = clipboardRepo
        )
    }

    val preferences by viewModel.userPreferences.collectAsState()
    val currentPanel by viewModel.currentPanel.collectAsState()
    val shiftState by viewModel.shiftState.collectAsState()
    val symbolsState by viewModel.symbolsState.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val recentEmojis by viewModel.recentEmojis.collectAsState()
    val gifList by viewModel.gifList.collectAsState()
    val isGifLoading by viewModel.isGifLoading.collectAsState()
    val clips by viewModel.clipboardClips.collectAsState()

    NexBoardTheme(themeMode = preferences.themeMode) {
        val colors = LocalKeyboardColors.current

        Column(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(colors.background)
        ) {
            // Top Toolbar: Toggles for [Emoji], [GIFs], [Text Editing], [Clipboard]
            TopToolbar(
                currentPanel = currentPanel,
                currentMode = preferences.keyboardMode,
                onPanelSelect = { viewModel.setPanel(it) },
                onModeSelect = { viewModel.updateKeyboardMode(it) },
                onSwitchIme = { actionHandler.onSwitchInputMethod() },
                onCloseKeyboard = { actionHandler.onCloseKeyboard() }
            )

            // Suggestion Strip (shown when in QWERTY mode)
            if (currentPanel == KeyboardPanel.QWERTY) {
                SuggestionStrip(
                    suggestions = suggestions,
                    onSuggestionClick = { word ->
                        actionHandler.onText(word)
                        viewModel.updateWordSuggestions("")
                    }
                )
            }

            // Keyboard Container with One-Handed and Split Layout support
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                when (preferences.keyboardMode) {
                    KeyboardMode.NORMAL -> {
                        PanelContent(
                            currentPanel = currentPanel,
                            actionHandler = actionHandler,
                            viewModel = viewModel,
                            shiftState = shiftState,
                            symbolsState = symbolsState,
                            editorInfo = editorInfo,
                            recentEmojis = recentEmojis,
                            gifList = gifList,
                            isGifLoading = isGifLoading,
                            clips = clips,
                            isSplit = false
                        )
                    }
                    KeyboardMode.ONE_HANDED_LEFT -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(modifier = Modifier.weight(0.85f)) {
                                PanelContent(
                                    currentPanel = currentPanel,
                                    actionHandler = actionHandler,
                                    viewModel = viewModel,
                                    shiftState = shiftState,
                                    symbolsState = symbolsState,
                                    editorInfo = editorInfo,
                                    recentEmojis = recentEmojis,
                                    gifList = gifList,
                                    isGifLoading = isGifLoading,
                                    clips = clips,
                                    isSplit = false
                                )
                            }
                            // Right Side Controls: Expand to Full, Switch to Right
                            Column(
                                modifier = Modifier
                                    .weight(0.15f)
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.updateKeyboardMode(KeyboardMode.NORMAL) },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colors.surface)
                                        .size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = "Expand to Full Screen",
                                        tint = colors.actionKeyText
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.updateKeyboardMode(KeyboardMode.ONE_HANDED_RIGHT) },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colors.surface)
                                        .size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Switch to Right",
                                        tint = colors.actionKeyText
                                    )
                                }
                            }
                        }
                    }
                    KeyboardMode.ONE_HANDED_RIGHT -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Left Side Controls: Expand to Full, Switch to Left
                            Column(
                                modifier = Modifier
                                    .weight(0.15f)
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.updateKeyboardMode(KeyboardMode.NORMAL) },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colors.surface)
                                        .size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = "Expand to Full Screen",
                                        tint = colors.actionKeyText
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.updateKeyboardMode(KeyboardMode.ONE_HANDED_LEFT) },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colors.surface)
                                        .size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Switch to Left",
                                        tint = colors.actionKeyText
                                    )
                                }
                            }

                            Box(modifier = Modifier.weight(0.85f)) {
                                PanelContent(
                                    currentPanel = currentPanel,
                                    actionHandler = actionHandler,
                                    viewModel = viewModel,
                                    shiftState = shiftState,
                                    symbolsState = symbolsState,
                                    editorInfo = editorInfo,
                                    recentEmojis = recentEmojis,
                                    gifList = gifList,
                                    isGifLoading = isGifLoading,
                                    clips = clips,
                                    isSplit = false
                                )
                            }
                        }
                    }
                    KeyboardMode.SPLIT -> {
                        PanelContent(
                            currentPanel = currentPanel,
                            actionHandler = actionHandler,
                            viewModel = viewModel,
                            shiftState = shiftState,
                            symbolsState = symbolsState,
                            editorInfo = editorInfo,
                            recentEmojis = recentEmojis,
                            gifList = gifList,
                            isGifLoading = isGifLoading,
                            clips = clips,
                            isSplit = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelContent(
    currentPanel: KeyboardPanel,
    actionHandler: KeyboardActionHandler,
    viewModel: KeyboardViewModel,
    shiftState: ShiftState,
    symbolsState: SymbolsState,
    editorInfo: EditorInfo?,
    recentEmojis: List<String>,
    gifList: List<com.nexboard.ime.data.gif.GifItem>,
    isGifLoading: Boolean,
    clips: List<com.nexboard.ime.data.clipboard.ClipItem>,
    isSplit: Boolean
) {
    when (currentPanel) {
        KeyboardPanel.QWERTY -> {
            QwertyLayout(
                actionHandler = actionHandler,
                shiftState = shiftState,
                symbolsState = symbolsState,
                onToggleShift = { viewModel.toggleShift() },
                onResetTemporaryShift = { viewModel.resetShiftIfTemporary() },
                onToggleSymbols = { viewModel.toggleSymbols() },
                onSetSymbolsState = { viewModel.setSymbolsState(it) },
                editorInfo = editorInfo,
                isSplit = isSplit
            )
        }
        KeyboardPanel.EMOJI -> {
            EmojiPanel(
                actionHandler = actionHandler,
                recentEmojis = recentEmojis,
                onEmojiSelected = { viewModel.addRecentEmoji(it) },
                onBackToKeyboard = { viewModel.setPanel(KeyboardPanel.QWERTY) }
            )
        }
        KeyboardPanel.GIF -> {
            GifPanel(
                actionHandler = actionHandler,
                gifList = gifList,
                isLoading = isGifLoading,
                onSearch = { viewModel.searchGifs(it) },
                onBackToKeyboard = { viewModel.setPanel(KeyboardPanel.QWERTY) }
            )
        }
        KeyboardPanel.TEXT_EDIT -> {
            TextEditingPanel(
                actionHandler = actionHandler,
                onBackToKeyboard = { viewModel.setPanel(KeyboardPanel.QWERTY) }
            )
        }
        KeyboardPanel.CLIPBOARD -> {
            ClipboardPanel(
                actionHandler = actionHandler,
                clips = clips,
                onTogglePin = { viewModel.togglePinClip(it) },
                onDeleteClip = { viewModel.deleteClip(it) },
                onClearAllNonPinned = { viewModel.clearAllNonPinnedClips() },
                onBackToKeyboard = { viewModel.setPanel(KeyboardPanel.QWERTY) }
            )
        }
    }
}
