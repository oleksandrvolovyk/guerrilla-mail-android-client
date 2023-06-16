package volovyk.guerrillamail.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import volovyk.guerrillamail.data.model.Email

@Database(entities = [Email::class], version = 2)
abstract class RoomEmailDatabase : RoomDatabase(), LocalEmailDatabase {
    abstract override fun getEmailDao(): EmailDao
}