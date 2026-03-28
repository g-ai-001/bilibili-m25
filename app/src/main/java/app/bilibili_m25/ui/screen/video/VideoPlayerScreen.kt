package app.bilibili_m25.ui.screen.video

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import app.bilibili_m25.data.local.PlaybackSpeedPreferences
import app.bilibili_m25.domain.model.Video

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    videoId: Long,
    onBackClick: () -> Unit,
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var showQueue by remember { mutableStateOf(false) }
    var showSpeedSelector by remember { mutableStateOf(false) }

    LaunchedEffect(videoId) {
        viewModel.loadVideo(videoId)
    }

    LaunchedEffect(uiState.video) {
        uiState.video?.let { video ->
            exoPlayer?.release()
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(video.uri)
                setMediaItem(mediaItem)
                seekTo(video.lastPlayPosition)
                setPlaybackSpeed(uiState.playbackSpeed)
                prepare()
                playWhenReady = true

                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            viewModel.savePlayPosition(0)
                            val nextVideo = viewModel.playNext()
                            if (nextVideo != null) {
                                val nextMediaItem = MediaItem.fromUri(nextVideo.uri)
                                setMediaItem(nextMediaItem)
                                setPlaybackSpeed(uiState.playbackSpeed)
                                prepare()
                                playWhenReady = true
                            }
                        }
                    }
                })
            }
        }
    }

    LaunchedEffect(uiState.playbackSpeed) {
        exoPlayer?.setPlaybackSpeed(uiState.playbackSpeed)
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer?.let { player ->
                viewModel.savePlayPosition(player.currentPosition)
                player.release()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.video?.title ?: "视频播放",
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (uiState.playQueue.isNotEmpty()) {
                        IconButton(onClick = { showQueue = true }) {
                            Icon(Icons.Default.Queue, contentDescription = "播放队列")
                        }
                    }
                    IconButton(onClick = { showSpeedSelector = true }) {
                        Text(
                            text = "${uiState.playbackSpeed}x",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    IconButton(onClick = {
                        uiState.video?.let { viewModel.addToQueue(it) }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "添加到队列")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "未知错误",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                exoPlayer != null -> {
                    VideoPlayerView(
                        exoPlayer = exoPlayer!!,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    if (showQueue) {
        PlayQueueDialog(
            queue = uiState.playQueue,
            currentIndex = uiState.currentIndex,
            onDismiss = { showQueue = false },
            onVideoClick = { index ->
                val video = uiState.playQueue.getOrNull(index)
                if (video != null) {
                    viewModel.loadVideoWithQueue(video.id, uiState.playQueue, index)
                }
                showQueue = false
            },
            onRemove = { videoId ->
                viewModel.removeFromQueue(videoId)
            },
            onClear = {
                viewModel.clearQueue()
                showQueue = false
            }
        )
    }

    if (showSpeedSelector) {
        PlaybackSpeedDialog(
            currentSpeed = uiState.playbackSpeed,
            onDismiss = { showSpeedSelector = false },
            onSpeedSelected = { speed ->
                viewModel.setPlaybackSpeed(speed)
                showSpeedSelector = false
            }
        )
    }
}

@Composable
private fun PlayQueueDialog(
    queue: List<Video>,
    currentIndex: Int,
    onDismiss: () -> Unit,
    onVideoClick: (Int) -> Unit,
    onRemove: (Long) -> Unit,
    onClear: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("播放队列 (${queue.size})") },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(queue.size) { index ->
                    val video = queue[index]
                    val isCurrent = index == currentIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onVideoClick(index) }
                            .background(
                                if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                else Color.Transparent
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isCurrent) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "正在播放",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = video.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        IconButton(onClick = { onRemove(video.id) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "移除",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    if (index < queue.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClear) {
                Text("清空队列")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

@Composable
private fun PlaybackSpeedDialog(
    currentSpeed: Float,
    onDismiss: () -> Unit,
    onSpeedSelected: (Float) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("播放倍速") },
        text = {
            Column {
                PlaybackSpeedPreferences.PLAYBACK_SPEEDS.forEach { speed ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSpeedSelected(speed) }
                            .background(
                                if (speed == currentSpeed) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                else Color.Transparent
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${speed}x",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (speed == currentSpeed) {
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "当前",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayerView(
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                useController = true
                setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
            }
        },
        modifier = modifier
    )
}
