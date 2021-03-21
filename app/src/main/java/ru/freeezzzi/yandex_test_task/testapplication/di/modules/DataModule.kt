package ru.freeezzzi.yandex_test_task.testapplication.di.modules

import dagger.Binds
import dagger.Module
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import ru.freeezzzi.yandex_test_task.testapplication.data.local.dao.CompanyProfileDao
import ru.freeezzzi.yandex_test_task.testapplication.data.repositories.CompaniesRepositoryImpl
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository
import javax.inject.Singleton

@Module
internal abstract class DataModule {
    @Binds
    @Singleton
    abstract fun providesCompaniesRepository(companiesRepositoryImpl: CompaniesRepositoryImpl): CompaniesRepository

    /*@Binds
    @Singleton
    abstract fun providesCompanyProfileDao(database: FavoriteCompaniesDatabase): CompanyProfileDao //= database.companyProfileDao()*/
}
