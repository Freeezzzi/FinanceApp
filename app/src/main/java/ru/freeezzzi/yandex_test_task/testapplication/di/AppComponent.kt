package ru.freeezzzi.yandex_test_task.testapplication.di

import android.content.Context
import com.github.terrakok.cicerone.Router
import dagger.BindsInstance
import dagger.Component
import ru.freeezzzi.yandex_test_task.testapplication.App
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import ru.freeezzzi.yandex_test_task.testapplication.data.local.dao.CompanyProfileDao
import ru.freeezzzi.yandex_test_task.testapplication.di.modules.AppModule
import ru.freeezzzi.yandex_test_task.testapplication.di.modules.DataModule
import ru.freeezzzi.yandex_test_task.testapplication.di.modules.NetworkModule
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository
import ru.freeezzzi.yandex_test_task.testapplication.ui.AppActivity
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataModule::class, NetworkModule::class])
interface AppComponent {
    fun provideRouter(): Router

    fun provideCompaniesRepository(): CompaniesRepository

    //fun provideCompanyProfileDao(): CompanyProfileDao

    fun provideFavoriteCompaniesDatabase(): FavoriteCompaniesDatabase

    fun inject(app: App)

    fun inject(appActivity: AppActivity)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun apiUrl(@Named(NetworkModule.BASE_URL) url: String): Builder

        @BindsInstance
        fun appContext(context: Context): Builder
    }
}
