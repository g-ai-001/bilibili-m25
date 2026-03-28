package app.bilibili_m25.di

import android.content.Context
import androidx.room.Room
import app.bilibili_m25.data.local.BilibiliDatabase
import app.bilibili_m25.data.local.dao.VideoDao
import app.bilibili_m25.data.repository.VideoRepositoryImpl
import app.bilibili_m25.domain.repository.VideoRepository
import app.bilibili_m25.util.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLogger(): Logger = Logger()

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BilibiliDatabase {
        return Room.databaseBuilder(
            context,
            BilibiliDatabase::class.java,
            "bilibili_m25.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideVideoDao(database: BilibiliDatabase): VideoDao {
        return database.videoDao()
    }

    @Provides
    @Singleton
    fun provideVideoRepository(
        videoDao: VideoDao,
        videoLocalDataSource: app.bilibili_m25.data.local.datasource.VideoLocalDataSource,
        logger: Logger
    ): VideoRepository {
        return VideoRepositoryImpl(videoDao, videoLocalDataSource, logger)
    }
}
