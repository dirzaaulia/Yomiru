package com.dirzaaulia.yomiru.screen.list

import android.content.ActivityNotFoundException
import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.dirzaaulia.yomiru.AuthCallbackViewModel
import com.dirzaaulia.yomiru.BuildConfig
import com.dirzaaulia.yomiru.MALAuth
import com.dirzaaulia.yomiru.pagingsource.AnimeStatus
import com.dirzaaulia.yomiru.ui.common.NetworkImage
import com.dirzaaulia.yomiru.ui.common.SegmentedButtons
import com.dirzaaulia.yomiru.ui.common.VerticalStaggeredGridPaging
import com.dirzaaulia.yomiru.util.PKCEUtil
import com.dirzaaulia.yomiru.util.capitalizeWords
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    viewModel: ListViewModel = koinViewModel(),
    authViewModel: AuthCallbackViewModel = koinViewModel(),
    backStack: NavBackStack<NavKey>
) {
    val context = LocalContext.current
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val accessToken by viewModel.accessToken.collectAsStateWithLifecycle(initialValue = null)
    val nodeList = viewModel.nodeList.collectAsLazyPagingItems()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var statusExpanded by remember { mutableStateOf(false) }

    when {
        accessToken == null -> {
            Box(modifier = Modifier.fillMaxSize()) {
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            }
        }

        accessToken!!.isEmpty() -> {
            MyAnimeListAuthDialog(
                dialogTitle = "MyAnimeList Authorization",
                dialogText = "You are about to access your Anime & Manga list. " +
                        "Please authorize this app to access your MyAnimeList data",
                icon = Icons.Default.Info,
                onDismissRequest = {
                    onBackPressedDispatcherOwner?.onBackPressedDispatcher?.onBackPressed()
                },
                onConfirmation = {
                    val authUrl = buildMyAnimeListAuthUrl(
                        viewModel = authViewModel
                    )
                    val customTabsIntent = CustomTabsIntent.Builder().build()
                    try {
                        customTabsIntent.launchUrl(context, authUrl.toUri())
                    } catch (e: ActivityNotFoundException) {
                        Log.e("ListScreen", "Custom Tab not supported or browser not found.", e)
                        scope.launch {
                            snackbarHostState.showSnackbar("Your current browser not support to do this!")
                        }
                    }
                }
            )
        }

        else -> {
            val gridColumn =
                if (!windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
                    StaggeredGridCells.Adaptive(120.dp)
                } else {
                    when {
                        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                            // return for EXPANDED width size class
                            StaggeredGridCells.Adaptive(120.dp)
                        }

                        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                            // return for MEDIUM width size class
                            StaggeredGridCells.Adaptive(120.dp)
                        }

                        else -> {
                            // return for COMPACT width size class
                            StaggeredGridCells.Fixed(2)
                        }
                    }
                }
            BottomSheetScaffold(
                modifier = Modifier.fillMaxSize(),
                scaffoldState = bottomSheetScaffoldState,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                sheetPeekHeight = 0.dp,
                sheetContent = {
                    Text(
                        text = "Sheet Content For Now",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    SegmentedButtons { index ->
                        viewModel.setSelectedIndex(index)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Your List",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Box {
                            AssistChip(
                                onClick = {
                                    statusExpanded = !statusExpanded
                                },
                                label = {
                                    Text(
                                        text = AnimeStatus.entries[selectedStatus]
                                            .name
                                            .replace("_", " ")
                                            .capitalizeWords()
                                    )
                                }
                            )
                            DropdownMenu(
                                expanded = statusExpanded,
                                onDismissRequest = { statusExpanded = false }
                            ) {
                                AnimeStatus.entries.forEachIndexed { index, item ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                item.name
                                                    .replace("_", " ")
                                                    .capitalizeWords()
                                            )
                                        },
                                        onClick = {
                                            statusExpanded = false
                                            viewModel.setSelectedStatus(index)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    VerticalStaggeredGridPaging(
                        list = nodeList,
                        columns = gridColumn,
                        emptyContent = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No anime yet on your list for status ${
                                        AnimeStatus.entries[selectedStatus]
                                            .name
                                            .replace("_", " ")
                                            .capitalizeWords()
                                    }"
                                )
                            }
                        }
                    ) { item ->
                        Card(
                            modifier = Modifier
                                .clickable {
                                    scope.launch {
                                        if (bottomSheetScaffoldState.bottomSheetState.currentValue
                                                == SheetValue.Hidden) {
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        } else bottomSheetScaffoldState.bottomSheetState.hide()
                                    }
                                }
                        ) {
                            NetworkImage(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .clip(MaterialShapes.Slanted.toShape())
                                    .fillMaxWidth(),
                                url = item.node?.mainPicture?.large.toString(),
                                contentDescription = item.node?.title,
                                contentScale = ContentScale.FillWidth
                            )
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = item.node?.title.toString(),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyAnimeListAuthDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

private fun buildMyAnimeListAuthUrl(
    viewModel: AuthCallbackViewModel
): String {
    // --- PKCE Generation ---
    val codeVerifier = PKCEUtil.generateCodeVerifier()
    val codeChallenge = PKCEUtil.generateCodeChallenge(codeVerifier)

    // --- State Generation ---
    val state = UUID.randomUUID().toString()

    val malAuth = MALAuth(
        codeVerifier = codeVerifier,
        codeChallenge = codeChallenge,
        state = state
    )
    viewModel.malAuth = malAuth

    val clientId = BuildConfig.MAL_CLIENT_ID

    val authUrl = "https://myanimelist.net/v1/oauth2/authorize".toUri()
        .buildUpon()
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("client_id", clientId)
        .appendQueryParameter("state", state)
        .appendQueryParameter("code_challenge", codeChallenge)
        // Add any other required parameters like 'scope'
        .appendQueryParameter("scope", "write:users") // Example scopes
        .build()
        .toString()

    Log.d("LoginWithMALButton", "Auth URL: $authUrl")
    Log.d("LoginWithMALButton", "Code Verifier (store this): $codeVerifier")

    return authUrl
}