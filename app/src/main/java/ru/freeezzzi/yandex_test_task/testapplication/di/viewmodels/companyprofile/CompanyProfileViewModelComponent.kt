package ru.freeezzzi.yandex_test_task.testapplication.di.viewmodels.companyprofile

import dagger.Component
import ru.freeezzzi.yandex_test_task.testapplication.di.AppComponent
import ru.freeezzzi.yandex_test_task.testapplication.di.scopes.AppScope
import ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.CompanyProfileViewModel

@AppScope
@Component(dependencies = [AppComponent::class])
interface CompanyProfileViewModelComponent {
    fun provideViewModel(): CompanyProfileViewModel
}
