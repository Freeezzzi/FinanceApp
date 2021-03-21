package ru.freeezzzi.yandex_test_task.testapplication.di.modules

import dagger.Binds
import dagger.Module
import ru.freeezzzi.yandex_test_task.testapplication.data.repositories.CompaniesRepositoryImpl
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository
import javax.inject.Singleton

@Module
internal abstract class DataModule {
    @Binds
    @Singleton
    abstract fun providesCompaniesRepository(companiesRepositoryImpl: CompaniesRepositoryImpl): CompaniesRepository

}