package app.bilibili_m25.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.playbackSpeedDataStore: DataStore<Preferences> by preferencesDataStore(name = "playback_speed_preferences")

@Singleton
class PlaybackSpeedPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val PLAYBACK_SPEED_KEY = floatPreferencesKey("playback_speed")

    companion object {
        val PLAYBACK_SPEEDS = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        const val DEFAULT_SPEED = 1.0f
    }

    fun getPlaybackSpeed(): Flow<Float> {
        return context.playbackSpeedDataStore.data.map { preferences ->
            preferences[PLAYBACK_SPEED_KEY] ?: DEFAULT_SPEED
        }
    }

    suspend fun setPlaybackSpeed(speed: Float) {
        context.playbackSpeedDataStore.edit { preferences ->
            preferences[PLAYBACK_SPEED_KEY] = speed
        }
    }
}