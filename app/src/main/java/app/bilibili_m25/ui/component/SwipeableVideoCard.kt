package app.bilibili_m25.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.bilibili_m25.domain.model.Video

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableVideoCard(
    video: Video,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    // Swipe right - toggle favorite
                    onFavoriteClick()
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    // Swipe left - show delete confirmation
                    showDeleteDialog = true
                    false
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { it * 0.4f }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除视频") },
            text = { Text("确定要从列表中删除「${video.title}」吗？\n注意：此操作不会删除视频文件。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color by animateColorAsState(
                when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> if (video.isFavorite) Color(0xFFFF5252) else Color(0xFF4CAF50)
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFFF5252)
                    else -> Color.Transparent
                },
                label = "swipe_color"
            )
            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> if (video.isFavorite) Icons.Default.Favorite else Icons.Default.Favorite
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                else -> null
            }
            val contentDesc = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> if (video.isFavorite) "取消收藏" else "收藏"
                SwipeToDismissBoxValue.EndToStart -> "删除"
                else -> ""
            }

            if (icon != null) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color, RoundedCornerShape(8.dp))
                        .padding(horizontal = 20.dp),
                    contentAlignment = alignment
                ) {
                    Icon(
                        icon,
                        contentDescription = contentDesc,
                        tint = Color.White
                    )
                }
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true
    ) {
        VideoCard(
            video = video,
            onClick = onClick,
            onFavoriteClick = onFavoriteClick
        )
    }
}