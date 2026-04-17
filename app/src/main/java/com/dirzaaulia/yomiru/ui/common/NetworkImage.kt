package com.dirzaaulia.yomiru.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent

/**
 * A wrapper around [Image] and [rememberImagePainter], setting a
 * default [contentScale] and showing content while loading.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NetworkImage(
    modifier: Modifier = Modifier,
    url: String?,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
) {
    SubcomposeAsyncImage(
        modifier = modifier,
        model = url,
        contentDescription = contentDescription,
        contentScale = contentScale,
        colorFilter = colorFilter
    ) {
        val state = painter.state.collectAsStateWithLifecycle()
        when (state.value) {
            is AsyncImagePainter.State.Loading -> {
                LoadingIndicator(modifier = modifier,)
            }

            is AsyncImagePainter.State.Success -> {
                SubcomposeAsyncImageContent()
            }

            is AsyncImagePainter.State.Error -> {
                Box(
                    modifier = modifier,
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = modifier.fillMaxSize(),
                        imageVector = Icons.Filled.BrokenImage,
                        contentDescription = contentDescription,
                        contentScale = ContentScale.FillBounds,
                        colorFilter = ColorFilter.tint(color = Color.White)
                    )
                }
            }

            AsyncImagePainter.State.Empty -> Unit
        }
    }
}