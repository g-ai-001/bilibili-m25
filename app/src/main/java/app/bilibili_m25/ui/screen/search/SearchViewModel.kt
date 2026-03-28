package app.bilibili_m25.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bilibili_m25.domain.model.Video
import app.bilibili_m25.domain.usecase.SearchVideosUseCase
import app.bilibili_m25.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<Video> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchVideosUseCase: SearchVideosUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }

        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), hasSearched = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            _uiState.update { it.copy(isSearching = true) }

            searchVideosUseCase(query)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            results = emptyList(),
                            isSearching = false,
                            hasSearched = true
                        )
                    }
                }
                .collect { videos ->
                    _uiState.update {
                        it.copy(
                            results = videos,
                            isSearching = false,
                            hasSearched = true
                        )
                    }
                }
        }
    }

    fun toggleFavorite(videoId: Long) {
        viewModelScope.launch {
            toggleFavoriteUseCase(videoId)
        }
    }
}
