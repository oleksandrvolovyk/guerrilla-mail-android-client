package volovyk.guerrillamail.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import volovyk.guerrillamail.data.model.Email;

@Database(entities = {Email.class}, version = 1)
public abstract class LocalEmailDatabase extends RoomDatabase {
    public abstract EmailDao emailDao();

    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseExecutorService =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
}
