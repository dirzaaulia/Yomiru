package id.pgidata.gomamam.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dirzaaulia.yomiru.model.MalGenre
import io.ktor.http.ContentType.Application.Json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    val accessTokenFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[accessTokenKey].orEmpty()
        }

    suspend fun setAccessToken(value: String) {
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = value
        }
    }

    val refreshTokenFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[refreshTokenKey].orEmpty()
        }

    suspend fun setRefreshTokenToken(value: String) {
        context.dataStore.edit { preferences ->
            preferences[refreshTokenKey] = value
        }
    }

    val expiresInFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[expiresInKey] ?: 0
        }

    suspend fun setExpiresIn(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[expiresInKey] = value
        }
    }

    val genreFlow: Flow<List<MalGenre>> = context.dataStore.data
        .map { preferences ->
            val jsonStr = preferences[genreKey].orEmpty()
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<List<MalGenre>>(jsonStr)
        }

    suspend fun setListGenre(value: List<MalGenre>) {
        context.dataStore.edit { preferences ->
            val json = Json { ignoreUnknownKeys = true }
            val listJson = json.encodeToString(value)
            preferences[genreKey] = listJson
        }
    }
}