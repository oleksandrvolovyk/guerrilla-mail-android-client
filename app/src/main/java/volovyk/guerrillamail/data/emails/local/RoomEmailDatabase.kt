package volovyk.guerrillamail.data.emails.local

import androidx.room.Database
import androidx.room.RoomDatabase
import volovyk.guerrillamail.data.emails.model.Email

@Database(entities = [Email::class], version = 6)
abstract class RoomEmailDatabase : RoomDatabase(), LocalEmailDatabase {
    abstract override fun getEmailDao(): EmailDao
}