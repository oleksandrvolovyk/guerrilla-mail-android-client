package volovyk.guerrillamail.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import volovyk.guerrillamail.data.model.Email
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Email::class], version = 2)
abstract class RoomEmailDatabase : RoomDatabase(), LocalEmailDatabase {
    abstract override fun getEmailDao(): EmailDao

    companion object {
        private const val NUMBER_OF_THREADS = 4
        val databaseExecutorService: ExecutorService =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS)
    }
}