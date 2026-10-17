package com.nexboard.ime.data.gif

import com.google.gson.annotations.SerializedName

data class GifItem(
    val id: String,
    val title: String,
    val previewUrl: String,
    val fullUrl: String,
    val mimeType: String = "image/gif",
    val width: Int = 200,
    val height: Int = 200
)

// Tenor API v2 Models
data class TenorResponse(
    @SerializedName("results") val results: List<TenorResult>? = null,
    @SerializedName("next") val next: String? = null
)

data class TenorResult(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String?,
    @SerializedName("content_description") val contentDescription: String?,
    @SerializedName("media_formats") val mediaFormats: Map<String, TenorMediaFormat>?
)

data class TenorMediaFormat(
    @SerializedName("url") val url: String,
    @SerializedName("dims") val dims: List<Int>?,
    @SerializedName("size") val size: Long?
)

// Giphy API Models
data class GiphyResponse(
    @SerializedName("data") val data: List<GiphyGif>? = null
)

data class GiphyGif(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String?,
    @SerializedName("images") val images: GiphyImages?
)

data class GiphyImages(
    @SerializedName("fixed_height") val fixedHeight: GiphyImageDetail?,
    @SerializedName("original") val original: GiphyImageDetail?
)

data class GiphyImageDetail(
    @SerializedName("url") val url: String,
    @SerializedName("width") val width: String?,
    @SerializedName("height") val height: String?
)
