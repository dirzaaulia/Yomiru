package com.dirzaaulia.yomiru.screen.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.dirzaaulia.yomiru.model.MalGenre
import com.dirzaaulia.yomiru.model.request.SearchQuery
import com.dirzaaulia.yomiru.navigation.SearchType
import com.dirzaaulia.yomiru.ui.common.YearPicker
import com.dirzaaulia.yomiru.util.capitalizeWords
import java.time.LocalDate
import java.time.Year
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSheet(
    genre: List<MalGenre>,
    type: SearchType,
    doSearchWithFilter: (SearchQuery) -> Unit = { }
) {

    val scrollState = rememberScrollState()
    val rangeSliderState =
        rememberRangeSliderState(
            0f,
            10f,
            valueRange = 0f..10f,
            steps = 10,
            onValueChangeFinished = {
                // launch some business logic update with the state you hold
                // viewModel.updateSelectedSliderValue(sliderPosition)
            },
        )
    val rangeStart = rangeSliderState.activeRangeStart.roundToInt().toString()
    val rangeEnd = rangeSliderState.activeRangeEnd.roundToInt().toString()
    var checked by remember { mutableStateOf(true) }
    val queryState = rememberTextFieldState("")
    val queryLabel = if (type == SearchType.SEARCH_ANIME) "Anime Title" else "Manga Title"
    var searchQuery by remember { mutableStateOf(SearchQuery()) }

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        //Query
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            state = queryState,
            lineLimits = TextFieldLineLimits.SingleLine,
            label = { Text(queryLabel) },
        )
        //Type
        val typeList = if (type == SearchType.SEARCH_ANIME) {
            SearchAnimeType.entries
        } else {
            SearchMangaType.entries
        }.map {
            it.name.capitalizeWords().replace("_", " ")
        }
        AutoCompleteSelect(
            title = "Type",
            options = typeList
        ) {
            searchQuery = searchQuery.copy(type = it.lowercase())
        }
        //Score
        Text(
            text = "Score",
            style = MaterialTheme.typography.bodyLarge
        )
        RangeSlider(
            state = rangeSliderState,
            startThumb = {
                // Custom thumb for the start value without the default icon
                Text(
                    text = rangeStart,
                    style = MaterialTheme.typography.labelLarge, // Making text a bit larger
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .padding(6.dp)
                )
            },
            endThumb = {
                // Custom thumb for the end value without the default icon
                Text(
                    text = rangeEnd,
                    style = MaterialTheme.typography.labelLarge, // Making text a bit larger
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .padding(6.dp)
                )
            }
        )
        //Status
        val statusList = if (type == SearchType.SEARCH_ANIME) {
            SearchAnimeStatus.entries
        } else {
            SearchMangaStatus.entries
        }.map {
            it.name.capitalizeWords().replace("_", " ")
        }
        AutoCompleteSelect(
            title = "Status",
            options = statusList
        ) {
            searchQuery = searchQuery.copy(status = it.lowercase())
        }
        //Ratings
        val ratingsList = SearchAnimeRating.entries.map { item -> item.description }
        AutoCompleteSelect(
            isVisible = type == SearchType.SEARCH_ANIME,
            title = "Ratings",
            options = ratingsList
        ) {
            val rating = SearchAnimeRating.fromDescription(it)?.name.orEmpty()
            searchQuery = searchQuery.copy(rating = rating)
        }
        //Safe For Works
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "SFW",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                modifier = Modifier.semantics { contentDescription = "Demo" },
                checked = checked,
                onCheckedChange = {
                    checked = it
                    searchQuery.sfw = it
                },
            )
        }
        //Genre
        AutoCompleteSelect(
            title = "Genre",
            options = genre.map { it.name }
        ) { name ->
            val genre = genre.find { it.name == name }
            searchQuery = searchQuery.copy(genres = genre?.id)
        }
        //Order By
        val orderByList = if (type == SearchType.SEARCH_ANIME) {
            SearchAnimeOrderBy.entries
        } else {
            SearchMangaOrderBy.entries
        }.map {
            it.name.capitalizeWords().replace("_", " ")
        }
        AutoCompleteSelect(
            title = "Order By",
            options = orderByList
        ) {
            val orderBy = it.replace(" ", "_").lowercase()
            searchQuery = searchQuery.copy(orderBy = orderBy)
        }
        //Sort
        val sortList = SearchSort.entries.map { item -> item.description }
        AutoCompleteSelect(
            title = "Ratings",
            options = sortList
        ) {
            val sort = SearchSort.fromDescription(it)?.name.orEmpty()
            searchQuery = searchQuery.copy(sort = sort)
        }
        Spacer(modifier = Modifier.height(12.dp))
        //Button Search
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                searchQuery = searchQuery.copy(
                    query = queryState.text.toString(),
                    minScore = rangeSliderState.activeRangeStart.roundToInt(),
                    maxScore = rangeSliderState.activeRangeEnd.roundToInt()
                )
                Log.d("TAG_DIRZA", searchQuery.toString())
                doSearchWithFilter.invoke(searchQuery)
            }
        ) {
            Text("Search")
        }
    }
}

