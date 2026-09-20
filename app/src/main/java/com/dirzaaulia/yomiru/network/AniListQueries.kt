package com.dirzaaulia.yomiru.network

object AniListQueries {

    val HOME_SCREEN_QUERY = """
        query (${'$'}season: MediaSeason, ${'$'}seasonYear: Int) {
          seasonal: Page(page: 1, perPage: 12) {
            media(season: ${'$'}season, seasonYear: ${'$'}seasonYear, type: ANIME, sort: POPULARITY_DESC) {
              id title { romaji english native } coverImage { extraLarge large } meanScore format episodes genres description bannerImage
            }
          }
          trending: Page(page: 1, perPage: 12) {
            media(type: ANIME, sort: TRENDING_DESC) {
              id title { romaji english native } coverImage { extraLarge large } meanScore format episodes genres description bannerImage
            }
          }
          topAnime: Page(page: 1, perPage: 12) {
            media(type: ANIME, sort: SCORE_DESC) {
              id title { romaji english native } coverImage { extraLarge large } meanScore format episodes genres description bannerImage
            }
          }
          topManga: Page(page: 1, perPage: 12) {
            media(type: MANGA, sort: SCORE_DESC) {
              id title { romaji english native } coverImage { extraLarge large } meanScore format volumes chapters genres description bannerImage
            }
          }
          reviews: Page(page: 1, perPage: 5) {
            reviews(sort: RATING_DESC) {
              id summary rating score user { name avatar { large } } media { id title { romaji english } coverImage { large } }
            }
          }
          recommendations: Page(page: 1, perPage: 12) {
            recommendations(sort: RATING_DESC) {
              id rating
              media { id title { romaji english } coverImage { extraLarge large } meanScore format description bannerImage }
              mediaRecommendation { id title { romaji english } coverImage { extraLarge large } meanScore format description bannerImage }
            }
          }
        }
    """.trimIndent()

    val SEARCH_MEDIA_QUERY = """
        query (${'$'}page: Int, ${'$'}perPage: Int, ${'$'}search: String, ${'$'}type: MediaType, ${'$'}format: MediaFormat, ${'$'}status: MediaStatus, ${'$'}season: MediaSeason, ${'$'}seasonYear: Int, ${'$'}genre: String, ${'$'}countryOfOrigin: CountryCode, ${'$'}sort: [MediaSort]) {
          Page(page: ${'$'}page, perPage: ${'$'}perPage) {
            pageInfo { total perPage currentPage lastPage hasNextPage }
            media(search: ${'$'}search, type: ${'$'}type, format: ${'$'}format, status: ${'$'}status, season: ${'$'}season, seasonYear: ${'$'}seasonYear, genre: ${'$'}genre, countryOfOrigin: ${'$'}countryOfOrigin, sort: ${'$'}sort) {
              id title { romaji english native } coverImage { extraLarge large } meanScore format episodes volumes chapters genres description bannerImage
            }
          }
        }
    """.trimIndent()

    val MEDIA_DETAIL_QUERY = """
        query (${'$'}id: Int, ${'$'}type: MediaType) {
          Media(id: ${'$'}id, type: ${'$'}type) {
            id idMal title { romaji english native userPreferred } type format status description
            startDate { year month day } endDate { year month day }
            season seasonYear episodes duration chapters volumes countryOfOrigin isLicensed source hashtag
            synonyms averageScore meanScore popularity favourites
            coverImage { extraLarge large color } bannerImage genres
            studios { nodes { id name } }
            mediaListEntry { id mediaId status score progress progressVolumes repeat notes }
            nextAiringEpisode { episode airingAt timeUntilAiring }
            rankings { id rank type format year season allTime context }
            tags { id name description rank isMediaSpoiler }
            characters(perPage: 50, sort: [ROLE, RELEVANCE]) {
              edges {
                role
                node { id name { full native } image { large } }
                voiceActors { id name { full native } image { large } languageV2 }
              }
            }
            staff(perPage: 50, sort: [RELEVANCE]) {
              edges {
                role
                node { id name { full native } image { large } }
              }
            }
            streamingEpisodes {
              title thumbnail url site
            }
            relations {
              edges {
                relationType
                node {
                  id title { romaji english native userPreferred } format type status coverImage { extraLarge large } meanScore
                }
              }
            }
            reviews(perPage: 25, sort: [RATING_DESC]) {
              nodes {
                id summary rating score body user { name avatar { large } }
              }
            }
            stats {
              statusDistribution { status amount }
              scoreDistribution { score amount }
            }
            externalLinks {
              id url site color
            }
            recommendations(perPage: 30, sort: RATING_DESC) {
              nodes {
                id rating mediaRecommendation { id title { romaji english userPreferred } coverImage { extraLarge large } meanScore format description bannerImage }
              }
            }
          }
        }
    """.trimIndent()

