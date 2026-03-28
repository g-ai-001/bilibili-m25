package app.bilibili_m25.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val path: String,
    val duration: Long = 0,
    val size: Long = 0,
    val lastModified: Long = 0,
    val thumbnailUri: String? = null,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lastPlayPosition: Long = 0
)
