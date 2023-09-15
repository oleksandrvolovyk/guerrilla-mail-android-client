package volovyk.guerrillamail.data.preferences.local

interface PreferencesDatasource {
    suspend fun setValue(key: String, value: String)
    suspend fun getValue(key: String): String?
}