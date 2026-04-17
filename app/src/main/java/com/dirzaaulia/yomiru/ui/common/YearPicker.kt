package com.dirzaaulia.yomiru.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.Year

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YearPicker(
    startYear: Int = 1900,
    endYear: Int = 2100,
    initialYear: Int,
    onYearSelected: (Int) -> Unit
) {
    val years = remember { (startYear..endYear).toList() }

    val initialIndex = (initialYear - startYear).coerceIn(0, years.lastIndex)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    val flingBehavior = rememberSnapFlingBehavior(listState)

    val centerItemIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter =
                (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2

            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                kotlin.math.abs(
                    (item.offset + item.size / 2) - viewportCenter
                )
            }?.index
        }
    }

    LaunchedEffect(centerItemIndex) {
        centerItemIndex?.let { onYearSelected(years[it]) }
    }

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        contentPadding = PaddingValues(vertical = 96.dp),
        modifier = Modifier.height(240.dp)
    ) {
        itemsIndexed(years) { index, year ->
            YearItem(
                year = year,
                isSelected = index == centerItemIndex
            )
        }
    }
}

@Composable
private fun YearItem(
    year: Int,
    isSelected: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.4f,
        label = "alpha"
    )

    Text(
        text = year.toString(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            },
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
}
