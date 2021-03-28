package ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.launch
import ru.freeezzzi.yandex_test_task.testapplication.BuildConfig
import ru.freeezzzi.yandex_test_task.testapplication.Screens
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.toCompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.OperationResult
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository
import ru.freeezzzi.yandex_test_task.testapplication.ui.CompaniesViewModel
import ru.freeezzzi.yandex_test_task.testapplication.ui.ViewState
import javax.inject.Inject

class QuotesListViewModel @Inject constructor(
    private val router: Router,
    private val companiesRepository: CompaniesRepository,
    private val database: FavoriteCompaniesDatabase
) : CompaniesViewModel(companiesRepository, database) {

    /**
     * Список компаний, которые хранятся на устройстве
     */
    private val mutableLocalCompanies = MutableLiveData<ViewState<MutableList<CompanyProfile>, String?>>()
    val localCompanies: LiveData<ViewState<MutableList<CompanyProfile>, String?>> get() = mutableLocalCompanies

    fun searchAction() {
        router.navigateTo(Screens.searchFragment(), true)
    }

    /**
     * Добавляет
     */
    override fun addToFavorites(companyProfile: CompanyProfile) {
        viewModelScope.launch {
            //скопируем лист компаний на устройстве, чтобы потом его заново отправить в адаптер
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
                false -> {
                    companyProfile.isFavorite = true
                    companiesList.add(companyProfile)
                    database.companyProfileDao().insert(companyProfile.toCompanyProfileEntity())
                }
            }
            mutableLocalCompanies.value = ViewState.success(companiesList)

            //Обновим компанию в листе компаний с сервера
            super.setFavouriteFlagInList(companyProfile, companyProfile.isFavorite)
        }
    }

    fun itemOnClickAction(companyProfile: CompanyProfile) {
        router.navigateTo(Screens.companyProfileFragment(companyProfile), true)
    }

    fun getTickers() {
        viewModelScope.launch {
            mutableTickersList.value = ViewState.loading()
            when (val tickersResult = companiesRepository.getCompaniesTop500(BuildConfig.TICKERS_KEY)) {
                is OperationResult.Success -> {
                    numberOfCompanies = tickersResult.data.size
                    mutableTickersList.value = ViewState.success(tickersResult.data)
                }
                is OperationResult.Error -> mutableTickersList.value = ViewState.error(emptyList(), tickersResult.data)
            }
        }
    }

    /**
     * set to companyProfile quote new data or null if error occurred
     */
    private fun getQuote(companyProfile: CompanyProfile) {
        viewModelScope.launch {
            when (val quoteResult = companiesRepository.getCompanyQuote(companyProfile.ticker ?: "")) {
                is OperationResult.Success -> companyProfile.quote = quoteResult.data
                is OperationResult.Error -> companyProfile.quote = null
            }
        }
    }

    /**
     * Отображает все компании, добавленные в favorites
     */
    fun showFavourites() {
        viewModelScope.launch {
            mutableLocalCompanies.value = ViewState.loading()
            val localCompaniesQuery = database.companyProfileDao().getFavoriteCompanies().map {
                it.toCompanyProfile()
            }
            mutableLocalCompanies.value = ViewState.success(localCompaniesQuery.toMutableList())
        }
    }

    fun exitFragment() {
        router.exit()
    }
}
