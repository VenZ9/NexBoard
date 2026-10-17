package com.nexboard.ime.data.gif

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GifRepository {

    private val tenorApiKey: String = "LIVDSRZULELA" // Public test API key for Tenor v2 / demo

    private val apiService: GifApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("https://tenor.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GifApiService::class.java)
    }

    // Curated high-quality fallback animated GIFs in case of offline/network limits
    private val fallbackGifs = listOf(
        GifItem(
            id = "cat_vibing",
            title = "Cat Vibing",
            previewUrl = "https://media.giphy.com/media/jpbnoe3UIa8TU8LM13/giphy-preview.gif",
            fullUrl = "https://media.giphy.com/media/jpbnoe3UIa8TU8LM13/giphy.gif"
        ),
        GifItem(
            id = "thumbs_up",
            title = "Thumbs Up",
            previewUrl = "https://media.giphy.com/media/111ebonMs90YLu/giphy-preview.gif",
            fullUrl = "https://media.giphy.com/media/111ebonMs90YLu/giphy.gif"
        ),
        GifItem(
            id = "applause_clap",
            title = "Applause",
            previewUrl = "https://media.giphy.com/media/3o72FcJmLzIdYJdmDe/giphy-preview.gif",
            fullUrl = "https://media.giphy.com/media/3o72FcJmLzIdYJdmDe/giphy.gif"
        ),
        GifItem(
            id = "mind_blown",
            title = "Mind Blown",
            previewUrl = "https://media.giphy.com/media/26ufdipQqU2lhNA4g/giphy-preview.gif",
            fullUrl = "https://media.giphy.com/media/26ufdipQqU2lhNA4g/giphy.gif"
        ),
        GifItem(
            id = "excited_happy",
            title = "Super Excited",
            previewUrl = "https://media.giphy.com/media/5GoVLqeAOo6PK/giphy-preview.gif",
            fullUrl = "https://media.giphy.com/media/5GoVLqeAOo6PK/giphy.gif"
        ),
        GifItem(
            id = "mic_drop",
            title = "Mic Drop",
            previewUrl = "https://media.giphy.com/media/3o7qDSOvfaCO9b3MlO/giphy-preview.gif",
            fullUrl = "https://media.giphy.com/media/3o7qDSOvfaCO9b3MlO/giphy.gif"
        ),
        GifItem(
            id = "laughing_tears",
            title = "Laughing Tears",
            previewUrl = "https://media.giphy.com/media/O5NyCibf93upy/giphy-preview.gif",
            fullUrl = "https://media.giphy.com/media/O5NyCibf93upy/giphy.gif"
        ),
        GifItem(
            id = "party_dance",
            title = "Party Time",
            previewUrl = "https://media.giphy.com/media/artj92V8o75VPL7AeQ/giphy-preview.gif",
            fullUrl = "https://media.giphy.com/media/artj92V8o75VPL7AeQ/giphy.gif"
        )
    )

    suspend fun getTrendingGifs(): List<GifItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFeaturedTenor(apiKey = tenorApiKey, limit = 24)
            mapTenorResponse(response).ifEmpty { fallbackGifs }
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching trending GIFs, falling back to curated list: ${e.message}")
            fallbackGifs
        }
    }

    suspend fun searchGifs(query: String): List<GifItem> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext getTrendingGifs()

        try {
            val response = apiService.searchTenor(query = query, apiKey = tenorApiKey, limit = 24)
            val mapped = mapTenorResponse(response)
            if (mapped.isNotEmpty()) {
                mapped
            } else {
                fallbackGifs.filter { it.title.contains(query, ignoreCase = true) }.ifEmpty { fallbackGifs }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error searching GIFs: ${e.message}")
            fallbackGifs.filter { it.title.contains(query, ignoreCase = true) }.ifEmpty { fallbackGifs }
        }
    }

    private fun mapTenorResponse(response: TenorResponse): List<GifItem> {
        val results = response.results ?: return emptyList()
        return results.mapNotNull { item ->
            val formats = item.mediaFormats ?: return@mapNotNull null
            // Prefer tinygif or gif for preview, gif for full
            val previewFormat = formats["tinygif"] ?: formats["nanogif"] ?: formats["gif"]
            val fullFormat = formats["gif"] ?: previewFormat

            if (previewFormat != null && fullFormat != null) {
                GifItem(
                    id = item.id,
                    title = item.title?.ifBlank { item.contentDescription } ?: "GIF",
                    previewUrl = previewFormat.url,
                    fullUrl = fullFormat.url,
                    mimeType = "image/gif",
                    width = previewFormat.dims?.getOrNull(0) ?: 200,
                    height = previewFormat.dims?.getOrNull(1) ?: 200
                )
            } else null
        }
    }

    companion object {
        private const val TAG = "GifRepository"
    }
}
