package com.nexboard.ime.data.clipboard

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.clipboardDataStore: DataStore<Preferences> by preferencesDataStore(name = "nexboard_clipboard")

class ClipboardRepository(private val context: Context) {

    private val gson = Gson()
    private val clipboardKey = stringPreferencesKey("clipboard_items_json")

    val clipsFlow: Flow<List<ClipItem>> = context.clipboardDataStore.data
        .map { preferences ->
            val json = preferences[clipboardKey] ?: "[]"
            parseClipsJson(json)
        }

    private fun parseClipsJson(json: String): List<ClipItem> {
        return try {
            val type = object : TypeToken<List<ClipItem>>() {}.type
            gson.fromJson<List<ClipItem>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun addClip(rawText: String) {
        val cleanText = rawText.trim()
        if (cleanText.isBlank()) return

        context.clipboardDataStore.edit { preferences ->
            val currentList = parseClipsJson(preferences[clipboardKey] ?: "[]").toMutableList()

            // Check if item already exists
            val existingIndex = currentList.indexOfFirst { it.text == cleanText }
            if (existingIndex >= 0) {
                val existing = currentList.removeAt(existingIndex)
                // Move to top with updated timestamp, preserve pin status
                currentList.add(0, existing.copy(timestamp = System.currentTimeMillis()))
            } else {
                currentList.add(0, ClipItem(text = cleanText))
            }

            // Sort pinned first, then by timestamp desc
            val sorted = currentList.sortedWith(
                compareByDescending<ClipItem> { it.isPinned }
                    .thenByDescending { it.timestamp }
            )

            preferences[clipboardKey] = gson.toJson(sorted)
        }
    }

    suspend fun togglePin(id: String) {
        context.clipboardDataStore.edit { preferences ->
            val currentList = parseClipsJson(preferences[clipboardKey] ?: "[]").toMutableList()
            val index = currentList.indexOfFirst { it.id == id }
            if (index >= 0) {
                val item = currentList[index]
                currentList[index] = item.copy(isPinned = !item.isPinned)
                val sorted = currentList.sortedWith(
                    compareByDescending<ClipItem> { it.isPinned }
                        .thenByDescending { it.timestamp }
                )
                preferences[clipboardKey] = gson.toJson(sorted)
            }
        }
    }

    suspend fun deleteClip(id: String) {
        context.clipboardDataStore.edit { preferences ->
            val currentList = parseClipsJson(preferences[clipboardKey] ?: "[]").toMutableList()
            currentList.removeAll { it.id == id }
            preferences[clipboardKey] = gson.toJson(currentList)
        }
    }

    suspend fun clearAllNonPinned() {
        context.clipboardDataStore.edit { preferences ->
            val currentList = parseClipsJson(preferences[clipboardKey] ?: "[]")
            val pinnedOnly = currentList.filter { it.isPinned }
            preferences[clipboardKey] = gson.toJson(pinnedOnly)
        }
    }
}
