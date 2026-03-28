package app.bilibili_m25.ui.screen.video

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Rational
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import app.bilibili_m25.domain.model.Video
import app.bilibili_m25.ui.component.PlaybackSpeedDialog
import app.bilibili_m25.ui.component.PlayQueueDialog
import app.bilibili_m25.ui.component.ResumePlaybackDialog

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    videoId: Long,
    onBackClick: () -> Unit,
    onDetailClick: () -> Unit = {},
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current

    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var showQueue by remember { mutableStateOf(false) }
    var showSpeedSelector by remember { mutableStateOf(false) }
    var showResumeDialog by remember { mutableStateOf(false) }
    var pendingSeekPosition by remember { mutableStateOf(0L) }

    val isInPipMode = remember { mutableStateOf(false) }

    LaunchedEffect(videoId) {
        viewModel.loadVideo(videoId)
    }

    LaunchedEffect(uiState.video) {
        uiState.video?.let { video ->
            exoPlayer?.release()
            val player = ExoPlayer.Builder(context).build()
            player.setMediaItem(MediaItem.fromUri(video.uri))
            player.setPlaybackSpeed(uiState.playbackSpeed)
            player.prepare()
            player.playWhenReady = true

            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        viewModel.savePlayPosition(0)
                        val nextVideo = viewModel.playNext()
                        if (nextVideo != null) {
                            player.setMediaItem(MediaItem.fromUri(nextVideo.uri))
                            player.setPlaybackSpeed(uiState.playbackSpeed)
                            player.prepare()
                            player.playWhenReady = true
                        }
                    }
                }
            })

            if (video.lastPlayPosition > 0 && video.duration > 0) {
                val progress = video.lastPlayPosition.toFloat() / video.duration.toFloat()
                if (progress > 0.05f && progress < 0.95f) {
                    pendingSeekPosition = video.lastPlayPosition
                    showResumeDialog = true
                } else {
                    player.seekTo(video.lastPlayPosition)
                }
            }
            exoPlayer = player
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

    fun enterPipMode() {
        val activity = context as? Activity ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val aspectRatio = Rational(16, 9)
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(aspectRatio)
                .build()
            activity.enterPictureInPictureMode(params)
            isInPipMode.value = true
        }
    }

    if (showResumeDialog && uiState.video != null) {
        val video = uiState.video!!
        ResumePlaybackDialog(
            lastPosition = pendingSeekPosition,
            duration = video.duration,
            onResumeClick = {
                exoPlayer?.seekTo(pendingSeekPosition)
                showResumeDialog = false
            },
            onStartOverClick = {
                exoPlayer?.seekTo(0)
                showResumeDialog = false
            },
            onDismiss = {
                exoPlayer?.seekTo(pendingSeekPosition)
                showResumeDialog = false
            }
        )
    }

    if (!isInPipMode.value) {
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
                        IconButton(onClick = onDetailClick) {
                            Icon(Icons.Default.Info, contentDescription = "视频详情")
                        }
                        IconButton(onClick = { enterPipMode() }) {
                            Icon(Icons.Default.PictureInPicture, contentDescription = "画中画")
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
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            exoPlayer?.let { player ->
                VideoPlayerView(
                    exoPlayer = player,
                    modifier = Modifier.fillMaxSize()
                )
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