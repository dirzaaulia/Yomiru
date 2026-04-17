package com.dirzaaulia.yomiru.screen.detail.anime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.dirzaaulia.yomiru.screen.detail.DetailViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailEpisodeScreen(
    viewModel: DetailViewModel
) {
    val malEpisodes = viewModel.malEpisodes.collectAsLazyPagingItems()

    when (malEpisodes.loadState.source.refresh) {
        LoadState.Loading -> Box(modifier = Modifier.fillMaxSize()) {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
        }
        is LoadState.NotLoading -> LazyColumn {
            items(malEpisodes.itemCount) { index ->
                val item = malEpisodes[index]
                Card {
                    Text(text = item?.title.toString())
                    Text(text = item?.score.toString())
                    Row {
                        if (item?.filler == true) {
                            AssistChip(
                                onClick = { },
                                label = { Text(text = "Filler") }
                            )
                        }
                        if (item?.recap == true) {
                            AssistChip(
                                onClick = { },
                                label = { Text(text = "Recap") }
                            )
                        }
                    }
                }
            }
        }
        is LoadState.Error -> {

        }
    }
}