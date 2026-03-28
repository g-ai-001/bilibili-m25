package app.bilibili_m25.domain.model

data class VideoFolder(
    val path: String,
    val name: String,
    val videoCount: Int,
    val lastModified: Long
)
