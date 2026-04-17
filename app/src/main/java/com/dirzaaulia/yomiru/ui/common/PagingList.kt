package com.dirzaaulia.yomiru.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T: Any> VerticalStaggeredGridPaging(
    list: LazyPagingItems<T>,
    columns: StaggeredGridCells,
    isHavePlaceholder: Boolean = false,
    placeholderContent: @Composable () -> Unit = { },
    emptyContent: @Composable () -> Unit = { },
    itemContent: @Composable (T) -> Unit
) {
    when (list.loadState.source.refresh) {
        LoadState.Loading -> {
            if (isHavePlaceholder) {
                if (list.itemCount == 0) {
                    placeholderContent()
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LoadingIndicator(modifier = Modifier.fillMaxSize())
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }
            }
        }

        is LoadState.NotLoading -> {
            if (list.itemCount == 0) {
                emptyContent()
            } else {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                    columns = columns,
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(list.itemCount) { index ->
                        val item = list[index]
                        itemContent(item!!)
                    }

                    when (list.loadState.source.append) {
                        LoadState.Loading -> {
                            item { LoadingIndicator() }
                        }

                        is LoadState.NotLoading -> Unit
                        is LoadState.Error -> {
                            val errorMessage =
                                (list.loadState.source.append as LoadState.Error).error.message
                            item {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Text(
                                        text = errorMessage.toString(),
                                        style = MaterialTheme.typography.headlineSmallEmphasized
                                    )
                                    Button(onClick = {
                                        list.refresh()
                                    }) { Text(text = "Retry") }
                                }
                            }
                        }
                    }
                }
            }
        }

        is LoadState.Error -> {
            val errorMessage =
                (list.loadState.source.refresh as LoadState.Error).error.message
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = errorMessage.toString(),
                    style = MaterialTheme.typography.headlineSmallEmphasized
                )
                Button(onClick = {
                    list.refresh()
                }) { Text(text = "Retry") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T: Any> VerticalPaging(
    list: LazyPagingItems<T>,
    isHavePlaceholder: Boolean = false,
    placeholderContent: @Composable () -> Unit = { },
    emptyContent: @Composable () -> Unit = { },
    itemContent: @Composable (T) -> Unit
) {
    when (list.loadState.source.refresh) {
        LoadState.Loading -> {
            if (isHavePlaceholder) {
                if (list.itemCount == 0) {
                    placeholderContent()
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LoadingIndicator(modifier = Modifier.fillMaxSize())
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }
            }
        }

        is LoadState.NotLoading -> {
            if (list.itemCount == 0) {
                emptyContent()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                ) {
                    items(list.itemCount) { index ->
                        val item = list[index]
                        itemContent(item!!)
                    }

                    when (list.loadState.source.append) {
                        LoadState.Loading -> {
                            item { LoadingIndicator() }
                        }

                        is LoadState.NotLoading -> Unit
                        is LoadState.Error -> {
                            val errorMessage =
                                (list.loadState.source.append as LoadState.Error).error.message
                            item {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Text(
                                        text = errorMessage.toString(),
                                        style = MaterialTheme.typography.headlineSmallEmphasized
                                    )
                                    Button(onClick = {
                                        list.refresh()
                                    }) { Text(text = "Retry") }
                                }
                            }
                        }
                    }
                }
            }
        }

        is LoadState.Error -> {
            val errorMessage =
                (list.loadState.source.refresh as LoadState.Error).error.message
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = errorMessage.toString(),
                    style = MaterialTheme.typography.headlineSmallEmphasized
                )
                Button(onClick = {
                    list.refresh()
                }) { Text(text = "Retry") }
            }
        }
    }
}