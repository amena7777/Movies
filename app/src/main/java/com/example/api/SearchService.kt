package com.example.api

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

enum class MediaCategory(val label: String) {
    PELICULA("PELÍCULA"),
    SERIE("SERIE"),
    ANIME("ANIME")
}

data class SearchResultItem(
    val id: String, // composite id "anime_X", "movie_X", "tv_X", "fallback_X"
    val apiId: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val mediaType: MediaCategory
)

object RetrofitClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    val jikanApi: JikanApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.jikan.moe/v4/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(JikanApi::class.java)
    }

    val tmdbApi: TmdbApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TmdbApi::class.java)
    }

    val geminiApi: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    val moshiInstance: Moshi = moshi
}

class SearchService {
    private val jikanApi = RetrofitClient.jikanApi
    private val tmdbApi = RetrofitClient.tmdbApi
    private val geminiApi = RetrofitClient.geminiApi
    private val moshi = RetrofitClient.moshiInstance

    // Local beautiful pre-populated lists to show on first open
    fun getDiscoverItems(): List<SearchResultItem> {
        return listOf(
            SearchResultItem(
                id = "anime_52034",
                apiId = 52034,
                title = "Dandadan",
                overview = "Momo Ayase y Ken Takakura (Okarun) son dos estudiantes obsesionados con lo sobrenatural. Momo cree en fantasmas pero no en extraterrestres, y Okarun cree en alienígenas pero no en espectros. Para demostrar quién tiene razón, deciden visitar lugares conocidos por sus avistamientos paranormales, desencadenando una loca batalla donde espíritus, alienígenas y poderes psíquicos se entrelazan de forma explosiva.",
                posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500",
                mediaType = MediaCategory.ANIME
            ),
            SearchResultItem(
                id = "anime_50411",
                apiId = 50411,
                title = "Summer Time Render",
                overview = "Tras enterarse de la muerte de su hermana adoptiva Ushio, Shinpei Ajiro regresa a la remota isla de Hitogashima. Al investigar la repentina pérdida de Ushio, Shinpei tropieza con un oscuro y letal misterio ancestral relacionado con seres llamados 'Sombras', capaces de suplantar identidades. Pronto se encuentra atrapado en un bucle temporal de supervivencia intensa y misterio constante.",
                posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=500",
                mediaType = MediaCategory.ANIME
            ),
            SearchResultItem(
                id = "movie_123456",
                apiId = 123456,
                title = "Spider-Man: Beyond the Spider-Verse",
                overview = "La continuación del épico viaje multiversal de Miles Morales. Tras quedar atrapado en una dimensión equivocada y perseguido por la Sociedad Arácnida, Miles deberá reunir a sus más fieles aliados para salvar a su padre de las manos de Spot en un espectacular enfrentamiento que redefinirá el significado de ser un héroe.",
                posterUrl = "https://images.unsplash.com/photo-1478720568477-152d9b164e26?w=500",
                mediaType = MediaCategory.PELICULA
            ),
            SearchResultItem(
                id = "tv_100088",
                apiId = 100088,
                title = "The Last of Us (Temporada 2)",
                overview = "Tras los desgarradores eventos en Salt Lake City, Joel y Ellie se asientan temporalmente en Jackson County. Pero las consecuencias del pasado no tardarán en alcanzarlos, arrastrando a Ellie a un implacable camino de venganza y supervivencia en un mundo post-apocalíptico implacable guiado por las cicatrices emocionales.",
                posterUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500",
                mediaType = MediaCategory.SERIE
            ),
            SearchResultItem(
                id = "anime_5114",
                apiId = 5114,
                title = "Fullmetal Alchemist: Brotherhood",
                overview = "Para recuperar sus cuerpos perdidos tras intentar el tabú supremo de la transmutación humana, los hermanos alquimistas Edward y Alphonse Elric emprenden un viaje implacable en búsqueda del mítico artefacto: la Piedra Filosofal, desvelando conspiraciones milenarias dentro de las fuerzas del estado.",
                posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500",
                mediaType = MediaCategory.ANIME
            )
        )
    }

