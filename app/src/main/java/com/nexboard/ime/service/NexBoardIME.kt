package com.nexboard.ime.service

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.nexboard.ime.audio.SoundEffectManager
import com.nexboard.ime.data.clipboard.ClipboardManagerHelper
import com.nexboard.ime.data.clipboard.ClipboardRepository
import com.nexboard.ime.data.gif.GifContentCommitter
import com.nexboard.ime.data.gif.GifItem
import com.nexboard.ime.data.settings.UserPreferencesRepository
import com.nexboard.ime.ui.keyboard.KeyboardRootView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NexBoardIME : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner,
    KeyboardActionHandler {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    lateinit var soundEffectManager: SoundEffectManager
        private set
    lateinit var userPreferencesRepo: UserPreferencesRepository
        private set
    lateinit var clipboardRepo: ClipboardRepository
        private set
    lateinit var clipboardHelper: ClipboardManagerHelper
        private set
    private lateinit var gifCommitter: GifContentCommitter

    private var currentEditorInfo: EditorInfo? = null

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        soundEffectManager = SoundEffectManager(this)
        userPreferencesRepo = UserPreferencesRepository(this)
        clipboardRepo = ClipboardRepository(this)
        clipboardHelper = ClipboardManagerHelper(this, clipboardRepo, serviceScope)
        gifCommitter = GifContentCommitter(this)

        clipboardHelper.startListening()

        // Sync preferences to sound manager
        serviceScope.launch {
            userPreferencesRepo.userPreferencesFlow.collectLatest { prefs ->
                soundEffectManager.isSoundEnabled = prefs.soundEnabled
                soundEffectManager.respectSystemSoundSettings = prefs.systemSoundRespected
                soundEffectManager.soundVolume = prefs.soundVolume
                soundEffectManager.isHapticEnabled = prefs.hapticEnabled
            }
        }
    }

    override fun onCreateInputView(): View {
        window?.window?.decorView?.let { decor ->
            ViewTreeLifecycleOwner.set(decor, this)
            ViewTreeViewModelStoreOwner.set(decor, this)
            ViewTreeSavedStateRegistryOwner.set(decor, this)
        }

        return ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            ViewTreeLifecycleOwner.set(this, this@NexBoardIME)
            ViewTreeViewModelStoreOwner.set(this, this@NexBoardIME)
            ViewTreeSavedStateRegistryOwner.set(this, this@NexBoardIME)

            setContent {
                KeyboardRootView(
                    actionHandler = this@NexBoardIME,
                    userPreferencesRepo = userPreferencesRepo,
                    clipboardRepo = clipboardRepo,
                    editorInfo = currentEditorInfo
                )
            }
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        currentEditorInfo = info
        clipboardHelper.capturePrimaryClip()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        clipboardHelper.stopListening()
        soundEffectManager.release()
        serviceScope.cancel()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }

    // --- KeyboardActionHandler Implementation ---

    override fun onKey(char: Char) {
        soundEffectManager.onKeyAction()
        currentInputConnection?.commitText(char.toString(), 1)
    }

    override fun onText(text: String) {
        soundEffectManager.onKeyAction()
        currentInputConnection?.commitText(text, 1)
    }

    override fun onBackspace() {
        soundEffectManager.onKeyAction()
        val ic = currentInputConnection ?: return
        val selected = ic.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            ic.commitText("", 1)
        } else {
            ic.deleteSurroundingText(1, 0)
        }
    }

    override fun onEnter() {
        soundEffectManager.onKeyAction()
        val ic = currentInputConnection ?: return
        val info = currentEditorInfo

        val imeAction = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)
            ?: EditorInfo.IME_ACTION_NONE

        if (imeAction != EditorInfo.IME_ACTION_NONE && imeAction != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.performEditorAction(imeAction)
        } else {
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }
    }

    override fun onSpace() {
        soundEffectManager.onKeyAction()
        currentInputConnection?.commitText(" ", 1)
    }

    override fun onDpad(direction: DpadDirection) {
        soundEffectManager.onKeyAction()
        val ic = currentInputConnection ?: return
        val keyCode = when (direction) {
            DpadDirection.UP -> KeyEvent.KEYCODE_DPAD_UP
            DpadDirection.DOWN -> KeyEvent.KEYCODE_DPAD_DOWN
            DpadDirection.LEFT -> KeyEvent.KEYCODE_DPAD_LEFT
            DpadDirection.RIGHT -> KeyEvent.KEYCODE_DPAD_RIGHT
        }
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
    }

    override fun onSelect() {
        soundEffectManager.onKeyAction()
        val ic = currentInputConnection ?: return
        // Send shift + dpad right to start/expand selection
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT))
    }

    override fun onSelectAll() {
        soundEffectManager.onKeyAction()
        currentInputConnection?.performContextMenuAction(android.R.id.selectAll)
    }

    override fun onCut() {
        soundEffectManager.onKeyAction()
        currentInputConnection?.performContextMenuAction(android.R.id.cut)
    }

    override fun onCopy() {
        soundEffectManager.onKeyAction()
        currentInputConnection?.performContextMenuAction(android.R.id.copy)
    }

    override fun onPaste() {
        soundEffectManager.onKeyAction()
        currentInputConnection?.performContextMenuAction(android.R.id.paste)
    }

    override fun onHome() {
        soundEffectManager.onKeyAction()
        val ic = currentInputConnection ?: return
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MOVE_HOME))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MOVE_HOME))
    }

    override fun onEnd() {
        soundEffectManager.onKeyAction()
        val ic = currentInputConnection ?: return
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MOVE_END))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MOVE_END))
    }

    override fun onCommitGif(gif: GifItem) {
        soundEffectManager.onKeyAction()
        serviceScope.launch {
            gifCommitter.commitGif(currentInputConnection, currentEditorInfo, gif)
        }
    }

    override fun onSwitchInputMethod() {
        soundEffectManager.onKeyAction()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                switchToNextInputMethod(false)
                return
            } catch (_: Exception) {
                // Fallback to picker
            }
        }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showInputMethodPicker()
    }

    override fun onCloseKeyboard() {
        soundEffectManager.onKeyAction()
        requestHideSelf(0)
    }
}
