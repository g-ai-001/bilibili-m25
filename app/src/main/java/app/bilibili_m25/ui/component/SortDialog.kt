package app.bilibili_m25.ui.component

import androidx.compose.material3.*
import androidx.compose.runtime.*
import app.bilibili_m25.data.local.VideoSortOrder

@Composable
fun SortDialog(
    currentSortOrder: VideoSortOrder,
    onSortOrderSelected: (VideoSortOrder) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("排序方式") },
        text = {
            Column {
                VideoSortOrder.entries.forEach { order ->
                    val label = when (order) {
                        VideoSortOrder.NAME_ASC -> "名称升序"
                        VideoSortOrder.NAME_DESC -> "名称降序"
                        VideoSortOrder.TIME_ASC -> "时间升序"
                        VideoSortOrder.TIME_DESC -> "时间降序"
                        VideoSortOrder.SIZE_ASC -> "大小升序"
                        VideoSortOrder.SIZE_DESC -> "大小降序"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = currentSortOrder == order,
                            onClick = {
                                onSortOrderSelected(order)
                                onDismiss()
                            }
                        )
                        Text(
                            text = label,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
