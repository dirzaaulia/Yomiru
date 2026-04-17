package com.dirzaaulia.yomiru.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dirzaaulia.yomiru.model.MalNode
import com.dirzaaulia.yomiru.repository.NetworkRepository
import com.dirzaaulia.yomiru.util.pagingSucceeded

enum class AnimeStatus {
    WATCHING, COMPLETED, ON_HOLD, DROPPED, PLAN_TO_WATCH
}

class MalNodePagingSource(
    private val repository: NetworkRepository,
    private val code: Int,
    private val status: Int
): PagingSource<Int, MalNode>() {

    override fun getRefreshKey(state: PagingState<Int, MalNode>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MalNode> {
        val offset = params.key ?: 0

        //TODO Update to use code for differentiate Anime and Manga

        val statusStr = if (code == 0) {
            AnimeStatus.entries[status].name.lowercase()
        } else {
            ""
        }

        return repository.getUserAnimeList(
            userName = "@me",
            offset = offset,
            status = statusStr
        ).pagingSucceeded { response ->
            val data = response.data
            LoadResult.Page(
                data = data,
                prevKey = if (offset == 0) null else offset - 50,
                nextKey = if (data.isNotEmpty()) offset + 50 else null
            )
        }
    }

}