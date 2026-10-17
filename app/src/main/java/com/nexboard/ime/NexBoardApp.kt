package com.nexboard.ime

import android.app.Application
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat

class NexBoardApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initEmojiCompat()
    }

    private fun initEmojiCompat() {
        try {
            val config = BundledEmojiCompatConfig(this)
                .setReplaceAll(true)
            EmojiCompat.init(config)
        } catch (_: Exception) {
            // Graceful fallback if bundled emoji metadata is unavailable
        }
    }
}
