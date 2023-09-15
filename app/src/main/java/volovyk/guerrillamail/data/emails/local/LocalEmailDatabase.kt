package volovyk.guerrillamail.data.emails.local

interface LocalEmailDatabase {
    fun getEmailDao(): EmailDao
}