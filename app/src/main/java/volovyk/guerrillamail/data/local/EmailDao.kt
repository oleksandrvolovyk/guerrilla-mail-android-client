package volovyk.guerrillamail.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import volovyk.guerrillamail.data.model.Email

@Dao
interface EmailDao {
    @get:Query("SELECT * FROM email")
    val all: Flow<List<Email>>

    @Query("SELECT * FROM email WHERE id = :emailId")
    fun getById(emailId: Int): Email?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(emails: Collection<Email?>?)

    @Delete
    fun delete(email: Email?)
}