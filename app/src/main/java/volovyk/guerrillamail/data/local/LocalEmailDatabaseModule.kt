package volovyk.guerrillamail.data.local;

import android.content.Context;

import androidx.room.Room;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class LocalEmailDatabaseModule {
    @Provides
    public LocalEmailDatabase provideDatabase(@ApplicationContext Context appContext) {
        return Room.databaseBuilder(appContext, LocalEmailDatabase.class, "email-database")
                .build();
    }

    @Provides
    public EmailDao provideEmailDao(LocalEmailDatabase localEmailDatabase) {
        return  localEmailDatabase.emailDao();
    }
}
