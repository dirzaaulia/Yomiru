package com.dirzaaulia.yomiru.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dirzaaulia.yomiru.model.MalReview
import com.dirzaaulia.yomiru.repository.NetworkRepository
import com.dirzaaulia.yomiru.util.pagingSucceeded

class MalReviewPagingSource(
    private val repository: NetworkRepository,
    private val type: String,
): PagingSource<Int, MalReview>() {
    override fun getRefreshKey(state: PagingState<Int, MalReview>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MalReview> {
        val page = params.key ?: 1
        return repository.getTopReview(
            type = type,
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
    }
}