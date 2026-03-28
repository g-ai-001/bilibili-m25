package app.bilibili_m25.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bilibili_m25.domain.model.Video
import app.bilibili_m25.domain.usecase.GetAllVideosUseCase
import app.bilibili_m25.domain.usecase.GetFavoriteVideosUseCase
import app.bilibili_m25.domain.usecase.ScanVideosUseCase
import app.bilibili_m25.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val videos: List<Video> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllVideosUseCase: GetAllVideosUseCase,
    private val getFavoriteVideosUseCase: GetFavoriteVideosUseCase,
    private val scanVideosUseCase: ScanVideosUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var showFavoritesOnly = false

    init {
        loadVideos()
    }

    fun loadVideos(showFavorites: Boolean = false) {
        showFavoritesOnly = showFavorites
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val flow = if (showFavorites) {
                getFavoriteVideosUseCase()
            } else {
                getAllVideosUseCase()
            }

            flow.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect { videos ->
                _uiState.update { it.copy(videos = videos, isLoading = false) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                scanVideosUseCase()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
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
}
