package app.bilibili_m25.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class ScreenshotHelper @Inject constructor() {
    companion object {
        private const val TAG = "ScreenshotHelper"
    }

    suspend fun takeScreenshot(
        context: Context,
        exoPlayer: ExoPlayer,
        videoTitle: String
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val bitmap = suspendCancellableCoroutine<Bitmap?> { continuation ->
                try {
                    val retriever = MediaMetadataRetriever()
                    val uri = exoPlayer.currentMediaItem?.localConfiguration?.uri
                    if (uri != null) {
                        retriever.setDataSource(context, uri)
                        val frame = retriever.getFrameAtTime(
                            exoPlayer.currentPosition * 1000, // Convert to microseconds
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                        )
                        retriever.release()
                        continuation.resume(frame)
                    } else {
                        retriever.release()
                        continuation.resume(null)
                    }
                } catch (e: Exception) {
                    Logger.e(TAG, "Failed to capture frame", e)
                    continuation.resume(null)
                }
            }

            if (bitmap == null) {
                return@withContext Result.failure(Exception("无法捕获视频画面"))
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val sanitizedTitle = videoTitle.replace(Regex("[^\\w\\u4e00-\\u9fa5]"), "_").take(50)
            val fileName = "IMG_${sanitizedTitle}_$timestamp.jpg"

            val uri = saveBitmapToGallery(context, bitmap, fileName)
            bitmap.recycle()

            if (uri != null) {
                Logger.i(TAG, "Screenshot saved: $uri")
                Result.success(uri)
            } else {
                Result.failure(Exception("保存截图失败"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Screenshot failed", e)
            Result.failure(e)
        }
    }

    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/bilibili-m25")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)
                }
            }

            uri
        } catch (e: Exception) {
            Logger.e(TAG, "Failed to save bitmap to gallery", e)
            null
        }
    }
}