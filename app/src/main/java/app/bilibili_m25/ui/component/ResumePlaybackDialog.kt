package app.bilibili_m25.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import app.bilibili_m25.util.FormatUtils

@Composable
fun ResumePlaybackDialog(
    lastPosition: Long,
    duration: Long,
    onResumeClick: () -> Unit,
    onStartOverClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val lastPositionStr = FormatUtils.formatDuration(lastPosition)
    val durationStr = FormatUtils.formatDuration(duration)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("续播提示") },
        text = {
            Text("上次播放到 $lastPositionStr / $durationStr，是否从上次位置继续播放？")
        },
        confirmButton = {
            TextButton(onClick = onResumeClick) {
                Text("继续播放")
            }
        },
        dismissButton = {
            TextButton(onClick = onStartOverClick) {
                Text("重新开始")
            }
        }
    )
}