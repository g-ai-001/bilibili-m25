package app.bilibili_m25.ui.screen.history

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.bilibili_m25.ui.component.VideoGridContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onVideoClick: (Long) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("播放历史") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        VideoGridContent(
            videos = uiState.videos,
            isLoading = uiState.isLoading,
            error = uiState.error,
            emptyText = "暂无播放历史",
            onVideoClick = onVideoClick,
            onFavoriteClick = { viewModel.toggleFavorite(it) },
            onDeleteClick = { viewModel.deleteVideo(it) },
            onRetry = { viewModel.refreshHistory() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}
