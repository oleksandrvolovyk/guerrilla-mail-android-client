package volovyk.guerrillamail.data.emails.local

import androidx.room.Database
import androidx.room.RoomDatabase
import volovyk.guerrillamail.data.emails.model.Email

@Database(entities = [Email::class], version = 5)
abstract class RoomEmailDatabase : RoomDatabase(), LocalEmailDatabase {
    abstract override fun getEmailDao(): EmailDao
}