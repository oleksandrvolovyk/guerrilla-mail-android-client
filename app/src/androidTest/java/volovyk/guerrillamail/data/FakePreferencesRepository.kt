package volovyk.guerrillamail.data

import volovyk.guerrillamail.data.preferences.PreferencesRepository

class FakePreferencesRepository(initialValues: Map<String, String> = emptyMap()) :
    PreferencesRepository {

    val values = initialValues.toMutableMap()

    override suspend fun setValue(key: String, value: String) {
        values[key] = value
    }

    override suspend fun getValue(key: String): String? = values[key]
}