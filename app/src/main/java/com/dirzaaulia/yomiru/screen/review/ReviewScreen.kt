package com.dirzaaulia.yomiru.screen.review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dirzaaulia.yomiru.model.MalReview
import com.dirzaaulia.yomiru.screen.component.DynamicCircleOutlineText
import com.dirzaaulia.yomiru.screen.component.ImageWithCaption
import com.dirzaaulia.yomiru.ui.common.NetworkImage
import com.dirzaaulia.yomiru.util.getCarouselHomeSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    item: MalReview
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val carouselSize = getCarouselHomeSize(windowSizeClass)
    val scrollState = rememberScrollState()
    val tooltipState = rememberTooltipState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageWithCaption(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .size(width = carouselSize.first, height = carouselSize.second),
                    url = item.entry?.images?.webp?.largeImageUrl,
                    contentDescription = item.entry?.title,
                    caption = item.entry?.title
                )
                Spacer(modifier = Modifier.weight(1f))
                DynamicCircleOutlineText(
                    text = item.score.toString(),
                    textSyle = MaterialTheme.typography.displayLarge
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                NetworkImage(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    url = item.user?.images?.webp?.imageUrl,
                    contentDescription = item.user?.username,
                    contentScale = ContentScale.FillBounds
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.user?.username.toString(),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(4.dp),
                    tooltip = {
                        PlainTooltip { Text("Overall") }
                    },
                    state = tooltipState
                ) {
                    Card(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = "⭐ ${item.reactions?.overall}"
                        )
                    }
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(4.dp),
                    tooltip = {
                        PlainTooltip { Text("Nice") }
                    },
                    state = tooltipState
                ) {
                    Card(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = "\uD83D\uDE0A ${item.reactions?.nice}"
                        )
                    }
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(4.dp),
                    tooltip = {
                        PlainTooltip { Text("Nice") }
                    },
                    state = tooltipState
                ) {
                    Card(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = "❤\uFE0F ${item.reactions?.loveIt}"
                        )
                    }
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(4.dp),
                    tooltip = {
                        PlainTooltip { Text("Nice") }
                    },
                    state = tooltipState
                ) {
                    Card(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = "\uD83D\uDE02 ${item.reactions?.funny}"
                        )
                    }
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(4.dp),
                    tooltip = {
                        PlainTooltip { Text("Nice") }
                    },
                    state = tooltipState
                ) {
                    Card(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = "\uD83D\uDE15 ${item.reactions?.confusing}"
                        )
                    }
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(4.dp),
                    tooltip = {
                        PlainTooltip { Text("Nice") }
                    },
                    state = tooltipState
                ) {
                    Card(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = "\uD83D\uDCDA ${item.reactions?.informative}"
                        )
                    }
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(4.dp),
                    tooltip = {
                        PlainTooltip { Text("Nice") }
                    },
                    state = tooltipState
                ) {
                    Card(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = "✍\uFE0F ${item.reactions?.wellWritten}"
                        )
                    }
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(4.dp),
                    tooltip = {
                        PlainTooltip { Text("Nice") }
                    },
                    state = tooltipState
                ) {
                    Card(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = "\uD83C\uDFA8 ${item.reactions?.creative}"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.review.toString(),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

