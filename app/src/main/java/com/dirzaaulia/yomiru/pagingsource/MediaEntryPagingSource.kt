package com.dirzaaulia.yomiru.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dirzaaulia.yomiru.model.MediaEntry
import com.dirzaaulia.yomiru.model.request.SearchQuery
import com.dirzaaulia.yomiru.repository.NetworkRepository
import com.dirzaaulia.yomiru.util.pagingSucceeded

enum class MediaEntryPaging {
    SEARCH_ANIME, SEARCH_MANGA, TOP_ANIME, TOP_MANGA, SEASONAL
}

class MediaEntryPagingSource(
    private val repository: NetworkRepository,
    private val data: SearchQuery,
    private val type: MediaEntryPaging,
): PagingSource<Int, MediaEntry>() {
    override fun getRefreshKey(state: PagingState<Int, MediaEntry>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaEntry> {
        val page = params.key ?: 1
        val limit = params.loadSize

        return when (type) {
            MediaEntryPaging.SEARCH_ANIME -> repository.searchAnime(
                query = data,
                page = page,
                limit = limit
            ).pagingSucceeded { response ->
                val isHasNextPage = response.pagination?.hasNextPage
                val dataList = response.data
                LoadResult.Page(
                    data = dataList,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (isHasNextPage == true) page + 1 else null
                )
            }
            MediaEntryPaging.SEARCH_MANGA -> repository.searchManga(
                query = data,
                page = page,
                limit = limit
            ).pagingSucceeded { response ->
                val isHasNextPage = response.pagination?.hasNextPage
                val dataList = response.data
                LoadResult.Page(
                    data = dataList,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (isHasNextPage == true) page + 1 else null
                )
            }
            MediaEntryPaging.TOP_ANIME -> repository.getTopAnime(
                query = data,
                page = page,
                limit = limit
            ).pagingSucceeded { response ->
                val isHasNextPage = response.pagination?.hasNextPage
                val dataList = response.data
                LoadResult.Page(
                    data = dataList,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (isHasNextPage == true) page + 1 else null
                )
            }
            MediaEntryPaging.TOP_MANGA -> repository.getTopManga(
                query = data,
                page = page,
                limit = limit
            ).pagingSucceeded { response ->
                val isHasNextPage = response.pagination?.hasNextPage
                val dataList = response.data
                LoadResult.Page(
                    data = dataList,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (isHasNextPage == true) page + 1 else null
                )
            }
            MediaEntryPaging.SEASONAL -> repository.getAnimeSeason(
                query = data,
                page = page,
                limit = limit
            ).pagingSucceeded { response ->
                val isHasNextPage = response.pagination?.hasNextPage
                val dataList = response.data
                LoadResult.Page(
                    data = dataList,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (isHasNextPage == true) page + 1 else null
                )
            }
        }
    }
}
