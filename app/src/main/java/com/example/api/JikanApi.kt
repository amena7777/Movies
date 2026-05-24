package com.example.api

import retrofit2.http.GET
import retrofit2.http.Query

interface JikanApi {
    @GET("anime")
    suspend fun searchAnime(@Query("q") query: String): JikanResponse
}
