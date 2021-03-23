package ru.freeezzzi.yandex_test_task.testapplication.di.viewmodels.searchfragment

import dagger.Component
import ru.freeezzzi.yandex_test_task.testapplication.di.AppComponent
import ru.freeezzzi.yandex_test_task.testapplication.di.scopes.AppScope
import ru.freeezzzi.yandex_test_task.testapplication.ui.searchfragment.SearchFragmentViewModel

@AppScope
@Component(dependencies = [AppComponent::class])
interface SearchFragmentViewModelComponent {
    fun provideViewModel() : SearchFragmentViewModel
}