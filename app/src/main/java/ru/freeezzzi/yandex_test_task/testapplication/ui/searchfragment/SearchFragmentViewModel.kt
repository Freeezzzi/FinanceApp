package ru.freeezzzi.yandex_test_task.testapplication.ui.searchfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.launch
import ru.freeezzzi.yandex_test_task.testapplication.Screens
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.RecentQueryEntity
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.toCompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.OperationResult
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.toCompanyProfileEntity
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository
import ru.freeezzzi.yandex_test_task.testapplication.ui.CompaniesViewModel
import ru.freeezzzi.yandex_test_task.testapplication.ui.SingleLiveEvent
import ru.freeezzzi.yandex_test_task.testapplication.ui.ViewState
import javax.inject.Inject

class SearchFragmentViewModel @Inject constructor(
    private val router: Router,
    private val database: FavoriteCompaniesDatabase,
    private val companiesRepository: CompaniesRepository
) : CompaniesViewModel(companiesRepository, database) {
    /**
     * Список компаний, которые удоавлетворяют запросу и хранятся на устройстве
     */
    private val mutableLocalCompanies = MutableLiveData<ViewState<MutableList<CompanyProfile>, String?>>()
    val localCompanies: LiveData<ViewState<MutableList<CompanyProfile>, String?>> get() = mutableLocalCompanies

    /**
     * Список прошлых запросов. Хранится на устройстве
     */
    private var mutableQueriesList: MutableLiveData<ViewState<List<String>, String?>> = MutableLiveData<ViewState<List<String>, String?>>()
    val queriesList: LiveData<ViewState<List<String>, String?>> get() = mutableQueriesList

    override fun addToFavorites(companyProfile: CompanyProfile) {
        viewModelScope.launch {
            //Добавим в список компаний новую или удалим
            var companiesList: MutableList<CompanyProfile> = mutableListOf()
            when (mutableLocalCompanies.value) {
                is ViewState.Success -> companiesList = (mutableLocalCompanies.value as ViewState.Success<MutableList<CompanyProfile>>).result
                is ViewState.Error -> companiesList = (mutableLocalCompanies.value as ViewState.Error<MutableList<CompanyProfile>, String?>).oldvalue
            }
            // Здесь приходится копировать лист, т.к. если ссылка останется той же то submitList адаптера посчитает что они одинаковые и не обновит RecyclerView
            companiesList = companiesList.toMutableList()
            when (companyProfile.isFavorite) {
                true -> { // Нужно удалить
                    database.companyProfileDao().delete(companyProfile.toCompanyProfileEntity())
                    companiesList.remove(companyProfile)
                    companyProfile.isFavorite = false
                }
                false -> { //Нужно добавить
                    companyProfile.isFavorite = true
                    companiesList.add(companyProfile)
                    database.companyProfileDao().insert(companyProfile.toCompanyProfileEntity())
                }
            }
            mutableLocalCompanies.value = ViewState.success(companiesList)
        }
    }

    fun searchAction(symbol: String) {
        if (symbol == "") return
        viewModelScope.launch {
            mutableTickersList.value = ViewState.loading()
            when (val tickersResult = companiesRepository.symbolLookup(symbol)) {
                is OperationResult.Success -> {
                    numberOfCompanies = tickersResult.data.size
                    mutableTickersList.value = ViewState.success(tickersResult.data)
                }
                is OperationResult.Error -> mutableTickersList.value = ViewState.error(emptyList(), tickersResult.data)
            }
        }
    }

    fun itemOnClickAction(companyProfile: CompanyProfile) {
        router.navigateTo(Screens.companyProfileFragment(companyProfile), true)
    }

    fun searchInFavourites(symbol: String) {
        viewModelScope.launch {
            mutableLocalCompanies.value = ViewState.loading()
            val localCompaniesQuery = database.companyProfileDao().findInFavouritesCompanies("%$symbol%").map {
                it.toCompanyProfile()
            }
            mutableLocalCompanies.value = ViewState.success(localCompaniesQuery.toMutableList())
        }
    }

    fun getRecentQueries() {
        viewModelScope.launch {
            mutableQueriesList.value = ViewState.loading()
            val recentQueries = database.companyProfileDao().getQueries().map {
                it.query
            }
            mutableQueriesList.value = ViewState.success(recentQueries)
        }
    }

    fun saveToRecentQueries(string: String) {
        viewModelScope.launch {
            database.companyProfileDao().insert(RecentQueryEntity(string))
            getRecentQueries()
        }
    }

    fun exitFragment() {
        router.exit()
    }
}
