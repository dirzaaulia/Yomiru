package com.dirzaaulia.yomiru.screen.detail.anime

import android.util.Log
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView // Import PlayerView
import com.dirzaaulia.yomiru.screen.detail.DetailViewModel
import com.dirzaaulia.yomiru.util.ResponseResult
import com.dirzaaulia.yomiru.util.success

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailVideoScreen(
    viewModel: DetailViewModel
) {

    val malVideos by viewModel.malVideos.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.getAnimeVideos()
    }

    when (malVideos) {
        ResponseResult.Loading -> LoadingIndicator()
        is ResponseResult.Success<*> -> {
            malVideos.success {
                val data = it?.data
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { Text("Music Video") }
                    items(data?.musicVideos.orEmpty()) { item ->
                        VideoPlayer(
                            id = item.video?.youtubeId.toString(),
                            videoUrl = item.video?.url.toString()
                        )
                    }
                }
            }
        }

        is ResponseResult.Error -> {

        }
    }
}

@Composable
fun VideoPlayer(
    id: String,
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // 1. Create ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    exoPlayer.addListener(object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            // Handle error
            Log.e("VideoPlayer", "Player Error: ", error)
        }
    })

    // 2. Create MediaSession instance
    val mediaSession = remember {
        MediaSession.Builder(context, exoPlayer)
            .setId(id)
            .build()
    }

    // 3. Lifecycle Management
    DisposableEffect(Unit) {
        onDispose {
            mediaSession.release()
            exoPlayer.release()
        }
    }

    // 4. Load and Prepare Media when videoUrl changes or on initial composition
    LaunchedEffect(videoUrl) {
        if (videoUrl.isNotBlank()) {
            val mediaItem = MediaItem.fromUri(videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            // exoPlayer.playWhenReady = true // Uncomment to auto-play
        }
    }

    // 5. Embed PlayerView using AndroidView
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply { // Changed StyledPlayerView to PlayerView
                player = exoPlayer
                // You can customize the controller visibility and behavior here
                // e.g., useController = true, controllerShowTimeoutMs = 3000
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f) // Adjust aspect ratio as needed
    )
}
