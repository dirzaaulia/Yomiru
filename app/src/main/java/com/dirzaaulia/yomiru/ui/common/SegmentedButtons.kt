package com.dirzaaulia.yomiru.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.dirzaaulia.yomiru.screen.home.YomiruMenu

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SegmentedButtons(
    modifier: Modifier = Modifier,
    selectedMenu: YomiruMenu = YomiruMenu.Anime,
    onMenuSelected: (YomiruMenu) -> Unit = {}
) {
    val selectedIndex = if (selectedMenu == YomiruMenu.Manga) 1 else 0
    SegmentedButtons(
        modifier = modifier,
        selectedIndex = selectedIndex,
        onOptionSelected = { index ->
            val menu = if (index == 1) YomiruMenu.Manga else YomiruMenu.Anime
            onMenuSelected(menu)
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SegmentedButtons(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    options: List<String> = listOf("Anime", "Manga"),
    onOptionSelected: (Int) -> Unit
) {
    Row(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = {
                    onOptionSelected(index)
                },
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                }
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
