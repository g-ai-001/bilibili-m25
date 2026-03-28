package app.bilibili_m25.ui.screen.folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bilibili_m25.domain.model.Video
import app.bilibili_m25.domain.repository.VideoRepository
import app.bilibili_m25.domain.usecase.DeleteVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FolderDetailUiState(
    val folderName: String = "",
    val videos: List<Video> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class FolderDetailViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val deleteVideoUseCase: DeleteVideoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val folderPath: String = savedStateHandle.get<String>("folderPath") ?: ""

    private val _uiState = MutableStateFlow(FolderDetailUiState())
    val uiState: StateFlow<FolderDetailUiState> = _uiState.asStateFlow()

    init {
        loadVideos()
    }

    fun loadVideos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val videos = videoRepository.getVideosByFolder(folderPath)
                _uiState.update {
                    it.copy(
                        folderName = folderPath.substringAfterLast("/"),
                        videos = videos,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun toggleFavorite(videoId: Long) {
        viewModelScope.launch {
            try {
                videoRepository.toggleFavorite(videoId)
                loadVideos()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteVideo(video: Video) {
        viewModelScope.launch {
            try {
                deleteVideoUseCase(video)
                loadVideos()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}