package volovyk.guerrillamail.data.local

import android.content.Context
import androidx.room.Room.databaseBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object LocalEmailDatabaseModule {
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): LocalEmailDatabase {
        return databaseBuilder(appContext, RoomEmailDatabase::class.java, "email-database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideEmailDao(localEmailDatabase: LocalEmailDatabase): EmailDao {
        return localEmailDatabase.getEmailDao()
    }
}