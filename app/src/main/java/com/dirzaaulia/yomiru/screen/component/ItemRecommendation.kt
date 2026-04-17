package com.dirzaaulia.yomiru.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.dirzaaulia.yomiru.model.response.MalRecommendation

@Composable
fun ItemRecommendation(
    modifier: Modifier = Modifier,
    item: MalRecommendation,
    imageWidth: Dp,
    imageHeight: Dp,
) {
    val firstEntry = item.entry?.first()
    val secondEntry = item.entry?.last()

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "By ${item.user?.username.toString()}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight),
                // Arrangement.spacedBy with a negative value can create the "overlap" look
                // Or use 0.dp for perfectly side-by-side symmetry
                horizontalArrangement = Arrangement.spacedBy((-32).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageWithCaption(
                    modifier = Modifier
                        .weight(1f) // Makes it symmetric
                        .fillMaxHeight()
                        .clip(MaterialTheme.shapes.extraLarge),
                    url = firstEntry?.images?.webp?.largeImageUrl,
                    contentDescription = firstEntry?.title,
                    caption = firstEntry?.title
                )
                ImageWithCaption(
                    modifier = Modifier
                        .weight(1f) // Makes it symmetric
                        .fillMaxHeight()
                        .clip(MaterialTheme.shapes.extraLarge)
                        .zIndex(2f), // Ensures the second one stays "on top" if overlapping
                    url = secondEntry?.images?.webp?.largeImageUrl,
                    contentDescription = secondEntry?.title,
                    caption = secondEntry?.title
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.content.toString(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

//@Composable
//fun ItemRecommendation(
//    modifier: Modifier = Modifier,
//    item: MalRecommendation,
//    imageWidth: Dp,
//    imageHeight: Dp,
//) {
//    val firstEntry = item.entry?.first()
//    val secondEntry = item.entry?.last()
//    Card(modifier = modifier) {
//        Box(modifier = Modifier.fillMaxWidth()){
//            Column(modifier = Modifier.padding(14.dp)) {
//                Text(
//                    text = "By ${item.user?.username.toString()}",
//                    style = MaterialTheme.typography.titleLarge
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    ImageWithCaption(
//                        modifier = Modifier
//                            .requiredWidth(imageWidth)
//                            .height(imageHeight)
//                            .clip(MaterialTheme.shapes.extraLarge),
//                        url = firstEntry?.images?.webp?.largeImageUrl,
//                        contentDescription = firstEntry?.title,
//                        caption = firstEntry?.title
//                    )
//                    ImageWithCaption(
//                        modifier = Modifier
//                            .requiredWidth(imageWidth)
//                            .height(imageHeight)
//                            .clip(MaterialTheme.shapes.extraLarge)
//                            .zIndex(2f),
//                        url = secondEntry?.images?.webp?.largeImageUrl,
//                        contentDescription = secondEntry?.title,
//                        caption = secondEntry?.title
//                    )
//                }
//                Text(
//                    text = item.content.toString(),
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis,
//                    style = MaterialTheme.typography.titleSmall
//                )
//            }
//        }
//    }
//}