package com.dirzaaulia.yomiru.network

import io.ktor.resources.Resource
import kotlinx.serialization.SerialName

/**
 * MyAnimeList
 */
@Resource("/token")
object Token

@Resource("/users")
class Users() {
    @Resource("{userName}")
    class Username(
        val parent: Users = Users(),
        val userName: String,
    ) {
        @Resource("/animelist")
        class AnimeList(
            val parent: Username,
            val offset: Int,
            val limit: Int = 50,
            val status: String
        )
    }
}

/**
 * Jikan
 */
@Resource("/top")
class Top() {
    @Resource("/anime")
    class Anime(
        val parent: Top = Top(),
        val type: String? = null,
        val filter: String? = null,
        val rating: String? = null,
        val sfw: Boolean = true,
        val page: Int = 1,
        val limit: Int = 25,
    )

    @Resource("/manga")
    class Manga(
        val parent: Top = Top(),
        val type: String? = null,
        val filter: String? = null,
        val page: Int = 1,
        val limit: Int = 25
    )

    @Resource("/reviews")
    class Reviews(
        val parent: Top = Top(),
        val type: String,
        val page: Int = 1,
    )
}

@Resource("/recommendations")
class Recommendations() {
    @Resource("/anime")
    class Anime(
        val parent: Recommendations = Recommendations(),
        val page: Int = 1,
    )

    @Resource("/manga")
    class Manga(
        val parent: Recommendations = Recommendations(),
        val page: Int = 1,
    )
}

@Resource("/seasons")
class Seasons() {
    @Resource("/now")
    class Now(
        val parent: Seasons = Seasons(),
        val page: Int = 1,
        val limit: Int = 25,
    )

    // NEW: Handles /seasons/{year}
    @Resource("{year}")
    class Year(
        val parent: Seasons = Seasons(),
        val year: Int
    ) {
        // NEW: Handles /seasons/{year}/{season}
        @Resource("{season}")
        class Season(
            val parent: Year,
            val season: String,
            val page: Int = 1,
            val limit: Int = 25,
            // You can add other query params here if the API supports them
            @SerialName("sfw") val sfw: Boolean? = null,
            @SerialName("type") val type: String? = null,
        )
    }
}

@Resource("/anime")
class Anime(
    @SerialName("q")
    val query: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("min_score")
    val minScore: Int? = null,
    @SerialName("max_score")
    val maxScore: Int? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("rating")
    val rating: String? = null,
    @SerialName("sfw")
    val sfw: Boolean? = null,
    @SerialName("genres")
    val genres: Int? = null,
    @SerialName("order_by")
    val orderBy: String? = null,
    @SerialName("sort")
    val sort: String? = null,
    @SerialName("letter")
    val letter: String? = null,
    val page: Int? = null,
    val limit: Int? = null
) {
    @Resource("{id}")
    class Id(
        val parent: Anime = Anime(),
        val id: String
    ) {
        @Resource("/characters")
        class Characters(val parent: Id)

        @Resource("/pictures")
        class Pictures(val parent: Id)

        @Resource("/episodes")
        class Episodes(
            val parent: Id,
            val page: Int = 1,
        )

        @Resource("/recommendations")
        class Recommendations(val parent: Id)

        @Resource("/videos")
        class Videos(val parent: Id)
    }
}

@Resource("/manga")
class Manga(
    @SerialName("q")
    val query: String? = null,
    val page: Int? = null,
    val limit: Int? = null
)

@Resource("/genres")
class Genres() {
    @Resource("/anime")
    class Anime(
        val parent: Genres = Genres()
    )
}