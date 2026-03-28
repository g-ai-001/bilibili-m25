package app.bilibili_m25.data.repository

import android.net.Uri
import app.bilibili_m25.data.local.VideoSortOrder
import app.bilibili_m25.data.local.dao.VideoDao
import app.bilibili_m25.data.local.datasource.VideoLocalDataSource
import app.bilibili_m25.data.local.entity.VideoEntity
import app.bilibili_m25.domain.model.Video
import app.bilibili_m25.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val videoDao: VideoDao,
    private val videoLocalDataSource: VideoLocalDataSource
) : VideoRepository {

    override fun getAllVideos(): Flow<List<Video>> {
        return videoDao.getAllVideos().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAllVideos(sortOrder: VideoSortOrder): Flow<List<Video>> {
        val flow = when (sortOrder) {
            VideoSortOrder.NAME_ASC -> videoDao.getAllVideosByNameAsc()
            VideoSortOrder.NAME_DESC -> videoDao.getAllVideosByNameDesc()
            VideoSortOrder.TIME_ASC -> videoDao.getAllVideosByTimeAsc()
            VideoSortOrder.TIME_DESC -> videoDao.getAllVideosByTimeDesc()
            VideoSortOrder.SIZE_ASC -> videoDao.getAllVideosBySizeAsc()
            VideoSortOrder.SIZE_DESC -> videoDao.getAllVideosBySizeDesc()
        }
        return flow.map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun searchVideos(query: String): Flow<List<Video>> {
        return videoDao.searchVideos(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getFavoriteVideos(): Flow<List<Video>> {
        return videoDao.getFavoriteVideos().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getHistoryVideos(): Flow<List<Video>> {
        return videoDao.getHistoryVideos().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getVideoById(id: Long): Video? {
        return videoDao.getVideoById(id)?.toDomainModel()
    }

    override suspend fun scanAndSyncVideos() {
        val scannedVideos = videoLocalDataSource.scanVideos()

        scannedVideos.forEach { scanned ->
            val existing = videoDao.getVideoByPath(scanned.path)
            if (existing == null) {
                videoDao.insertVideo(scanned)
            } else {
                if (existing.lastModified != scanned.lastModified) {
                    videoDao.updateVideo(scanned.copy(id = existing.id))
                }
            }
        }

        val scannedPaths = scannedVideos.map { it.path }.toSet()
        val allVideos = videoDao.getAllVideosOnce()
        allVideos.filter { it.path !in scannedPaths }.forEach { orphaned ->
            videoDao.deleteVideo(orphaned)
        }
    }

    override suspend fun updateVideo(video: Video) {
        videoDao.updateVideo(video.toEntity())
    }

    override suspend fun deleteVideo(video: Video) {
        videoDao.deleteVideo(video.toEntity())
    }

    override suspend fun toggleFavorite(id: Long) {
        val video = videoDao.getVideoById(id) ?: return
        videoDao.updateFavorite(id, !video.isFavorite)
    }

    override suspend fun updatePlayPosition(id: Long, position: Long) {
        videoDao.updatePlayPosition(id, position)
    }

    override suspend fun incrementPlayCount(id: Long) {
        videoDao.incrementPlayCount(id)
    }

    private fun VideoEntity.toDomainModel(): Video {
        return Video(
            id = id,
            title = title,
            path = path,
            uri = Uri.parse(path),
            duration = duration,
            size = size,
            lastModified = lastModified,
            thumbnailUri = thumbnailUri?.let { Uri.parse(it) },
            isFavorite = isFavorite,
            playCount = playCount,
            lastPlayPosition = lastPlayPosition
        )
    }

    private fun Video.toEntity(): VideoEntity {
        return VideoEntity(
            id = id,
            title = title,
            path = path,
            duration = duration,
            size = size,
            lastModified = lastModified,
            thumbnailUri = thumbnailUri?.toString(),
            isFavorite = isFavorite,
            playCount = playCount,
            lastPlayPosition = lastPlayPosition
        )
    }
}