    suspend fun search(query: String, selectedCategory: String): List<SearchResultItem> = withContext(Dispatchers.IO) {
        if (query.trim().isEmpty()) {
            return@withContext getDiscoverItems()
        }

        val results = mutableListOf<SearchResultItem>()

        // Check APIs
        val tmdbKey = BuildConfig.TMDB_API_KEY
        val hasTmdbKey = tmdbKey.isNotBlank() && 
                         !tmdbKey.startsWith("YOUR_") && 
                         !tmdbKey.startsWith("MY_") && 
                         !tmdbKey.contains("PLACEHOLDER")

        val searchAnimeJob = async {
            if (selectedCategory == "ALL" || selectedCategory == MediaCategory.ANIME.label) {
                try {
                    val jikanRes = jikanApi.searchAnime(query)
                    jikanRes.data?.take(10)?.map { anime ->
                        SearchResultItem(
                            id = "anime_${anime.malId}",
                            apiId = anime.malId,
                            title = anime.title,
                            overview = anime.synopsis ?: "No hay sinopsis oficial disponible para este anime.",
                            posterUrl = anime.images?.webp?.largeImageUrl,
                            mediaType = MediaCategory.ANIME
                        )
                    } ?: emptyList()
                } catch (e: Exception) {
                    Log.e("SearchService", "Failed to search Jikan anime: ${e.message}", e)
                    emptyList()
                }
            } else {
                emptyList()
            }
        }

        val searchMoviesJob = async {
            if (selectedCategory == "ALL" || selectedCategory == MediaCategory.PELICULA.label) {
                if (hasTmdbKey) {
                    try {
                        val movieRes = tmdbApi.searchMovie(query, tmdbKey)
                        movieRes.results?.take(10)?.map { movie ->
                            SearchResultItem(
                                id = "movie_${movie.id}",
                                apiId = movie.id,
                                title = movie.title ?: "Película Desconocida",
                                overview = movie.overview ?: "No hay sinopsis disponible.",
                                posterUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                                mediaType = MediaCategory.PELICULA
                            )
                        } ?: emptyList()
                    } catch (e: Exception) {
                        Log.e("SearchService", "Failed to search TMDB movies: ${e.message}", e)
                        emptyList()
                    }
                } else {
                    // Fallback to Gemini if no TMDB key is provided
                    searchWithGemini(query, MediaCategory.PELICULA.label)
                }
            } else {
                emptyList()
            }
        }

        val searchTvJob = async {
            if (selectedCategory == "ALL" || selectedCategory == MediaCategory.SERIE.label) {
                if (hasTmdbKey) {
                    try {
                        val tvRes = tmdbApi.searchTv(query, tmdbKey)
                        tvRes.results?.take(10)?.map { tv ->
                            SearchResultItem(
                                id = "tv_${tv.id}",
                                apiId = tv.id,
                                title = tv.name ?: "Serie Desconocida",
                                overview = tv.overview ?: "No hay sinopsis disponible.",
                                posterUrl = tv.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                                mediaType = MediaCategory.SERIE
                            )
                        } ?: emptyList()
                    } catch (e: Exception) {
                        Log.e("SearchService", "Failed to search TMDB series: ${e.message}", e)
                        emptyList()
                    }
                } else {
                    // Fallback to Gemini if no TMDB key is provided
                    searchWithGemini(query, MediaCategory.SERIE.label)
                }
            } else {
                emptyList()
            }
        }

        // Gather all search responses
        results.addAll(searchAnimeJob.await())
        results.addAll(searchMoviesJob.await())
        results.addAll(searchTvJob.await())

        // Sort results to prioritize matching query starts
        results.sortedBy { item ->
            val matchIndex = item.title.lowercase().indexOf(query.lowercase())
            if (matchIndex == 0) 0 else if (matchIndex > 0) 1 else 2
        }
    }

