package com.nexboard.ime.data.gif

import android.content.ClipDescription
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.core.content.FileProvider
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class GifContentCommitter(private val context: Context) {

    suspend fun commitGif(
        inputConnection: InputConnection?,
        editorInfo: EditorInfo?,
        gif: GifItem
    ): Boolean = withContext(Dispatchers.IO) {
        if (inputConnection == null) return@withContext false

        val supportedMimeTypes = if (editorInfo != null) {
            EditorInfoCompat.getContentMimeTypes(editorInfo)
        } else {
            emptyArray()
        }

        val supportsGif = supportedMimeTypes.any { mime ->
            ClipDescription.compareMimeTypes(mime, "image/gif") ||
            ClipDescription.compareMimeTypes(mime, "image/*")
        }

        if (supportsGif && editorInfo != null) {
            try {
                val gifFile = downloadGifToCache(gif)
                if (gifFile != null && gifFile.exists()) {
                    val authority = "${context.packageName}.fileprovider"
                    val contentUri = FileProvider.getUriForFile(context, authority, gifFile)

                    val description = ClipDescription(gif.title, arrayOf("image/gif"))
                    val contentInfo = InputContentInfoCompat(
                        contentUri,
                        description,
                        Uri.parse(gif.fullUrl)
                    )

                    var flags = 0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        flags = flags or InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
                    }

                    val committed = InputConnectionCompat.commitContent(
                        inputConnection,
                        editorInfo,
                        contentInfo,
                        flags,
                        null
                    )

                    if (committed) {
                        Log.d(TAG, "Rich content committed successfully: ${gif.title}")
                        return@withContext true
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to commit GIF rich content", e)
            }
        }

        // Fallback: commit GIF URL as text if app does not support rich content
        withContext(Dispatchers.Main) {
            inputConnection.commitText(gif.fullUrl, 1)
        }
        true
    }

    private fun downloadGifToCache(gif: GifItem): File? {
        return try {
            val gifsDir = File(context.cacheDir, "gifs").apply { if (!exists()) mkdirs() }
            val cleanId = gif.id.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val targetFile = File(gifsDir, "${cleanId}.gif")

            if (!targetFile.exists() || targetFile.length() == 0L) {
                val url = URL(gif.fullUrl)
                url.openStream().use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }
            targetFile
        } catch (e: Exception) {
            Log.e(TAG, "Error caching GIF file", e)
            null
        }
    }

    companion object {
        private const val TAG = "GifContentCommitter"
    }
}
