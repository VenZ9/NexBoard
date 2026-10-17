package com.nexboard.ime.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import com.nexboard.ime.R

class SoundEffectManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var soundPool: SoundPool? = null
    private var keypressSoundId: Int = 0
    private var isSoundLoaded: Boolean = false

    var isSoundEnabled: Boolean = true
    var isHapticEnabled: Boolean = true
    var respectSystemSoundSettings: Boolean = true
    var soundVolume: Float = 1.0f

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
            .apply {
                setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        isSoundLoaded = true
                        Log.d(TAG, "Keypress sound loaded successfully (id: $sampleId)")
                    } else {
                        Log.w(TAG, "Failed to load keypress sound (status: $status)")
                    }
                }
            }

        try {
            keypressSoundId = soundPool?.load(context, R.raw.keypress, 1) ?: 0
        } catch (e: Exception) {
            Log.e(TAG, "Error loading keypress.wav from raw resources", e)
        }
    }

    fun playKeypressSound() {
        if (!isSoundEnabled) return

        if (respectSystemSoundSettings) {
            val systemSoundOn = try {
                Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.SOUND_EFFECTS_ENABLED,
                    1
                ) != 0
            } catch (_: Exception) {
                true
            }

            val ringerNormal = audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL
            if (!systemSoundOn || !ringerNormal) {
                return
            }
        }

        if (isSoundLoaded && keypressSoundId != 0) {
            val volume = soundVolume.coerceIn(0.1f, 1.0f)
            soundPool?.play(
                keypressSoundId,
                volume,
                volume,
                1,      // priority
                0,      // loop (no loop)
                1.0f    // playback rate
            )
        }
    }

    fun playHapticFeedback() {
        if (!isHapticEnabled) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            }
        } catch (_: Exception) {
            // Ignore vibration exceptions on devices without vibrator hardware
        }
    }

    fun onKeyAction() {
        playKeypressSound()
        playHapticFeedback()
    }

    fun release() {
        try {
            soundPool?.release()
            soundPool = null
            isSoundLoaded = false
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing SoundPool", e)
        }
    }

    companion object {
        private const val TAG = "SoundEffectManager"
    }
}
