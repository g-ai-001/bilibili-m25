package app.bilibili_m25.ui.screen.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bilibili_m25.data.repository.PlayQueueManager
import app.bilibili_m25.domain.model.Video
import app.bilibili_m25.domain.repository.VideoRepository
import app.bilibili_m25.domain.usecase.IncrementPlayCountUseCase
import app.bilibili_m25.domain.usecase.UpdatePlayPositionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideoPlayerUiState(
    val video: Video? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val playQueue: List<Video> = emptyList(),
    val currentIndex: Int = -1,
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false
)

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val updatePlayPositionUseCase: UpdatePlayPositionUseCase,
    private val incrementPlayCountUseCase: IncrementPlayCountUseCase,
    private val playQueueManager: PlayQueueManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                playQueueManager.playQueue,
                playQueueManager.currentIndex
            ) { queue, index ->
                Pair(queue, index)
            }.collect { (queue, index) ->
                _uiState.update {
                    it.copy(
                        playQueue = queue,
                        currentIndex = index,
                        hasNext = index < queue.size - 1,
                        hasPrevious = index > 0
                    )
                }
            }
        }
    }

    fun loadVideo(videoId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            loadVideoInternal(videoId)
        }
    }

    fun loadVideoWithQueue(videoId: Long, videos: List<Video>, startIndex: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            playQueueManager.setQueue(videos, startIndex)
            loadVideoInternal(videoId)
        }
    }

    private suspend fun loadVideoInternal(videoId: Long) {
        try {
            val video = videoRepository.getVideoById(videoId)
            if (video != null) {
                incrementPlayCountUseCase(videoId)
                _uiState.update { it.copy(video = video, isLoading = false) }
            } else {
                _uiState.update { it.copy(error = "视频不存在", isLoading = false) }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message, isLoading = false) }
        }
    }

    fun savePlayPosition(position: Long) {
        val videoId = _uiState.value.video?.id ?: return
        viewModelScope.launch {
            try {
                updatePlayPositionUseCase(videoId, position)
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    fun playNext(): Video? {
        val nextVideo = playQueueManager.playNext()
        if (nextVideo != null) {
            _uiState.update { it.copy(video = nextVideo) }
        }
        return nextVideo
    }

    fun playPrevious(): Video? {
        val prevVideo = playQueueManager.playPrevious()
        if (prevVideo != null) {
            _uiState.update { it.copy(video = prevVideo) }
        }
        return prevVideo
    }

    fun addToQueue(video: Video) {
        playQueueManager.addToQueue(video)
    }

    fun removeFromQueue(videoId: Long) {
        playQueueManager.removeFromQueue(videoId)
    }

    fun clearQueue() {
        playQueueManager.clearQueue()
    }
}
