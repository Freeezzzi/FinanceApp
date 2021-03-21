package ru.freeezzzi.yandex_test_task.testapplication.di.viewmodels.quoteslist

import dagger.Component
import ru.freeezzzi.yandex_test_task.testapplication.di.AppComponent
import ru.freeezzzi.yandex_test_task.testapplication.di.scopes.AppScope
import ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist.QuotesListViewModel

@AppScope
@Component(dependencies = [AppComponent::class])
interface QuotesListViewModelComponent {
    fun provideViewModel() : QuotesListViewModel
}