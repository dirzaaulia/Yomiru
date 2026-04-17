package com.dirzaaulia.yomiru.screen.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.dirzaaulia.yomiru.model.MalReview
import com.dirzaaulia.yomiru.ui.common.NetworkImage

@Composable
fun ItemReview(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    imageWidth: Dp,
    imageHeight: Dp,
    item: MalReview
) {
    when {
        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            // return for EXPANDED width size class
            ItemReviewExpandedWidth(
                modifier = modifier,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                item = item
            )
        }

//        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
//            // return for MEDIUM width size class
////                StaggeredGridCells.Adaptive(120.dp)
//            StaggeredGridCells.Fixed(2)
//        }

        else -> {
            // return for COMPACT width size class
            ItemReviewMediumWidth(
                modifier = modifier,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                item = item
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemReviewExpandedWidth(
    modifier: Modifier = Modifier,
    imageWidth: Dp,
    imageHeight: Dp,
    item: MalReview
) {
    val tooltipState = rememberTooltipState()
    Card(modifier = modifier) {
        Row {
            ImageWithCaption(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraLarge)
                    .size(width = imageWidth, height = imageHeight),
                url = item.entry?.images?.webp?.largeImageUrl,
                contentDescription = item.entry?.title,
                caption = item.entry?.title
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NetworkImage(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        url = item.user?.images?.webp?.imageUrl,
                        contentDescription = item.user?.username,
                        contentScale = ContentScale.FillBounds
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.user?.username.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = item.score.toString())
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.review.toString(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )
                FlowRow(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Above,
                            4.dp
                        ),
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
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ItemReviewMediumWidth(
    modifier: Modifier = Modifier,
    imageWidth: Dp,
    imageHeight: Dp,
    item: MalReview
) {
    val tooltipState = rememberTooltipState()

    Card(modifier = modifier) {
        Box {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NetworkImage(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        url = item.user?.images?.webp?.imageUrl,
                        contentDescription = item.user?.username,
                        contentScale = ContentScale.FillBounds
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.user?.username.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = item.score.toString())
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                ImageWithCaption(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .size(width = imageWidth, height = imageHeight),
                    url = item.entry?.images?.webp?.largeImageUrl,
                    contentDescription = item.entry?.title,
                    caption = item.entry?.title
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.review.toString(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )
                FlowRow(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
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
            }
        }
    }
}