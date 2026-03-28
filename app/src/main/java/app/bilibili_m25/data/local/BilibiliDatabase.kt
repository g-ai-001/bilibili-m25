package app.bilibili_m25.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import app.bilibili_m25.data.local.dao.VideoDao
import app.bilibili_m25.data.local.entity.VideoEntity

@Database(
    entities = [VideoEntity::class],
    version = 1,
    exportSchema = true
)
abstract class BilibiliDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
}
