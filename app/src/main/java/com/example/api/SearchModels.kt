package com.example.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JikanResponse(
    val data: List<JikanAnime>?
)

@JsonClass(generateAdapter = true)
data class JikanAnime(
    @Json(name = "mal_id") val malId: Int,
    val title: String,
    val synopsis: String?,
    val images: JikanImages?
)

@JsonClass(generateAdapter = true)
data class JikanImages(
    val webp: JikanWebpImages?
)

@JsonClass(generateAdapter = true)
data class JikanWebpImages(
    @Json(name = "large_image_url") val largeImageUrl: String?
)

@JsonClass(generateAdapter = true)
data class TmdbSearchResponse(
    val results: List<TmdbItem>?
)

@JsonClass(generateAdapter = true)
data class TmdbItem(
    val id: Int,
    val title: String?, // used for movies
    val name: String?,  // used for tv shows
    val overview: String?,
    @Json(name = "poster_path") val posterPath: String?
)
