package volovyk.guerrillamail.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import volovyk.guerrillamail.data.model.Email

@Dao
interface EmailDao {
    @get:Query("SELECT * FROM email")
    val all: LiveData<List<Email?>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(emails: Collection<Email?>?)

    @Delete
    fun delete(email: Email?)
}