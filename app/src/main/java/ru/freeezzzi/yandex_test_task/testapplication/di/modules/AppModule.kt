package ru.freeezzzi.yandex_test_task.testapplication.di.modules

import android.content.Context
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import timber.log.Timber
import javax.inject.Singleton

@Module
internal class AppModule {
    private val cicerone: Cicerone<Router> = Cicerone.create()

    @Provides
    @Singleton
    fun provideRouter(): Router =
        cicerone.router

    @Provides
    @Singleton
    fun provideNavigatorHolder(): NavigatorHolder =
        cicerone.getNavigatorHolder()

    @Provides
    @Singleton
    fun provideTimberTree(): Timber.Tree =
        Timber.DebugTree()

    @InternalCoroutinesApi
    @Provides
    @Singleton
    fun provideDatabase(appContext: Context): FavoriteCompaniesDatabase =
        FavoriteCompaniesDatabase.getInstance(appContext)
}
