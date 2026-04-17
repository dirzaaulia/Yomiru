package com.dirzaaulia.yomiru.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dirzaaulia.yomiru.ui.common.NetworkImage

@Composable
fun ImageWithCaption(
   modifier: Modifier = Modifier,
   url: String?,
   contentDescription: String?,
   caption: String?
) {
    Box(modifier = modifier) {
        NetworkImage(
            modifier = Modifier.fillMaxSize(), // Image fills the Box
            url = url,
            contentDescription = contentDescription, // Keep for accessibility
            contentScale = ContentScale.FillBounds
        )
        Box( // Background for the title
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.Black.copy(alpha = 0.6f)
                ) // Semi-transparent background
                .align(Alignment.BottomCenter) // Align to bottom
        ) {
            Text(
                text = caption.toString(),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge, // Or bodySmall, adjust as needed
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}