package app.bilibili_m25.ui.screen.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.bilibili_m25.ui.component.SortDialog
import app.bilibili_m25.ui.component.VideoGridContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onVideoClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    showFavoritesOnly: Boolean = false,
    onFolderClick: (String) -> Unit = {},
    onFoldersClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var hasPermission by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.refresh()
        }
    }

    LaunchedEffect(showFavoritesOnly) {
        viewModel.loadVideos(showFavoritesOnly)
    }

    if (showSortDialog) {
        SortDialog(
            currentSortOrder = uiState.sortOrder,
            onSortOrderSelected = { viewModel.setSortOrder(it) },
            onDismiss = { showSortDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (showFavoritesOnly) "我的收藏" else "哔哩哔哩")
                },
                actions = {
                    if (!showFavoritesOnly) {
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, contentDescription = "搜索")
                        }
                        IconButton(onClick = { showSortDialog = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "排序")
                        }
                        IconButton(onClick = {
                            if (hasPermission) {
                                viewModel.refresh()
                            } else {
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
                            }
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "刷新")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!showFavoritesOnly) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedButton(
                        onClick = onFoldersClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("文件夹")
                    }
                }
            }

            VideoGridContent(
                videos = uiState.videos,
                isLoading = uiState.isLoading,
                error = uiState.error,
                emptyText = if (showFavoritesOnly) "暂无收藏" else "暂无视频",
                onVideoClick = onVideoClick,
                onFavoriteClick = { viewModel.toggleFavorite(it) },
                onDeleteClick = { viewModel.deleteVideo(it) },
                onRetry = { viewModel.refresh() },
                modifier = Modifier.weight(1f)
            )

            if (uiState.isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}