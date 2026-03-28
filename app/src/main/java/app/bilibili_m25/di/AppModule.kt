package app.bilibili_m25.di

import android.content.Context
import androidx.room.Room
import app.bilibili_m25.data.local.BilibiliDatabase
import app.bilibili_m25.data.local.SortPreferences
import app.bilibili_m25.data.local.ThemePreferences
import app.bilibili_m25.data.local.dao.VideoDao
import app.bilibili_m25.data.local.datasource.VideoLocalDataSource
import app.bilibili_m25.data.repository.VideoRepositoryImpl
import app.bilibili_m25.domain.repository.VideoRepository
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
    fun provideSortPreferences(
        @ApplicationContext context: Context
    ): SortPreferences {
        return SortPreferences(context)
    }

    @Provides
    @Singleton
    fun provideThemePreferences(
        @ApplicationContext context: Context
    ): ThemePreferences {
        return ThemePreferences(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BilibiliDatabase {
        return Room.databaseBuilder(
            context,
            BilibiliDatabase::class.java,
            "bilibili_m25.db"
        )
            .fallbackToDestructiveMigration()
            .build()
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
        videoLocalDataSource: VideoLocalDataSource
    ): VideoRepository {
        return VideoRepositoryImpl(videoDao, videoLocalDataSource)
    }
}
