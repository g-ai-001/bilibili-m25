package app.bilibili_m25.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import app.bilibili_m25.util.Logger
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "PlaybackManager"
    }

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    fun connectToService(onConnected: (MediaController) -> Unit = {}) {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )

        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                mediaController = controllerFuture?.get()
                mediaController?.let { onConnected(it) }
                logger.i(TAG, "Connected to PlaybackService")
            } catch (e: Exception) {
                logger.e(TAG, "Failed to connect to PlaybackService", e)
            }
        }, MoreExecutors.directExecutor())
    }

    fun disconnect() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        mediaController = null
        controllerFuture = null
        logger.i(TAG, "Disconnected from PlaybackService")
    }

    fun playVideo(uri: Uri, title: String) {
        val intent = Intent(context, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_PLAY
            putExtra(PlaybackService.EXTRA_VIDEO_URI, uri.toString())
            putExtra(PlaybackService.EXTRA_VIDEO_TITLE, title)
        }
        context.startForegroundService(intent)
    }

    fun pause() {
        mediaController?.pause()
    }

    fun resume() {
        mediaController?.play()
    }

    fun stop() {
        val intent = Intent(context, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun isPlaying(): Boolean {
        return mediaController?.isPlaying == true
    }
}
