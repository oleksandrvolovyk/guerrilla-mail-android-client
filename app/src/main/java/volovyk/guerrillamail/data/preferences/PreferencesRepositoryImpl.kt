package volovyk.guerrillamail.data.preferences

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import volovyk.guerrillamail.data.preferences.local.PreferencesDatasource

class PreferencesRepositoryImpl(private val preferencesDatasource: PreferencesDatasource) :
    PreferencesRepository {

    override suspend fun setValue(key: String, value: String) = withContext(Dispatchers.IO) {
        preferencesDatasource.setValue(key, value)
    }

    override suspend fun getValue(key: String): String? = withContext(Dispatchers.IO) {
        preferencesDatasource.getValue(key)
    }
}