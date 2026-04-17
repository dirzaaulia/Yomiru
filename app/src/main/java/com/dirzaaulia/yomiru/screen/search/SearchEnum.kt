package com.dirzaaulia.yomiru.screen.search

import com.dirzaaulia.yomiru.util.capitalizeWords

enum class SearchAnimeType {
    TV, MOVIE, OVA, SPECIAL, ONA, MUSIC, CM, PV, TV_SPECIAL
}

enum class SearchSeasonalType {
    TV, MOVIE, OVA, SPECIAL, ONA, MUSIC
}

enum class SearchMangaType {
    MANGA, NOVEL, LIGHT_NOVEL, ONE_SHOT, DOUJIN, MANHWA, MANHUA
}

enum class SearchAnimeOrderBy {
    MAL_ID, TITLE, START_DATE, END_DATE, CHAPTERS, VOLUMES, SCORE, SCORED_BY, RANK, POPULARITY, MEMBERS, FAVORITES
}

enum class SearchMangaOrderBy {
    MAL_ID, TITLE, START_DATE, END_DATE, CHAPTERS, VOLUMES, SCORE, SCORED_BY, RANK, POPULARITY, MEMBERS, FAVORITES
}

enum class SearchAnimeStatus {
    AIRING, COMPLETE, UPCOMING
}

enum class SearchMangaStatus {
    PUBLISHING, COMPLETE, HIATUS, DISCONTINUED, UPCOMING
}

enum class SearchSort(val code: String, val description: String) {
    ASC("ASC", "Ascending"),
    DESC("DESC", "Descending");

    companion object {
        fun fromCode(code: String): SearchSort? {
            return SearchSort.entries.find { it.code == code }
        }

        fun fromDescription(desc: String): SearchSort? {
            return SearchSort.entries.find { it.description == desc }
        }
    }
}

enum class SearchAnimeRating(val code: String, val description: String) {
    G("G", "All Ages"),
    PG("PG", "Children"),
    PG13("PG-13", "Teens 13 or Older"),
    R17("R-17", "Violence & Profanity"),
    R("R+", "Mild Nudity"),
    RX("Rx", "Hentai");

    companion object {
        fun fromCode(code: String): SearchAnimeRating? {
            return entries.find { it.code == code }
        }

        fun fromDescription(desc: String): SearchAnimeRating? {
            return entries.find { it.description == desc }
        }
    }
}

enum class AnimeSeason(val value: String) {
    WINTER("winter"),
    SPRING("spring"),
    SUMMER("summer"),
    FALL("fall");

    companion object {
        // Logic to determine the current season based on system month
        fun fromMonth(month: Int): AnimeSeason {
            return when (month) {
                1, 2, 3 -> WINTER
                4, 5, 6 -> SPRING
                7, 8, 9 -> SUMMER
                10, 11, 12 -> FALL
                else -> WINTER
            }
        }
    }
}

enum class SearchTopAnimeFilter {
    AIRING, UPCOMING, BY_POPULARITY, FAVORITE;

    fun toDisplayString(): String = name.lowercase().replace("_", " ").capitalizeWords()
}

enum class SearchTopMangaFilter {
    PUBLISHING, UPCOMING, BY_POPULARITY, FAVORITE;

    fun toDisplayString(): String = name.lowercase().replace("_", " ").capitalizeWords()
}