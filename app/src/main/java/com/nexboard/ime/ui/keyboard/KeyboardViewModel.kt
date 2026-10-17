package com.nexboard.ime.ui.keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexboard.ime.data.clipboard.ClipItem
import com.nexboard.ime.data.clipboard.ClipboardRepository
import com.nexboard.ime.data.emoji.EmojiData
import com.nexboard.ime.data.gif.GifItem
import com.nexboard.ime.data.gif.GifRepository
import com.nexboard.ime.data.settings.KeyboardMode
import com.nexboard.ime.data.settings.UserPreferences
import com.nexboard.ime.data.settings.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class KeyboardPanel {
    QWERTY,
    EMOJI,
    GIF,
    TEXT_EDIT,
    CLIPBOARD
}

enum class ShiftState {
    UN_SHIFTED,
    SHIFTED,
    CAPS_LOCKED
}

enum class SymbolsState {
    LETTERS,
    SYMBOLS,
    MORE_SYMBOLS
}

class KeyboardViewModel(
    private val userPreferencesRepo: UserPreferencesRepository,
    private val clipboardRepo: ClipboardRepository,
    private val gifRepository: GifRepository = GifRepository()
) : ViewModel() {

    private val _currentPanel = MutableStateFlow(KeyboardPanel.QWERTY)
    val currentPanel: StateFlow<KeyboardPanel> = _currentPanel.asStateFlow()

    private val _shiftState = MutableStateFlow(ShiftState.UN_SHIFTED)
    val shiftState: StateFlow<ShiftState> = _shiftState.asStateFlow()

    private val _symbolsState = MutableStateFlow(SymbolsState.LETTERS)
    val symbolsState: StateFlow<SymbolsState> = _symbolsState.asStateFlow()

    private val _recentEmojis = MutableStateFlow(EmojiData.defaultRecent)
    val recentEmojis: StateFlow<List<String>> = _recentEmojis.asStateFlow()

    private val _suggestions = MutableStateFlow(listOf("The", "I", "You", "NexBoard", "And"))
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private val _gifList = MutableStateFlow<List<GifItem>>(emptyList())
    val gifList: StateFlow<List<GifItem>> = _gifList.asStateFlow()

    private val _isGifLoading = MutableStateFlow(false)
    val isGifLoading: StateFlow<Boolean> = _isGifLoading.asStateFlow()

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepo.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    val clipboardClips: StateFlow<List<ClipItem>> = clipboardRepo.clipsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadTrendingGifs()
    }

    fun setPanel(panel: KeyboardPanel) {
        _currentPanel.value = panel
    }

    fun toggleShift() {
        _shiftState.value = when (_shiftState.value) {
            ShiftState.UN_SHIFTED -> ShiftState.SHIFTED
            ShiftState.SHIFTED -> ShiftState.CAPS_LOCKED
            ShiftState.CAPS_LOCKED -> ShiftState.UN_SHIFTED
        }
    }

    fun resetShiftIfTemporary() {
        if (_shiftState.value == ShiftState.SHIFTED) {
            _shiftState.value = ShiftState.UN_SHIFTED
        }
    }

    fun setSymbolsState(state: SymbolsState) {
        _symbolsState.value = state
    }

    fun toggleSymbols() {
        _symbolsState.value = if (_symbolsState.value == SymbolsState.LETTERS) {
            SymbolsState.SYMBOLS
        } else {
            SymbolsState.LETTERS
        }
    }

    fun updateKeyboardMode(mode: KeyboardMode) {
        viewModelScope.launch {
            userPreferencesRepo.updateKeyboardMode(mode)
        }
    }

    fun addRecentEmoji(emoji: String) {
        val current = _recentEmojis.value.toMutableList()
        current.remove(emoji)
        current.add(0, emoji)
        _recentEmojis.value = current.take(30)
    }

    fun loadTrendingGifs() {
        viewModelScope.launch {
            _isGifLoading.value = true
            _gifList.value = gifRepository.getTrendingGifs()
            _isGifLoading.value = false
        }
    }

    fun searchGifs(query: String) {
        viewModelScope.launch {
            _isGifLoading.value = true
            _gifList.value = gifRepository.searchGifs(query)
            _isGifLoading.value = false
        }
    }

    fun togglePinClip(id: String) {
        viewModelScope.launch {
            clipboardRepo.togglePin(id)
        }
    }

    fun deleteClip(id: String) {
        viewModelScope.launch {
            clipboardRepo.deleteClip(id)
        }
    }

    fun clearAllNonPinnedClips() {
        viewModelScope.launch {
            clipboardRepo.clearAllNonPinned()
        }
    }

    fun updateWordSuggestions(currentWord: String) {
        if (currentWord.isBlank()) {
            _suggestions.value = listOf("The", "I", "You", "NexBoard", "And")
        } else {
            val capitalized = currentWord.replaceFirstChar { it.uppercase() }
            _suggestions.value = listOf(
                currentWord,
                capitalized,
                "${currentWord}ing",
                "${currentWord}ed",
                "${currentWord}s"
            )
        }
    }
}
