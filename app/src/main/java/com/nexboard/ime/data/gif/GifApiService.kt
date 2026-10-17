package com.nexboard.ime.data.gif

import retrofit2.http.GET
import retrofit2.http.Query

interface GifApiService {

    @GET("v2/featured")
    suspend fun getFeaturedTenor(
        @Query("key") apiKey: String,
        @Query("limit") limit: Int = 20,
        @Query("client_key") clientKey: String = "nexboard_ime"
    ): TenorResponse

    @GET("v2/search")
    suspend fun searchTenor(
        @Query("q") query: String,
        @Query("key") apiKey: String,
        @Query("limit") limit: Int = 20,
        @Query("client_key") clientKey: String = "nexboard_ime"
    ): TenorResponse
}
