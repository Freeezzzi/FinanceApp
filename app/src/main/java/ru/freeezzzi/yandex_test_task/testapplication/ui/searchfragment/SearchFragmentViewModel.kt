package ru.freeezzzi.yandex_test_task.testapplication.ui.searchfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.launch
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.RecentQueryEntity
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.toCompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.OperationResult
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.toCompanyProfileEntity
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

    private val mutableLocalCompanies = MutableLiveData<ViewState<MutableList<CompanyProfile>, String?>>()

    val localCompanies: LiveData<ViewState<MutableList<CompanyProfile>, String?>> get() = mutableLocalCompanies

    private var mutableTickersList: MutableLiveData<ViewState<List<String>, String?>> = MutableLiveData<ViewState<List<String>, String?>>()
    val tickersList: LiveData<ViewState<List<String>, String?>> get() = mutableTickersList

    private var mutableQueriesList: MutableLiveData<ViewState<List<String>, String?>> = MutableLiveData<ViewState<List<String>, String?>>()
    val queriesList: LiveData<ViewState<List<String>, String?>> get() = mutableQueriesList

    private var tickersCount = 0

    private var numberOfCompanies = 0

    fun searchAction(symbol: String) {
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
            // TODO обновлять список
        }
    }

    fun itemOnClickAction(companyProfile: CompanyProfile) {
        // TODO
    }

    fun getCompanies(howManyCompanies: Int) {
        var lastCompanyToDownload = tickersCount + howManyCompanies
        if (tickersCount == numberOfCompanies) return // если загрузили все компании
        if (lastCompanyToDownload > numberOfCompanies) {
            lastCompanyToDownload = numberOfCompanies
        }
        if (mutableCompanies.value is ViewState.Loading && tickersCount != 0) return

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
                    (tickersList.value as ViewState.Success<List<String>>).result.subList(tickersCount, lastCompanyToDownload)) {
                        when (val companiesResult = companiesRepository.getCompanyProfile(ticker)) {
                            is OperationResult.Success -> { // Если удачно, то добавляем компанию в
                                val companyProfile = companiesResult.data
                                if (companyProfile.ticker == null) { // Если с сервера пришел невалидный ответ, то пропускаем эту компанию
                                    tickersCount++
                                    continue
                                }

                                // getQuote(companyProfile)
                                when (val quoteResult = companiesRepository.getCompanyQuote(companyProfile.ticker ?: "")) {
                                    is OperationResult.Success -> companyProfile.quote = quoteResult.data
                                    is OperationResult.Error -> companyProfile.quote = null
                                }

                                if (database.companyProfileDao().isCompanyInFavorite(companyProfile.ticker)) {
                                    companyProfile.isFavorite = true
                                    database.companyProfileDao().update(companyProfile.toCompanyProfileEntity())
                                }

                                companiesList.add(companyProfile)
                                state = ViewState.success(companiesList)
                                tickersCount++
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

    fun clearCompaniesList() {
        tickersCount = 0
        mutableCompanies.value = ViewState.loading()
    }

    fun showFavourites(symbol: String) {
        viewModelScope.launch {
            mutableLocalCompanies.value = ViewState.loading()
            val localCompaniesQuery = database.companyProfileDao().findInFavouritesCompanies(symbol).map {
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
            database.companyProfileDao().insert(RecentQueryEntity(string, null))
        }
    }

    fun exitFragment() {
        router.exit()
    }
}