    val RECOMMENDATIONS_QUERY = """
        query (${'$'}page: Int, ${'$'}perPage: Int) {
          Page(page: ${'$'}page, perPage: ${'$'}perPage) {
            pageInfo { total perPage currentPage lastPage hasNextPage }
            recommendations(sort: RATING_DESC) {
              id rating
              media { id title { userPreferred english romaji native } coverImage { extraLarge large } meanScore format description bannerImage }
              mediaRecommendation { id title { userPreferred english romaji native } coverImage { extraLarge large } meanScore format description bannerImage }
            }
          }
        }
    """.trimIndent()

    val REVIEWS_QUERY = """
        query (${'$'}page: Int, ${'$'}perPage: Int) {
          Page(page: ${'$'}page, perPage: ${'$'}perPage) {
            pageInfo { total perPage currentPage lastPage hasNextPage }
            reviews(sort: RATING_DESC) {
              id summary rating score user { name avatar { large } } media { id title { romaji english } coverImage { large } }
            }
          }
        }
    """.trimIndent()

    val SAVE_MEDIA_LIST_ENTRY_MUTATION = """
        mutation (${'$'}mediaId: Int, ${'$'}status: MediaListStatus, ${'$'}score: Float, ${'$'}progress: Int, ${'$'}progressVolumes: Int) {
          SaveMediaListEntry(mediaId: ${'$'}mediaId, status: ${'$'}status, score: ${'$'}score, progress: ${'$'}progress, progressVolumes: ${'$'}progressVolumes) {
            id
            mediaId
            status
            score
            progress
            progressVolumes
          }
        }
    """.trimIndent()

    val DELETE_MEDIA_LIST_ENTRY_MUTATION = """
        mutation (${'$'}id: Int) {
          DeleteMediaListEntry(id: ${'$'}id) {
            deleted
          }
        }
    """.trimIndent()

    val VIEWER_QUERY = """
        query {
          Viewer {
            id
            name
          }
        }
    """.trimIndent()

    val USER_MEDIA_LIST_QUERY = """
        query (${'$'}userId: Int, ${'$'}type: MediaType) {
          MediaListCollection(userId: ${'$'}userId, type: ${'$'}type) {
            lists {
              name
              status
              entries {
                id
                mediaId
                status
                score
                progress
                progressVolumes
                media {
                  id title { userPreferred english romaji native } coverImage { extraLarge large } meanScore format episodes chapters volumes status description bannerImage
                }
              }
            }
          }
        }
    """.trimIndent()

    val MEDIA_CHARACTERS_QUERY = """
        query (${'$'}id: Int, ${'$'}type: MediaType, ${'$'}page: Int, ${'$'}perPage: Int) {
          Media(id: ${'$'}id, type: ${'$'}type) {
            characters(page: ${'$'}page, perPage: ${'$'}perPage, sort: [ROLE, RELEVANCE]) {
              pageInfo { hasNextPage }
              edges {
                role
                node { id name { full native } image { large } }
                voiceActors { id name { full native } image { large } languageV2 }
              }
            }
          }
        }
    """.trimIndent()

    val MEDIA_STAFF_QUERY = """
        query (${'$'}id: Int, ${'$'}type: MediaType, ${'$'}page: Int, ${'$'}perPage: Int) {
          Media(id: ${'$'}id, type: ${'$'}type) {
            staff(page: ${'$'}page, perPage: ${'$'}perPage, sort: [RELEVANCE]) {
              pageInfo { hasNextPage }
              edges {
                role
                node { id name { full native } image { large } }
              }
            }
          }
        }
    """.trimIndent()

    val MEDIA_REVIEWS_QUERY = """
        query (${'$'}id: Int, ${'$'}type: MediaType, ${'$'}page: Int, ${'$'}perPage: Int) {
          Media(id: ${'$'}id, type: ${'$'}type) {
            reviews(page: ${'$'}page, perPage: ${'$'}perPage, sort: [RATING_DESC]) {
              pageInfo { hasNextPage }
              nodes {
                id summary rating score body user { name avatar { large } }
              }
            }
          }
        }
    """.trimIndent()

    val MEDIA_DETAIL_RECOMMENDATIONS_QUERY = """
        query (${'$'}id: Int, ${'$'}type: MediaType, ${'$'}page: Int, ${'$'}perPage: Int) {
          Media(id: ${'$'}id, type: ${'$'}type) {
            recommendations(page: ${'$'}page, perPage: ${'$'}perPage, sort: RATING_DESC) {
              pageInfo { hasNextPage }
              nodes {
                id rating mediaRecommendation { id title { userPreferred english romaji native } coverImage { extraLarge large } meanScore format description bannerImage }
              }
            }
          }
        }
    """.trimIndent()
}
