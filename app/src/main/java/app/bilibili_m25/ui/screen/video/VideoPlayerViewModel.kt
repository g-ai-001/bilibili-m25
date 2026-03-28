package app.bilibili_m25.ui.screen.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val error: String? = null
)

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val updatePlayPositionUseCase: UpdatePlayPositionUseCase,
    private val incrementPlayCountUseCase: IncrementPlayCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()

    fun loadVideo(videoId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
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
    }

    fun savePlayPosition(position: Long) {
        val videoId = _uiState.value.video?.id ?: return
        viewModelScope.launch {
            try {
                updatePlayPositionUseCase(videoId, position)
            } catch (e) {
                // Silently fail
            }
        }
    }
}
