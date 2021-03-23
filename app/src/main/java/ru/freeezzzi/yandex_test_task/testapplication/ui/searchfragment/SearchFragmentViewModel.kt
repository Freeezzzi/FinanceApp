package ru.freeezzzi.yandex_test_task.testapplication.ui.searchfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository
import ru.freeezzzi.yandex_test_task.testapplication.ui.ViewState
import javax.inject.Inject

class SearchFragmentViewModel @Inject constructor(
        private val router: Router,
        private val database: FavoriteCompaniesDatabase,
        private val companiesRepository: CompaniesRepository
) : ViewModel() {
    private val mutableCompanies = MutableLiveData<ViewState<MutableList<CompanyProfile>, String?>>()

    val companies: LiveData<ViewState<MutableList<CompanyProfile>, String?>> get() = mutableCompanies


    fun exitFragment() {
        router.exit()
    }
}