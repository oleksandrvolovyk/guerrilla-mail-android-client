package volovyk.guerrillamail.data.preferences

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import volovyk.guerrillamail.data.IoDispatcher
import volovyk.guerrillamail.data.preferences.local.PreferencesDatasource

class PreferencesRepositoryImpl(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val preferencesDatasource: PreferencesDatasource
) :
    PreferencesRepository {

    override suspend fun setValue(key: String, value: String) = withContext(ioDispatcher) {
        preferencesDatasource.setValue(key, value)
    }

    override suspend fun getValue(key: String): String? = withContext(ioDispatcher) {
        preferencesDatasource.getValue(key)
    }
}