@Composable
fun SeasonalSearchSheet(
    modifier: Modifier = Modifier,
    initialYear: Int = Year.now().value,
    initialSeason: AnimeSeason = AnimeSeason.fromMonth(LocalDate.now().monthValue),
    onFilterChanged: (year: Int, season: AnimeSeason, type: String, sfw: Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedYear by remember { mutableIntStateOf(initialYear) }
    var selectedSeason by remember { mutableStateOf(initialSeason) }
    var selectedType by remember { mutableStateOf("") }
    var sfwChecked by remember { mutableStateOf(true) }

    // Helper to trigger update to parent whenever local state changes
    val updateParent = {
        onFilterChanged(selectedYear, selectedSeason, selectedType, sfwChecked)
    }

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Year",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 1. Year Picker
        YearPicker(initialYear = selectedYear) { year ->
            selectedYear = year
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Season Selector (Chips)
        Text(text = "Season", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimeSeason.entries.forEach { season ->
                val isSelected = selectedSeason == season
                AssistChip(
                    onClick = {
                        selectedSeason = season
                    },
                    label = { Text(season.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    colors = if (isSelected) {
                        AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else AssistChipDefaults.assistChipColors()
                )
            }
        }

        // 3. Anime Type (Format)
        val typeOptions = SearchSeasonalType.entries.map { it.name.replace("_", " ") }
        AutoCompleteSelect(
            title = "Format (TV, Movie, etc.)",
            options = typeOptions
        ) { name ->
            selectedType = name.replace(" ", "").lowercase()
        }

        // 4. SFW Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Safe For Work (SFW)", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = sfwChecked,
                onCheckedChange = {
                    sfwChecked = it
                }
            )
        }

        // 5. Button Search
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                updateParent.invoke()
            }
        ) {
            Text("Search")
        }
    }
}

@Composable
fun SearchSheetTopManga(
    modifier: Modifier = Modifier,
    onFilterChanged: (String, String) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedType by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("") }

    // Helper to trigger update to parent whenever local state changes
    val updateParent = {
        onFilterChanged(selectedType, selectedFilter)
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Type Filter (TV, Movie, etc.)
        AutoCompleteSelect(
            title = "Type",
            options = SearchMangaType.entries.map { it.name },
        ) { selected ->
            selectedType = selected
        }

        // 2. Filter (Airing, Upcoming, etc.)
        // Mapping to the 'status' field in SearchQuery
        AutoCompleteSelect(
            title = "Filter",
            options = SearchTopMangaFilter.entries.map { it.toDisplayString() },
        ) { selected ->
            selectedFilter = selected
        }

        // 3.Button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                updateParent.invoke()
            }
        ) {
            Text("Search")
        }
    }
}


@Composable
fun SearchSheetTopAnime(
    modifier: Modifier = Modifier,
    onFilterChanged: (String, String, String, Boolean) -> Unit
) {

    val scrollState = rememberScrollState()
    var selectedType by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("") }
    var selectedRating by remember { mutableStateOf("") }
    var sfwChecked by remember { mutableStateOf(true) }

    // Helper to trigger update to parent whenever local state changes
    val updateParent = {
        onFilterChanged(selectedType, selectedFilter, selectedRating, sfwChecked)
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Type Filter (TV, Movie, etc.)
        AutoCompleteSelect(
            title = "Type",
            options = SearchAnimeType.entries.map { it.name },
        ) { selected ->
            selectedType = selected
        }

        // 2. Filter (Airing, Upcoming, etc.)
        // Mapping to the 'status' field in SearchQuery
        AutoCompleteSelect(
            title = "Filter",
            options = SearchTopAnimeFilter.entries.map { it.toDisplayString() },
        ) { selected ->
            selectedFilter = selected
        }

        // 3. Content Rating (G, PG-13, etc.)
        AutoCompleteSelect(
            title = "Rating",
            options = SearchAnimeRating.entries.map { it.description },
        ) { description ->
            val code = SearchAnimeRating.fromDescription(description)?.code
            selectedRating = code.toString()
        }

        // 4. SFW Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Safe For Work", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = sfwChecked,
                onCheckedChange = { isChecked ->
                    sfwChecked = isChecked
                }
            )
        }

        // 5.Button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                updateParent.invoke()
            }
        ) {
            Text("Search")
        }
    }
}