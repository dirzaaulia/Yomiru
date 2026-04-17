package com.dirzaaulia.yomiru.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dirzaaulia.yomiru.ui.common.NetworkImage
import com.dirzaaulia.yomiru.util.ResponseResult
import com.dirzaaulia.yomiru.util.success
import kotlin.collections.count
import kotlin.collections.orEmpty

@OptIn(ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class)
@Composable
fun DetailInfoScreen(
    viewModel: DetailViewModel,
    imageSize: Pair<Dp, Dp>,
) {
    val malImages by viewModel.malImages.collectAsStateWithLifecycle()

    Column {
        Box(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
                .height(imageSize.second)
        ) {
            when (malImages) {
                ResponseResult.Loading -> LoadingIndicator(
                    modifier = Modifier.fillMaxSize()
                )
                is ResponseResult.Success<*> -> {
                    malImages.success {  response ->
                        val list = response?.data.orEmpty()
                        val carouselState = rememberCarouselState { list.count() }
                        HorizontalMultiBrowseCarousel(
                            state = carouselState,
                            modifier = Modifier.fillMaxWidth(),
                            preferredItemWidth = imageSize.first,
                            itemSpacing = 8.dp,
                        ) { i ->
                            val item = list[i]
                            NetworkImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .maskClip(MaterialTheme.shapes.extraLarge),
                                url = item.webp?.maximumImageUrl ?: item.webp?.largeImageUrl,
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }
                }
                is ResponseResult.Error -> {

                }
            }
        }
    }
}