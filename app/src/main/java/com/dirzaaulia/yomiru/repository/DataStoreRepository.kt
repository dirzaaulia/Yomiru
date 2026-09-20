package id.pgidata.gomamam.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dirzaaulia.yomiru.model.MediaGenre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataStoreRepository (
    private val context: Context
) {

    private val Context.dataStore: DataStore<Preferences>
            by preferencesDataStore(name = "datastore")

    val accessTokenKey = stringPreferencesKey("accessTokenKey")
    val refreshTokenKey = stringPreferencesKey("refreshTokenKey")
    val expiresInKey = intPreferencesKey("expiresInKey")
    val genreKey = stringPreferencesKey("genreKey")
    val onboardingCompletedKey = booleanPreferencesKey("isOnboardingCompletedKey")

    val accessTokenFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[accessTokenKey].orEmpty()
        }

    suspend fun setAccessToken(value: String) {
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = value
        }
    }

    val refreshTokenFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[refreshTokenKey].orEmpty()
        }

    suspend fun setRefreshTokenToken(value: String) {
        context.dataStore.edit { preferences ->
            preferences[refreshTokenKey] = value
        }
    }

    val expiresInFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[expiresInKey] ?: 0
        }

    suspend fun setExpiresIn(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[expiresInKey] = value
        }
    }

    val genreFlow: Flow<List<MediaGenre>> = context.dataStore.data
        .map { preferences ->
            val jsonStr = preferences[genreKey].orEmpty()
            if (jsonStr.isBlank()) {
                emptyList()
            } else {
                val json = Json { ignoreUnknownKeys = true }
                json.decodeFromString<List<MediaGenre>>(jsonStr)
            }
        }

    suspend fun setListGenre(value: List<MediaGenre>) {
        context.dataStore.edit { preferences ->
            val json = Json { ignoreUnknownKeys = true }
            val listJson = json.encodeToString(value)
            preferences[genreKey] = listJson
        }
    }

    val isOnboardingCompletedFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[onboardingCompletedKey] ?: false
        }

    suspend fun setOnboardingCompleted(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[onboardingCompletedKey] = value
        }
    }
}
