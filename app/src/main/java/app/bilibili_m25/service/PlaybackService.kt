package app.bilibili_m25.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import app.bilibili_m25.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var player: ExoPlayer? = null
    private var currentVideoUri: String? = null
    private var currentVideoTitle: String? = null

    companion object {
        const val ACTION_PLAY = "app.bilibili_m25.ACTION_PLAY"
        const val ACTION_PAUSE = "app.bilibili_m25.ACTION_PAUSE"
        const val ACTION_RESUME = "app.bilibili_m25.ACTION_RESUME"
        const val ACTION_STOP = "app.bilibili_m25.ACTION_STOP"
        const val EXTRA_VIDEO_URI = "video_uri"
        const val EXTRA_VIDEO_TITLE = "video_title"
        private const val NOTIFICATION_CHANNEL_ID = "bilibili_playback_channel"
        private const val NOTIFICATION_ID = 1
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .setUsage(C.USAGE_MEDIA)
            .build()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        createNotificationChannel()

        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, player!!)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "视频播放",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "视频播放通知"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    @OptIn(UnstableApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val uri = intent.getStringExtra(EXTRA_VIDEO_URI)
                val title = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: "视频"
                if (uri != null) {
                    currentVideoUri = uri
                    currentVideoTitle = title
                    val mediaItem = MediaItem.Builder()
                        .setUri(uri)
                        .setMediaId(uri)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(title)
                                .build()
                        )
                        .build()
                    player?.apply {
                        setMediaItem(mediaItem)
                        prepare()
                        play()
                    }
                }
            }
            ACTION_RESUME -> {
                player?.play()
            }
            ACTION_PAUSE -> {
                player?.pause()
            }
            ACTION_STOP -> {
                player?.stop()
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mediaSession?.run {
            player?.release()
            release()
            mediaSession = null
        }
        player?.release()
        player = null
        super.onDestroy()
    }
}
