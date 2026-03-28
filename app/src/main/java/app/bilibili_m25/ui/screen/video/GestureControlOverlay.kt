package app.bilibili_m25.ui.screen.video

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import app.bilibili_m25.util.FormatUtils
import kotlinx.coroutines.delay
import kotlin.math.abs

enum class GestureType {
    SEEK, VOLUME, BRIGHTNESS
}

@Composable
fun GestureControlOverlay(
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier,
    onScreenshotClick: () -> Unit = {}
) {
    val context = LocalContext.current

    var currentGesture by remember { mutableStateOf<GestureType?>(null) }
    var gestureValue by remember { mutableFloatStateOf(0f) }
    var showIndicator by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var initialBrightness by remember { mutableFloatStateOf(0.5f) }

    // 更新播放状态
    LaunchedEffect(exoPlayer) {
        while (true) {
            isPlaying = exoPlayer.isPlaying
            currentPosition = exoPlayer.currentPosition
            duration = exoPlayer.duration.coerceAtLeast(1L)
            delay(100)
        }
    }

    // 获取当前亮度
    LaunchedEffect(Unit) {
        val window = (context as? android.app.Activity)?.window
        val params = window?.attributes
        initialBrightness = params?.screenBrightness ?: 0.5f
    }

    // 自动隐藏控制栏
    LaunchedEffect(showControls) {
        if (showControls && isPlaying) {
            delay(3000)
            showControls = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable { showControls = !showControls }
            .pointerInput(exoPlayer) {
                var totalHorizontalDrag = 0f
                var initialX = 0f
                var hasMoved = false
                var gestureType: GestureType? = null

                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        initialX = offset.x
                        totalHorizontalDrag = 0f
                        hasMoved = false
                        gestureType = null
                    },
                    onDragEnd = {
                        if (!hasMoved && gestureType == null) {
                            // 点击事件，切换控制器显示
                            showControls = !showControls
                        }
                        currentGesture = null
                        showIndicator = false
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (gestureType == null || gestureType == GestureType.SEEK) {
                            gestureType = GestureType.SEEK
                            hasMoved = true
                            showIndicator = true
                            showControls = false
                            currentGesture = GestureType.SEEK
                            totalHorizontalDrag += dragAmount

                            val seekDelta = (totalHorizontalDrag * 3).toLong()
                            val newPosition = (exoPlayer.currentPosition + seekDelta).coerceIn(0, exoPlayer.duration)
                            gestureValue = newPosition.toFloat() / exoPlayer.duration.toFloat().coerceAtLeast(1f)
                            exoPlayer.seekTo(newPosition)
                        }
                        change.consume()
                    }
                )
            }
            .pointerInput(exoPlayer) {
                var totalVerticalDrag = 0f
                var initialX = 0f
                var hasMoved = false
                var gestureType: GestureType? = null

                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        initialX = offset.x
                        totalVerticalDrag = 0f
                        hasMoved = false
                        gestureType = null
                    },
                    onDragEnd = {
                        currentGesture = null
                        showIndicator = false
                    },
                    onVerticalDrag = { change, dragAmount ->
                        if (gestureType == null) {
                            gestureType = if (initialX > size.width / 2) GestureType.VOLUME else GestureType.BRIGHTNESS
                            hasMoved = true
                            showIndicator = true
                            showControls = false
                            currentGesture = gestureType
                        }

                        if (gestureType == GestureType.VOLUME) {
                            totalVerticalDrag -= dragAmount
                            val volumeChange = (totalVerticalDrag / size.height).coerceIn(-1f, 1f)
                            val newVolume = (exoPlayer.volume + volumeChange * 0.02f).coerceIn(0f, 1f)
                            exoPlayer.volume = newVolume
                            gestureValue = newVolume
                            currentGesture = GestureType.VOLUME
                        } else if (gestureType == GestureType.BRIGHTNESS) {
                            totalVerticalDrag -= dragAmount
                            val brightnessChange = (totalVerticalDrag / size.height).coerceIn(-1f, 1f)
                            val newBrightness = (initialBrightness + brightnessChange * 0.5f).coerceIn(0.01f, 1f)

                            val window = (context as? android.app.Activity)?.window
                            val params = window?.attributes
                            params?.screenBrightness = newBrightness
                            window?.attributes = params

                            gestureValue = newBrightness
                            currentGesture = GestureType.BRIGHTNESS
                        }

                        change.consume()
                    }
                )
            }
    ) {
        // 播放进度条（底部）
        if (showControls) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                // 进度条
                Slider(
                    value = currentPosition.toFloat() / duration.toFloat().coerceAtLeast(1f),
                    onValueChange = { value ->
                        val newPosition = (value * duration).toLong()
                        exoPlayer.seekTo(newPosition)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 时间显示
                    Text(
                        text = "${FormatUtils.formatDuration(currentPosition)} / ${FormatUtils.formatDuration(duration)}",
                        color = Color.White,
                        fontSize = 12.sp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 截图按钮
                        IconButton(onClick = onScreenshotClick) {
                            Icon(
                                imageVector = Icons.Default.Screenshot,
                                contentDescription = "截图",
                                tint = Color.White
                            )
                        }

                        // 播放/暂停按钮
                        IconButton(
                            onClick = {
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                } else {
                                    exoPlayer.play()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "暂停" else "播放",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 手势指示器
        if (showIndicator && currentGesture != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        alpha = 0.8f
                    }
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (currentGesture) {
                            GestureType.SEEK -> "进度"
                            GestureType.VOLUME -> "音量"
                            GestureType.BRIGHTNESS -> "亮度"
                            else -> ""
                        },
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (currentGesture) {
                            GestureType.SEEK -> "${(gestureValue * 100).toInt()}%"
                            GestureType.VOLUME -> "${(gestureValue * 100).toInt()}%"
                            GestureType.BRIGHTNESS -> "${(gestureValue * 100).toInt()}%"
                            else -> ""
                        },
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}