package volovyk.guerrillamail.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;

import volovyk.guerrillamail.data.model.Email;

@Dao
public interface EmailDao {
    @Query("SELECT * FROM email")
    LiveData<List<Email>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Collection<Email> emails);

    @Delete
    void delete(Email email);
}
