package ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.launch
import ru.freeezzzi.yandex_test_task.testapplication.BuildConfig
import ru.freeezzzi.yandex_test_task.testapplication.Screens
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.toCompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.OperationResult
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.toCompanyProfileEntity
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository
import ru.freeezzzi.yandex_test_task.testapplication.ui.ViewState
import javax.inject.Inject

class QuotesListViewModel @Inject constructor(
    private val router: Router,
    private val companiesRepository: CompaniesRepository,
    val database: FavoriteCompaniesDatabase
) : ViewModel() {
    private val mutableCompanies = MutableLiveData<ViewState<MutableList<CompanyProfile>, String?>>()

    val companies: LiveData<ViewState<MutableList<CompanyProfile>, String?>> get() = mutableCompanies

    private val mutableLocalCompanies = MutableLiveData<ViewState<MutableList<CompanyProfile>, String?>>()

    val localCompanies: LiveData<ViewState<MutableList<CompanyProfile>, String?>> get() = mutableLocalCompanies

    private var mutableTickersList: MutableLiveData<ViewState<List<String>, String?>> = MutableLiveData<ViewState<List<String>, String?>>()
    val tickersList: LiveData<ViewState<List<String>, String?>> get() = mutableTickersList

    private var companiesCount = 0

    fun searchAction(){
        router.navigateTo(Screens.searchFragment(), true)
    }

    fun addToFavorites(companyProfile: CompanyProfile) {
        viewModelScope.launch {
            when (companyProfile.isFavorite) {
                true -> { // Нужно удалить
                    database.companyProfileDao().delete(companyProfile.toCompanyProfileEntity())
                    companyProfile.isFavorite = false
                }
                false -> {
                    companyProfile.isFavorite = true
                    database.companyProfileDao().insert(companyProfile.toCompanyProfileEntity())
                }
            }
            showFavourites()
        }
    }

    fun itemOnClickAction(companyProfile: CompanyProfile) {
        // TODO 
    }

    fun getTickers() {
        viewModelScope.launch {
            mutableTickersList.value = ViewState.loading()
            when (val tickersResult = companiesRepository.getCompaniesTop500(BuildConfig.TICKERS_KEY)) {
                is OperationResult.Success -> mutableTickersList.value = ViewState.success(tickersResult.data)
                is OperationResult.Error -> mutableTickersList.value = ViewState.error(emptyList(), tickersResult.data)
            }
        }
    }

    fun getCompanies(howManyCompanies: Int) {
        if (companiesCount + howManyCompanies > 499) return
        if (mutableCompanies.value is ViewState.Loading && companiesCount != 0) return

        viewModelScope.launch {
            // Проверим есть ли уже в сптске данные
            var companiesList: MutableList<CompanyProfile> = mutableListOf<CompanyProfile>()
            when (mutableCompanies.value) {
                is ViewState.Success -> companiesList = (mutableCompanies.value as ViewState.Success<MutableList<CompanyProfile>>).result
                is ViewState.Error -> companiesList = (mutableCompanies.value as ViewState.Error<MutableList<CompanyProfile>, String?>).oldvalue
            }
            // Здесь приходится копировать лист, т.к. если ссылка останется той же то submitList адаптера посчитает что они одинаковые и не обновит RecyclerView
            companiesList = companiesList.toMutableList()

            var state: ViewState<MutableList<CompanyProfile>, String?> = ViewState.loading() // Сюда будем записывать временные значения, пока не загрузим все значения
            mutableCompanies.value = ViewState.loading() // Сообщаем что идет загрузка
            when (tickersList.value) {
                is ViewState.Success -> {
                    // Для каждого запросим компанию и для нее quote
                    for (ticker in
                    (tickersList.value as ViewState.Success<List<String>>).result.subList(companiesCount, companiesCount + howManyCompanies)) {
                        when (val companiesResult = companiesRepository.getCompanyProfile(ticker)) {
                            is OperationResult.Success -> { // Если удачно, то добавляем компанию в
                                val companyProfile = companiesResult.data
                                if (companyProfile.ticker == null) { // Если с сервера пришел невалидный ответ, то пропускаем эту компанию
                                    continue
                                }

                                // getQuote(companyProfile)
                                when (val quoteResult = companiesRepository.getCompanyQuote(companyProfile.ticker ?: "")) {
                                    is OperationResult.Success -> companyProfile.quote = quoteResult.data
                                    is OperationResult.Error -> companyProfile.quote = null
                                }

                                companiesList.add(companyProfile)
                                state = ViewState.success(companiesList)
                                companiesCount++
                            }
                            is OperationResult.Error -> state = ViewState.Error(companiesList, companiesResult.data)
                        }
                    }
                    mutableCompanies.value = state
                }
                is ViewState.Error -> mutableCompanies.value = ViewState.error(companiesList, (tickersList.value as ViewState.Error<List<String>, String?>).result)
                is ViewState.Loading -> mutableCompanies.value = ViewState.error(companiesList, "Tickers loading.Try again!")
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

    fun clearCompaniesList() {
        companiesCount = 0
        mutableCompanies.value = ViewState.loading()
    }

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
