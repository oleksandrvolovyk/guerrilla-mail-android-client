package volovyk.guerrillamail.data.preferences

interface PreferencesRepository {
    suspend fun setValue(key: String, value: String)
    suspend fun getValue(key: String): String?
}