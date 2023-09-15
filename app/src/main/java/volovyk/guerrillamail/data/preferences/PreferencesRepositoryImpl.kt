package volovyk.guerrillamail.data.preferences

import volovyk.guerrillamail.data.preferences.local.PreferencesDatasource

class PreferencesRepositoryImpl(private val preferencesDatasource: PreferencesDatasource) :
    PreferencesRepository {

    override suspend fun setValue(key: String, value: String) {
        preferencesDatasource.setValue(key, value)
    }

    override suspend fun getValue(key: String): String? {
        return preferencesDatasource.getValue(key)
    }
}