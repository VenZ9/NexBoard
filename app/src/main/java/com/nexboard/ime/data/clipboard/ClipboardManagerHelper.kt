package com.nexboard.ime.data.clipboard

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClipboardManagerHelper(
    private val context: Context,
    private val repository: ClipboardRepository,
    private val scope: CoroutineScope
) {
    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

    private val clipListener = ClipboardManager.OnPrimaryClipChangedListener {
        capturePrimaryClip()
    }

    fun startListening() {
        clipboardManager?.addPrimaryClipChangedListener(clipListener)
        // Check current clip on startup
        capturePrimaryClip()
    }

    fun stopListening() {
        clipboardManager?.removePrimaryClipChangedListener(clipListener)
    }

    fun capturePrimaryClip() {
        val manager = clipboardManager ?: return
        if (!manager.hasPrimaryClip()) return

        val description = manager.primaryClipDescription ?: return
        if (!description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) &&
            !description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)
        ) {
            return
        }

        val clip = manager.primaryClip ?: return
        if (clip.itemCount > 0) {
            val text = clip.getItemAt(0)?.text?.toString()
            if (!text.isNullOrBlank()) {
                scope.launch(Dispatchers.IO) {
                    repository.addClip(text)
                }
            }
        }
    }
}
