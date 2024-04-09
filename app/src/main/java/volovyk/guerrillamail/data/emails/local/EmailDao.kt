package volovyk.guerrillamail.data.emails.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import volovyk.guerrillamail.data.emails.model.Email

@Dao
interface EmailDao {
    @get:Query("SELECT * FROM email")
    val all: Flow<List<Email>>

    @Query("SELECT * FROM email WHERE id = :emailId")
    fun getById(emailId: String): Email?

    @Query("UPDATE email SET viewed = :viewed WHERE id = :emailId")
    fun setEmailViewed(emailId: String, viewed: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(emails: Collection<Email>)

    @Query("DELETE FROM email WHERE id in (:emailIds)")
    fun delete(emailIds: List<String>)
}