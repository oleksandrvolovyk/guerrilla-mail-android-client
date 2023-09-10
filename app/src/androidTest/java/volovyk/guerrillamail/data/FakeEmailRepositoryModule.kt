package volovyk.guerrillamail.data

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [EmailRepositoryModule::class]
)
object FakeEmailRepositoryModule {
    @Provides
    @Singleton
    fun provideEmailRepository(): EmailRepository {
        return FakeEmailRepository()
    }
}
