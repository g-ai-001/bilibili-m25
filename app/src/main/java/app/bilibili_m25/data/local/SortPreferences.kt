package app.bilibili_m25.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sortDataStore: DataStore<Preferences> by preferencesDataStore(name = "sort_preferences")

@Singleton
class SortPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sortOrderKey = stringPreferencesKey("sort_order")

    fun getSortOrder(): Flow<VideoSortOrder> {
        return context.sortDataStore.data.map { preferences ->
            val orderName = preferences[sortOrderKey] ?: VideoSortOrder.TIME_DESC.name
            try {
                VideoSortOrder.valueOf(orderName)
            } catch (e: IllegalArgumentException) {
                VideoSortOrder.TIME_DESC
            }
        }
    }

    suspend fun setSortOrder(order: VideoSortOrder) {
        context.sortDataStore.edit { preferences ->
            preferences[sortOrderKey] = order.name
        }
    }
}
