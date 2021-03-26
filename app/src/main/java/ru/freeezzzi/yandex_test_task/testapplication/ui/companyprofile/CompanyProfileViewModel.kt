package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.launch
import ru.freeezzzi.yandex_test_task.testapplication.Screens
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import ru.freeezzzi.yandex_test_task.testapplication.domain.OperationResult
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.*
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository
import ru.freeezzzi.yandex_test_task.testapplication.ui.ViewState
import javax.inject.Inject

class CompanyProfileViewModel @Inject constructor(
    private val router: Router,
    private val companiesRepository: CompaniesRepository,
    private val database: FavoriteCompaniesDatabase
) : ViewModel() {
    /**
     * STOCK CANDLE
     */
    private var mutableStockCandle: MutableLiveData<ViewState<StockCandle, String?>> = MutableLiveData<ViewState<StockCandle, String?>>()
    val stockCandle: LiveData<ViewState<StockCandle, String?>> get() = mutableStockCandle

    /**
     * NEWS LISTS
     */
    private var mutableNewsList: MutableLiveData<ViewState<List<News>, String?>> = MutableLiveData<ViewState<List<News>, String?>>()
    val newsList: LiveData<ViewState<List<News>, String?>> get() = mutableNewsList

    /**
     * FORECASTS LIST
     */
    private var mutableRecommendationTrends: MutableLiveData<ViewState<List<RecommendationTrend>, String?>> = MutableLiveData<ViewState<List<RecommendationTrend>, String?>>()
    val recommendationTrend: LiveData<ViewState<List<RecommendationTrend>, String?>> get() = mutableRecommendationTrends

    /**
     * PEERS LISTS
     */
    private var mutablePeersList: MutableLiveData<ViewState<List<CompanyProfile>, String?>> = MutableLiveData<ViewState<List<CompanyProfile>, String?>>()
    val peersList: LiveData<ViewState<List<CompanyProfile>, String?>> get() = mutablePeersList

    private var mutablePeersTickersList: MutableLiveData<ViewState<List<String>, String?>> = MutableLiveData<ViewState<List<String>, String?>>()
    val peerstickersList: LiveData<ViewState<List<String>, String?>> get() = mutablePeersTickersList

    private var tickersCount = 0 // сколько уже загрузили(некоторые могут быть не валдины и не отображаться)
    private var numberOfCompanies = 0 // сколько компаний получили с сервера

    /**
     * CURRENT PROFILE
     */
    var companyProfile: CompanyProfile? = null

    /**
     * CURRENT PROFILE ADD TO FAVORITES
     */
    fun addToFavorites() {
        viewModelScope.launch {
            when (companyProfile!!.isFavorite) {
                true -> { // Нужно удалить
                    database.companyProfileDao().delete(companyProfile!!.toCompanyProfileEntity())
                    companyProfile!!.isFavorite = false
                }
                false -> {
                    companyProfile!!.isFavorite = true
                    database.companyProfileDao().insert(companyProfile!!.toCompanyProfileEntity())
                }
            }
        }
    }

    /**
     * CANDLE CHART TAB
     */
    fun getStockCandle(
        resolution: String,
        from: Long,
        to: Long
    ) {
        viewModelScope.launch {
            mutableStockCandle.value = ViewState.loading()
            when (val tickersResult = companiesRepository.getStockCandle(companyProfile?.ticker ?: "", resolution, from, to)) {
                is OperationResult.Success -> mutableStockCandle.value = ViewState.success(tickersResult.data)
                is OperationResult.Error -> mutableStockCandle.value = ViewState.error(StockCandle(), tickersResult.data)
            }
        }
    }

    /**
     * NEWS TAB
     */
    fun getNews(from: String, to: String) {
        viewModelScope.launch {
            var newsList: MutableList<News> = mutableListOf<News>()
            when (mutableNewsList.value) {
                is ViewState.Success -> newsList = (mutableNewsList.value as ViewState.Success<MutableList<News>>).result
                is ViewState.Error -> newsList = (mutableNewsList.value as ViewState.Error<MutableList<News>, String?>).oldvalue
            }
            // Здесь приходится копировать лист, т.к. если ссылка останется той же то submitList адаптера посчитает что они одинаковые и не обновит RecyclerView
            newsList = newsList.toMutableList()

            mutableNewsList.value = ViewState.loading()
            when (val tickersResult = companiesRepository.getCompanyNews(companyProfile?.ticker ?: " ", from, to)) {
                is OperationResult.Success -> { // Если успешно то добавтим новые новости к старым
                    newsList.addAll(tickersResult.data)
                    mutableNewsList.value = ViewState.success(newsList)
                }
                is OperationResult.Error -> mutableNewsList.value = ViewState.error(newsList, tickersResult.data)
            }
        }
    }

    /**
     * FORECASTS TAB
     */
    fun getRecommendationTrends(){
        viewModelScope.launch {
            mutableRecommendationTrends.value = ViewState.loading()
            when (val trends = companiesRepository.getRecommendationTrends(companyProfile?.ticker ?: "")) {
                is OperationResult.Success -> mutableRecommendationTrends.value = ViewState.success(trends.data)
                is OperationResult.Error -> mutableRecommendationTrends.value = ViewState.error(emptyList(), trends.data)
            }
        }
    }

    /**
     * PEERS TAB
     */
    fun addPeerToFavorites(company: CompanyProfile) {
        viewModelScope.launch {
            when (company.isFavorite) {
                true -> { // Нужно удалить
                    database.companyProfileDao().delete(company.toCompanyProfileEntity())
                    company.isFavorite = false
                }
                false -> {
                    company.isFavorite = true
                    database.companyProfileDao().insert(company.toCompanyProfileEntity())
                }
            }
        }
    }

    fun peerOnClickAction(company: CompanyProfile) {
        router.navigateTo(Screens.companyProfileFragment(company), true)
    }

    fun findPeers() {
        viewModelScope.launch {
            mutablePeersTickersList.value = ViewState.loading()
            when (val tickersResult = companiesRepository.getCompanyPeers(companyProfile?.ticker ?: " ")) {
                is OperationResult.Success -> {
                    numberOfCompanies = tickersResult.data.size
                    mutablePeersTickersList.value = ViewState.success(tickersResult.data)
                }
                is OperationResult.Error -> mutablePeersTickersList.value = ViewState.error(emptyList(), tickersResult.data)
            }
        }
    }

    fun getCompanies(howManyCompanies: Int) {
        var lastCompanyToDownload = tickersCount + howManyCompanies
        if (numberOfCompanies == 0) mutablePeersList.value = ViewState.success(mutableListOf()) // Если найдено 0 компаний, то удалим старые данные
        if (tickersCount == numberOfCompanies) return // если загрузили все компании
        if (lastCompanyToDownload > numberOfCompanies) { // если хотим загрузить больше компаний чем осталось
            lastCompanyToDownload = numberOfCompanies
        }
        if (mutablePeersList.value is ViewState.Loading && tickersCount != 0) return

        viewModelScope.launch {
            // Проверим есть ли уже в сптске данные
            var companiesList: MutableList<CompanyProfile> = mutableListOf<CompanyProfile>()
            when (mutablePeersList.value) {
                is ViewState.Success -> companiesList = (mutablePeersList.value as ViewState.Success<MutableList<CompanyProfile>>).result
                is ViewState.Error -> companiesList = (mutablePeersList.value as ViewState.Error<MutableList<CompanyProfile>, String?>).oldvalue
            }
            // Здесь приходится копировать лист, т.к. если ссылка останется той же то submitList адаптера посчитает что они одинаковые и не обновит RecyclerView
            companiesList = companiesList.toMutableList()

            var state: ViewState<MutableList<CompanyProfile>, String?> = ViewState.loading() // Сюда будем записывать временные значения, пока не загрузим все значения
            mutablePeersList.value = ViewState.loading() // Сообщаем что идет загрузка
            when (mutablePeersTickersList.value) {
                is ViewState.Success -> {
                    // Для каждого запросим компанию и для нее quote
                    for (ticker in
                    (mutablePeersTickersList.value as ViewState.Success<List<String>>).result.subList(tickersCount, lastCompanyToDownload)) {
                        when (val companiesResult = companiesRepository.getCompanyProfile(ticker)) {
                            is OperationResult.Success -> { // Если удачно, то добавляем компанию в
                                val companyProfile = companiesResult.data
                                if (companyProfile.ticker == null) { // Если с сервера пришел невалидный ответ, то пропускаем эту компанию
                                    tickersCount++
                                    state = ViewState.success(companiesList) // если у нас все компании не валдины, то нужно сообщить что мы все успешно обработали
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
                    mutablePeersList.value = state
                }
                is ViewState.Error -> mutablePeersList.value = ViewState.error(companiesList, (mutablePeersList.value as ViewState.Error<List<String>, String?>).result)
                is ViewState.Loading -> mutablePeersList.value = ViewState.error(companiesList, "Tickers loading.Try again!")
            }
        }
    }

    fun exitFragment() {
        router.exit()
    }
}
