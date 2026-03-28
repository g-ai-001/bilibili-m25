package app.bilibili_m25.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private fun sendAction(action: String, vararg extras: Pair<String, String>) {
        val intent = Intent(context, PlaybackService::class.java).apply {
            this.action = action
            extras.forEach { (key, value) -> putExtra(key, value) }
        }
        context.startForegroundService(intent)
    }

    fun playVideo(uri: Uri, title: String) {
        sendAction(
            PlaybackService.ACTION_PLAY,
            PlaybackService.EXTRA_VIDEO_URI to uri.toString(),
            PlaybackService.EXTRA_VIDEO_TITLE to title
        )
    }

    fun pause() {
        sendAction(PlaybackService.ACTION_PAUSE)
    }

    fun resume() {
        sendAction(PlaybackService.ACTION_RESUME)
    }

    fun stop() {
        sendAction(PlaybackService.ACTION_STOP)
    }
}