package app.bilibili_m25.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bilibili_m25.domain.model.Video
import app.bilibili_m25.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideoDetailUiState(
    val video: Video? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class VideoDetailViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoDetailUiState())
    val uiState: StateFlow<VideoDetailUiState> = _uiState.asStateFlow()

    fun loadVideo(videoId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val video = videoRepository.getVideoById(videoId)
                _uiState.update { it.copy(video = video, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun toggleFavorite() {
        val videoId = _uiState.value.video?.id ?: return
        viewModelScope.launch {
            try {
                videoRepository.toggleFavorite(videoId)
                val updatedVideo = videoRepository.getVideoById(videoId)
                _uiState.update { it.copy(video = updatedVideo) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}