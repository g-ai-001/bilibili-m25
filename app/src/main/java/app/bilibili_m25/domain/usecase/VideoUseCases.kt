package app.bilibili_m25.domain.usecase

import app.bilibili_m25.domain.model.Video
import app.bilibili_m25.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllVideosUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    operator fun invoke(): Flow<List<Video>> = repository.getAllVideos()
}

class SearchVideosUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    operator fun invoke(query: String): Flow<List<Video>> = repository.searchVideos(query)
}

class GetFavoriteVideosUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    operator fun invoke(): Flow<List<Video>> = repository.getFavoriteVideos()
}

class ScanVideosUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke() = repository.scanAndSyncVideos()
}

class DeleteVideoUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(video: Video) = repository.deleteVideo(video)
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(id: Long) = repository.toggleFavorite(id)
}

class UpdatePlayPositionUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(id: Long, position: Long) = repository.updatePlayPosition(id, position)
}

class IncrementPlayCountUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(id: Long) = repository.incrementPlayCount(id)
}
