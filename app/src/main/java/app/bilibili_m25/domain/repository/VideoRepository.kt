package app.bilibili_m25.domain.repository

import app.bilibili_m25.data.local.VideoSortOrder
import app.bilibili_m25.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getAllVideos(): Flow<List<Video>>
    fun getAllVideos(sortOrder: VideoSortOrder): Flow<List<Video>>
    fun searchVideos(query: String): Flow<List<Video>>
    fun getFavoriteVideos(): Flow<List<Video>>
    fun getHistoryVideos(): Flow<List<Video>>
    suspend fun getVideoById(id: Long): Video?
    suspend fun scanAndSyncVideos()
    suspend fun updateVideo(video: Video)
    suspend fun deleteVideo(video: Video)
    suspend fun toggleFavorite(id: Long)
    suspend fun updatePlayPosition(id: Long, position: Long)
    suspend fun incrementPlayCount(id: Long)
}
