package com.dirzaaulia.yomiru.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dirzaaulia.yomiru.repository.NetworkRepository
import com.dirzaaulia.yomiru.model.MalEntry
import com.dirzaaulia.yomiru.model.request.SearchQuery
import com.dirzaaulia.yomiru.util.pagingSucceeded

enum class MalEntryPaging {
    SEARCH_ANIME, SEARCH_MANGA, TOP_ANIME, TOP_MANGA, SEASONAL
}

class MalEntryPagingSource(
    private val repository: NetworkRepository,
    private val data: SearchQuery,
    private val type: MalEntryPaging,
): PagingSource<Int, MalEntry>() {
    override fun getRefreshKey(state: PagingState<Int, MalEntry>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MalEntry> {
        val page = params.key ?: 1

        return when (type) {
            MalEntryPaging.SEARCH_ANIME -> repository.animeSearch(
                data = data
            ).pagingSucceeded { response ->
                val isHasNextPage = response.pagination?.hasNextPage
                val data = response.data
                LoadResult.Page(
                    data = data,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (isHasNextPage == true) page + 1 else null
                )
            }
            MalEntryPaging.SEARCH_MANGA -> repository.mangaSearch(
                query = "",
                page = page
            ).pagingSucceeded { response ->
                val isHasNextPage = response.pagination?.hasNextPage
                val data = response.data
                LoadResult.Page(
                    data = data,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (isHasNextPage == true) page + 1 else null
                )
            }
            MalEntryPaging.TOP_ANIME -> repository.getTopAnime(
                query = data,
                page = page
            ).pagingSucceeded { response ->
                val isHasNextPage = response.pagination?.hasNextPage
                val data = response.data
                LoadResult.Page(
                    data = data,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (isHasNextPage == true) page + 1 else null
                )
            }

            MalEntryPaging.TOP_MANGA -> repository.getTopManga(
                query = data,
                page = page
            ).pagingSucceeded { response ->
                val isHasNextPage = response.pagination?.hasNextPage
                val data = response.data
                LoadResult.Page(
                    data = data,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (isHasNextPage == true) page + 1 else null
                )
            }

            MalEntryPaging.SEASONAL -> repository.getAnimeSeason(
                query = data,
                page = page,
                limit = 25
            ).pagingSucceeded { response ->
                val isHasNextPage = response.pagination?.hasNextPage
                val data = response.data
                LoadResult.Page(
                    data = data,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (isHasNextPage == true) page + 1 else null
                )
            }
        }
    }
}