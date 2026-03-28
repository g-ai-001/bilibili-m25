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
    fun playVideo(uri: Uri, title: String) {
        val intent = Intent(context, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_PLAY
            putExtra(PlaybackService.EXTRA_VIDEO_URI, uri.toString())
            putExtra(PlaybackService.EXTRA_VIDEO_TITLE, title)
        }
        context.startForegroundService(intent)
    }

    fun pause() {
        val intent = Intent(context, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    fun resume() {
        val intent = Intent(context, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_RESUME
        }
        context.startService(intent)
    }

    fun stop() {
        val intent = Intent(context, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_STOP
        }
        context.startService(intent)
    }
}
