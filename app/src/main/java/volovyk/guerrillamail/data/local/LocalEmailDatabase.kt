package volovyk.guerrillamail.data.local

interface LocalEmailDatabase {
    fun getEmailDao(): EmailDao
}