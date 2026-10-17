package com.nexboard.ime.service

import com.nexboard.ime.data.gif.GifItem

enum class DpadDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

interface KeyboardActionHandler {
    fun onKey(char: Char)
    fun onText(text: String)
    fun onBackspace()
    fun onEnter()
    fun onSpace()
    fun onDpad(direction: DpadDirection)
    fun onSelect()
    fun onSelectAll()
    fun onCut()
    fun onCopy()
    fun onPaste()
    fun onHome()
    fun onEnd()
    fun onCommitGif(gif: GifItem)
    fun onSwitchInputMethod()
    fun onCloseKeyboard()
}
