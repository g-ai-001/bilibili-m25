package app.bilibili_m25.domain.model

import android.net.Uri

data class Video(
    val id: Long = 0,
    val title: String,
    val path: String,
    val uri: Uri,
    val duration: Long = 0,
    val size: Long = 0,
    val lastModified: Long = 0,
    val thumbnailUri: Uri? = null,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lastPlayPosition: Long = 0
)
