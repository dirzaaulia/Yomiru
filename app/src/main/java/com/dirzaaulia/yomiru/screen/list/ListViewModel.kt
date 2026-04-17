package com.dirzaaulia.yomiru.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dirzaaulia.yomiru.model.MalNode
import com.dirzaaulia.yomiru.pagingsource.MalNodePagingSource
import com.dirzaaulia.yomiru.repository.NetworkRepository
import id.pgidata.gomamam.repository.DataStoreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest


class ListViewModel(
    private val repository: NetworkRepository,
    datastore: DataStoreRepository
): ViewModel() {

    private val _selectedIndex: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _selectedStatus: MutableStateFlow<Int> = MutableStateFlow(0)
    val selectedStatus = _selectedStatus.asStateFlow()

    val accessToken = datastore.accessTokenFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    val nodeList: Flow<PagingData<MalNode>> = accessToken.flatMapLatest { token ->
        _selectedIndex.flatMapLatest { index ->
            _selectedStatus.flatMapLatest { status ->
                if (token.isEmpty()) emptyFlow()
                else Pager(
                    config = PagingConfig(pageSize = 25),
                    pagingSourceFactory = {
                        MalNodePagingSource(
                            repository = repository,
                            code = index,
                            status = status
                        )
                    }
                ).flow
            }
        }
    }.cachedIn(viewModelScope)

    fun setSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }

    fun setSelectedStatus(index: Int) {
        _selectedStatus.value = index
    }
}