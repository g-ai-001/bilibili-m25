package app.bilibili_m25.util

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Logger {
    private var logFile: File? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE)
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE)

    fun init(context: Context) {
        val logDir = context.getExternalFilesDir(null) ?: return
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        val fileName = "bilibili_${fileNameFormat.format(Date())}.log"
        logFile = File(logDir, fileName)
    }

    fun d(tag: String, message: String) {
        log("DEBUG", tag, message)
    }

    fun i(tag: String, message: String) {
        log("INFO", tag, message)
    }

    fun w(tag: String, message: String) {
        log("WARN", tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        log("ERROR", tag, message)
        throwable?.let {
            log("ERROR", tag, it.stackTraceToString())
        }
    }

    private fun log(level: String, tag: String, message: String) {
        val logMessage = "${dateFormat.format(Date())} [$level] $tag: $message"
        println(logMessage)
        logFile?.let {
            try {
                FileWriter(it, true).use { writer ->
                    writer.write("$logMessage\n")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getLogDir(): File? {
        return logFile?.parentFile
    }
}