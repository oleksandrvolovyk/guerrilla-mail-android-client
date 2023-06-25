package volovyk.guerrillamail.data.local

import android.content.Context
import androidx.room.Room.databaseBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object LocalEmailDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): LocalEmailDatabase {
        return databaseBuilder(appContext, RoomEmailDatabase::class.java, "email-database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideEmailDao(localEmailDatabase: LocalEmailDatabase): EmailDao {
        return localEmailDatabase.getEmailDao()
    }
}