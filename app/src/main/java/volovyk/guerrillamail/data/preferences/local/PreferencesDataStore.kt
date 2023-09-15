package volovyk.guerrillamail.data.preferences.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferencesDataStore(private val appContext: Context): PreferencesDatasource {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override suspend fun setValue(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        appContext.dataStore.edit { settings ->
            settings[preferencesKey] = value
        }
    }

    override suspend fun getValue(key: String): String? {
        val preferencesKey = stringPreferencesKey(key)
        return appContext.dataStore.data
            .map { preferences ->
                preferences[preferencesKey]
            }.first()
    }
}