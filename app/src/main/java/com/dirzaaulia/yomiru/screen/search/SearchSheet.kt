package com.dirzaaulia.yomiru.screen.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberRangeSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dirzaaulia.yomiru.model.MediaGenre
import com.dirzaaulia.yomiru.model.request.SearchQuery
import com.dirzaaulia.yomiru.navigation.SearchType
import com.dirzaaulia.yomiru.ui.common.YearPicker
import com.dirzaaulia.yomiru.util.capitalizeWords
import java.time.LocalDate
import java.time.Year
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchSheet(
    genre: List<MediaGenre>,
    type: SearchType,
    isCompactHeight: Boolean = false,
    doSearchWithFilter: (SearchQuery) -> Unit = { }
) {
    val scrollState = rememberScrollState()
    val rangeSliderState = rememberRangeSliderState(
        0f,
        10f,
        valueRange = 0f..10f,
        steps = 10
    )
    val rangeStart = rangeSliderState.activeRangeStart.roundToInt().toString()
    val rangeEnd = rangeSliderState.activeRangeEnd.roundToInt().toString()
    var checked by remember { mutableStateOf(true) }
    val queryState = rememberTextFieldState("")
    val queryLabel = if (type == SearchType.SEARCH_ANIME) "Anime Title" else "Manga Title"
    var searchQuery by remember { mutableStateOf(SearchQuery()) }

    var selectedFormat by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf("") }
    var selectedGenreId by remember { mutableStateOf<Int?>(null) }
    var selectedGenreName by remember { mutableStateOf<String?>(null) }

    val sortOptions = listOf(
        "Popularity" to "POPULARITY_DESC",
        "Score" to "SCORE_DESC",
        "Start Date" to "START_DATE_DESC",
        "Trending" to "TRENDING_DESC",
        "Title" to "TITLE_ROMAJI"
    )

    val formats = if (type == SearchType.SEARCH_ANIME) {
        SearchAnimeType.entries.map { it.name.capitalizeWords().replace("_", " ") }
    } else {
        SearchMangaType.entries.map { it.name.capitalizeWords().replace("_", " ") }
    }

    if (isCompactHeight) {
        // 2-Column Side-By-Side Layout for Phone Landscape
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = queryState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    label = { Text(queryLabel) }
                )

                Text(text = "Format / Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    formats.forEach { fmt ->
                        val isSelected = selectedFormat.equals(fmt, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedFormat = if (isSelected) "" else fmt
                                searchQuery = searchQuery.copy(type = selectedFormat.lowercase().replace(" ", "_"))
                            },
                            label = { Text(fmt) },
                            shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                        )
                    }
                }

                Text(text = "Sort By", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sortOptions.forEach { (label, value) ->
                        val isSelected = selectedSort == value
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedSort = if (isSelected) "" else value
                                searchQuery = searchQuery.copy(sort = if (selectedSort.isBlank()) null else selectedSort)
                            },
                            label = { Text(label) },
                            shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "Score Range ($rangeStart - $rangeEnd)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                RangeSlider(
                    state = rangeSliderState,
                    startThumb = {
                        Text(
                            text = rangeStart,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                                .padding(4.dp)
                        )
                    },
                    endThumb = {
                        Text(
                            text = rangeEnd,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                                .padding(4.dp)
                        )
                    }
                )

                Surface(
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Safe For Work (SFW)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                searchQuery.sfw = it
                            }
                        )
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                    onClick = {
                        searchQuery = searchQuery.copy(
                            query = queryState.text.toString(),
                            minScore = rangeSliderState.activeRangeStart.roundToInt(),
                            maxScore = rangeSliderState.activeRangeEnd.roundToInt()
                        )
                        doSearchWithFilter.invoke(searchQuery)
                    }
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("APPLY FILTERS • 検索", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        // Standard Single-Column Layout
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SEARCH FILTERS • フィルター",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                state = queryState,
                lineLimits = TextFieldLineLimits.SingleLine,
                label = { Text(queryLabel) }
            )

            Column {
                Text(text = "Format / Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    formats.forEach { fmt ->
                        val isSelected = selectedFormat.equals(fmt, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedFormat = if (isSelected) "" else fmt
                                searchQuery = searchQuery.copy(type = selectedFormat.lowercase().replace(" ", "_"))
                            },
                            label = { Text(fmt) },
                            shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                        )
                    }
                }
            }

            Column {
                Text(text = "Sort By", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sortOptions.forEach { (label, value) ->
                        val isSelected = selectedSort == value
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedSort = if (isSelected) "" else value
                                searchQuery = searchQuery.copy(sort = if (selectedSort.isBlank()) null else selectedSort)
                            },
                            label = { Text(label) },
                            shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                        )
                    }
                }
            }

            Column {
                Text(text = "Score Range ($rangeStart - $rangeEnd)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                RangeSlider(
                    state = rangeSliderState,
                    startThumb = {
                        Text(
                            text = rangeStart,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                                .padding(6.dp)
                        )
                    },
                    endThumb = {
                        Text(
                            text = rangeEnd,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                                .padding(6.dp)
                        )
                    }
                )
            }

            Surface(
                shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Safe For Work (SFW)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                            searchQuery.sfw = it
                        }
                    )
                }
            }

            if (genre.isNotEmpty()) {
                Column {
                    Text(text = "Genre", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        genre.take(16).forEach { item ->
                            val isSelected = selectedGenreId == item.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedGenreId = if (isSelected) null else item.id
                                    selectedGenreName = if (isSelected) null else item.name
                                    searchQuery = searchQuery.copy(
                                        genres = selectedGenreId,
                                        genreName = selectedGenreName
                                    )
                                },
                                label = { Text(item.name) },
                                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                            )
                        }
                    }
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
                onClick = {
                    searchQuery = searchQuery.copy(
                        query = queryState.text.toString(),
                        minScore = rangeSliderState.activeRangeStart.roundToInt(),
                        maxScore = rangeSliderState.activeRangeEnd.roundToInt()
                    )
                    doSearchWithFilter.invoke(searchQuery)
                }
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("APPLY FILTERS • 検索", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SeasonalSearchSheet(
    modifier: Modifier = Modifier,
    genre: List<MediaGenre> = emptyList(),
    initialYear: Int = Year.now().value,
    initialSeason: AnimeSeason = AnimeSeason.fromMonth(LocalDate.now().monthValue),
    isCompactHeight: Boolean = false,
    onFilterChanged: (year: Int, season: AnimeSeason, type: String, sort: String, genreId: Int?, sfw: Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedYear by remember { mutableIntStateOf(initialYear) }
    var selectedSeason by remember { mutableStateOf(initialSeason) }
    var selectedFormat by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf("POPULARITY_DESC") }
    var selectedGenreId by remember { mutableStateOf<Int?>(null) }
    var sfwChecked by remember { mutableStateOf(true) }

    val sortOptions = listOf(
        "Popularity" to "POPULARITY_DESC",
        "Score" to "SCORE_DESC",
        "Start Date" to "START_DATE_DESC",
        "Trending" to "TRENDING_DESC"
    )

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        YearPicker(initialYear = selectedYear) { year ->
            selectedYear = year
        }

        Text(text = "Season", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimeSeason.entries.forEach { season ->
                val isSelected = selectedSeason == season
                AssistChip(
                    onClick = { selectedSeason = season },
                    label = { Text(season.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    colors = if (isSelected) {
                        AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else AssistChipDefaults.assistChipColors()
                )
            }
        }

        Text(text = "Format / Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SearchSeasonalType.entries.map { it.name.replace("_", " ") }.forEach { fmt ->
                val formatCode = fmt.replace(" ", "_").uppercase()
                val isSelected = selectedFormat.equals(formatCode, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedFormat = if (isSelected) "" else formatCode
                    },
                    label = { Text(fmt) },
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                )
            }
        }

        Text(text = "Sort By", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sortOptions.forEach { (label, value) ->
                val isSelected = selectedSort == value
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedSort = value },
                    label = { Text(label) },
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                )
            }
        }

        if (genre.isNotEmpty()) {
            Column {
                Text(text = "Genre", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    genre.take(16).forEach { item ->
                        val isSelected = selectedGenreId == item.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedGenreId = if (isSelected) null else item.id
                            },
                            label = { Text(item.name) },
                            shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                        )
                    }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
            onClick = {
                onFilterChanged(selectedYear, selectedSeason, selectedFormat, selectedSort, selectedGenreId, sfwChecked)
            }
        ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("APPLY FILTERS • 検索", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchSheetTopManga(
    modifier: Modifier = Modifier,
    onFilterChanged: (format: String?, sort: String?, country: String?) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedCategory by remember { mutableStateOf("All Time Top") }

    val rankingCategories = listOf(
        "All Time Top" to ("SCORE_DESC" to null to null),
        "Most Popular" to ("POPULARITY_DESC" to null to null),
        "Trending Now" to ("TRENDING_DESC" to null to null),
        "Most Favorite" to ("FAVOURITES_DESC" to null to null),
        "Manga" to ("SCORE_DESC" to "MANGA" to "JP"),
        "Manhwa (Korea)" to ("SCORE_DESC" to "MANGA" to "KR"),
        "Manhua (China)" to ("SCORE_DESC" to "MANGA" to "CN"),
        "Light Novels" to ("SCORE_DESC" to "NOVEL" to null),
        "One Shots" to ("SCORE_DESC" to "ONE_SHOT" to null)
    )

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(text = "Top Manga Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rankingCategories.forEach { (label, config) ->
                val isSelected = selectedCategory == label
                FilterChip(
                    selected = isSelected,
                    onClick = { 
                        selectedCategory = label
                        val sort = config.first.first
                        val format = config.first.second
                        val country = config.second
                        onFilterChanged(format, sort, country)
                    },
                    label = { Text(label) },
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchSheetTopAnime(
    modifier: Modifier = Modifier,
    onFilterChanged: (type: String?, status: String?, sort: String?, sfw: Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedCategory by remember { mutableStateOf("All Time Top") }

    val rankingCategories = listOf(
        "All Time Top" to ("SCORE_DESC" to null to null),
        "Most Popular" to ("POPULARITY_DESC" to null to null),
        "Trending Now" to ("TRENDING_DESC" to null to null),
        "Most Favorite" to ("FAVOURITES_DESC" to null to null),
        "Top Airing" to ("SCORE_DESC" to "RELEASING" to null),
        "Top Upcoming" to ("POPULARITY_DESC" to "NOT_YET_RELEASED" to null),
        "Top Movies" to ("SCORE_DESC" to null to "MOVIE"),
        "Top TV Series" to ("SCORE_DESC" to null to "TV")
    )

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(text = "Top Anime Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rankingCategories.forEach { (label, config) ->
                val isSelected = selectedCategory == label
                FilterChip(
                    selected = isSelected,
                    onClick = { 
                        selectedCategory = label
                        val sort = config.first.first
                        val status = config.first.second
                        val format = config.second
                        onFilterChanged(format, status, sort, true)
                    },
                    label = { Text(label) },
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                )
            }
        }
    }
}
