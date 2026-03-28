package app.bilibili_m25.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bilibili_m25.domain.model.Video
import app.bilibili_m25.domain.usecase.DeleteVideoUseCase
import app.bilibili_m25.domain.usecase.GetHistoryVideosUseCase
import app.bilibili_m25.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val videos: List<Video> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryVideosUseCase: GetHistoryVideosUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getHistoryVideosUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { videos ->
                    _uiState.update { it.copy(videos = videos, isLoading = false) }
                }
        }
    }

    fun toggleFavorite(videoId: Long) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(videoId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteVideo(video: Video) {
        viewModelScope.launch {
            try {
                deleteVideoUseCase(video)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
