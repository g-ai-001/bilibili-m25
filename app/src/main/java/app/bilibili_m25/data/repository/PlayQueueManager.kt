package app.bilibili_m25.data.repository

import app.bilibili_m25.domain.model.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayQueueManager @Inject constructor() {

    private val _playQueue = MutableStateFlow<List<Video>>(emptyList())
    val playQueue: StateFlow<List<Video>> = _playQueue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _currentVideo = MutableStateFlow<Video?>(null)
    val currentVideo: StateFlow<Video?> = _currentVideo.asStateFlow()

    fun setQueue(videos: List<Video>, startIndex: Int = 0) {
        _playQueue.value = videos
        _currentIndex.value = startIndex.coerceIn(-1, videos.size - 1)
        if (startIndex in videos.indices) {
            _currentVideo.value = videos[startIndex]
        }
    }

    fun addToQueue(video: Video) {
        val currentQueue = _playQueue.value.toMutableList()
        currentQueue.add(video)
        _playQueue.value = currentQueue
    }

    fun removeFromQueue(videoId: Long) {
        val currentQueue = _playQueue.value.toMutableList()
        val indexToRemove = currentQueue.indexOfFirst { it.id == videoId }
        if (indexToRemove != -1) {
            currentQueue.removeAt(indexToRemove)
            _playQueue.value = currentQueue

            when {
                indexToRemove < _currentIndex.value -> {
                    _currentIndex.value = (_currentIndex.value - 1).coerceAtLeast(0)
                }
                indexToRemove == _currentIndex.value -> {
                    if (currentQueue.isEmpty()) {
                        _currentIndex.value = -1
                        _currentVideo.value = null
                    } else {
                        _currentIndex.value = _currentIndex.value.coerceIn(0, currentQueue.size - 1)
                        _currentVideo.value = currentQueue.getOrNull(_currentIndex.value)
                    }
                }
            }
        }
    }

    fun playNext(): Video? {
        val nextIndex = _currentIndex.value + 1
        if (nextIndex < _playQueue.value.size) {
            _currentIndex.value = nextIndex
            _currentVideo.value = _playQueue.value[nextIndex]
            return _currentVideo.value
        }
        return null
    }

    fun playPrevious(): Video? {
        val prevIndex = _currentIndex.value - 1
        if (prevIndex >= 0) {
            _currentIndex.value = prevIndex
            _currentVideo.value = _playQueue.value[prevIndex]
            return _currentVideo.value
        }
        return null
    }

    fun clearQueue() {
        _playQueue.value = emptyList()
        _currentIndex.value = -1
        _currentVideo.value = null
    }

    fun hasNext(): Boolean = _currentIndex.value < _playQueue.value.size - 1

    fun hasPrevious(): Boolean = _currentIndex.value > 0
}