    // Direct Gemini Smart Fallback for Movie & Series Search
    private suspend fun searchWithGemini(query: String, specificType: String): List<SearchResultItem> {
        val geminiKey = BuildConfig.GEMINI_API_KEY
        if (geminiKey.isBlank() || geminiKey.startsWith("MY_")) {
            Log.w("SearchService", "No Gemini Key found for fallback search.")
            return getLocalDumbFallback(query, specificType)
        }

        val systemPrompt = """
            Eres un motor de búsqueda semántico de entretenimiento. El usuario está buscando títulos de tipo: '$specificType' que coincidan con la búsqueda: '$query'.
            Devuelve una respuesta estructurada estrictamente en un formato JSON plano, con un array que contenga entre 3 y 6 títulos relevantes en español.
            Debe respetar exactamente el siguiente formato JSON, sin envoltorios de Markdown de tipo ```json o explicaciones de texto adicionales:
            
            [
              {
                "apiId": 999123,
                "title": "Título exacto",
                "overview": "Sinopsis extendida y redactada excelentemente en español.",
                "mediaType": "$specificType"
              }
            ]
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = "Busca títulos de tipo '$specificType' para la clave: '$query'")
                    )
                )
            ),
            generationConfig = GeminiGenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.3
            ),
            systemInstruction = GeminiContent(
                parts = listOf(
                    GeminiPart(text = systemPrompt)
                )
            )
        )

        try {
            val response = geminiApi.generateContent(geminiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!jsonText.isNullOrBlank()) {
                // Parse JSON into list of GeminiResultItem
                val cleanJson = jsonText.trim().removeSurrounding("```json", "```")
                val listType = Types.newParameterizedType(List::class.java, GeminiParsedItem::class.java)
                val jsonAdapter = moshi.adapter<List<GeminiParsedItem>>(listType)
                val parsedList = jsonAdapter.fromJson(cleanJson) ?: emptyList()

                return parsedList.map { parsedItem ->
                    val resolvedId = "${if (parsedItem.mediaType.uppercase().contains("PELI")) "movie" else "tv"}_${parsedItem.apiId}"
                    SearchResultItem(
                        id = resolvedId,
                        apiId = parsedItem.apiId,
                        title = parsedItem.title,
                        overview = parsedItem.overview,
                        posterUrl = when (parsedItem.mediaType.uppercase()) {
                            "PELÍCULA", "PELICULA" -> "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500" // Cinema general fallback url
                            else -> "https://images.unsplash.com/photo-1478720568477-152d9b164e26?w=500" // Film projection fallback url
                        },
                        mediaType = if (parsedItem.mediaType.uppercase().contains("PELI")) MediaCategory.PELICULA else MediaCategory.SERIE
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("SearchService", "Gemini search failed: ${e.message}", e)
        }

        return getLocalDumbFallback(query, specificType)
    }

    private fun getLocalDumbFallback(query: String, type: String): List<SearchResultItem> {
        // Return a clean fallback based on simple keyword matches
        val lowercaseQuery = query.lowercase()
        val allFallbacks = listOf(
            SearchResultItem(
                id = "movie_9910",
                apiId = 9910,
                title = "Avatar 3: Fire and Ash",
                overview = "La tercera entrega de la saga de James Cameron continúa explorando las tierras de Pandora de la mano de un nuevo peligro: el clan de la ceniza, quienes encarnan el odio y el fuego del planeta.",
                posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=500",
                mediaType = MediaCategory.PELICULA
            ),
            SearchResultItem(
                id = "movie_9911",
                apiId = 9911,
                title = "Dune: Part Two",
                overview = "Paul Atreides se une a Chani y a los Fremen mientras busca venganza contra los conspiradores que destruyeron a su familia. En el camino, debe elegir entre el amor de su vida y el destino del universo para evitar un futuro кошмар que solo él puede prever.",
                posterUrl = "https://images.unsplash.com/photo-1478720568477-152d9b164e26?w=500",
                mediaType = MediaCategory.PELICULA
            ),
            SearchResultItem(
                id = "tv_9912",
                apiId = 9912,
                title = "Stranger Things",
                overview = "Cuando un niño de un pequeño pueblo desaparece de forma misteriosa, sus amigos empiezan una investigación descubriendo fuerzas paranormales, experimentos secretos del gobierno y una extraña niña con capacidades psíquicas arrolladoras.",
                posterUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500",
                mediaType = MediaCategory.SERIE
            ),
            SearchResultItem(
                id = "tv_9913",
                apiId = 9913,
                title = "Severance",
                overview = "Mark Scout lidera un equipo en Lumon Industries, una misteriosa corporación donde los empleados se han sometido voluntariamente a un procedimiento quirúrgico de 'separación' cerebral para dividir radicalmente sus recuerdos laborales de sus vidas personales.",
                posterUrl = "https://images.unsplash.com/photo-1542204172-e7052809f852?w=500",
                mediaType = MediaCategory.SERIE
            )
        )

        return allFallbacks.filter { 
            (it.mediaType.label == type) && 
            (it.title.lowercase().contains(lowercaseQuery) || it.overview.lowercase().contains(lowercaseQuery) || lowercaseQuery.length < 3) 
        }
    }
}

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class GeminiParsedItem(
    val apiId: Int,
    val title: String,
    val overview: String,
    val mediaType: String
)
