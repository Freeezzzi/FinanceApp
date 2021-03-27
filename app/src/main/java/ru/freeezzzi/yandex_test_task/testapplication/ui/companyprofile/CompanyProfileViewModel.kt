package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.launch
import ru.freeezzzi.yandex_test_task.testapplication.Screens
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import ru.freeezzzi.yandex_test_task.testapplication.domain.OperationResult
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.*
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository
import ru.freeezzzi.yandex_test_task.testapplication.ui.CompaniesViewModel
import ru.freeezzzi.yandex_test_task.testapplication.ui.ViewState
import javax.inject.Inject

class CompanyProfileViewModel @Inject constructor(
    private val router: Router,
    private val companiesRepository: CompaniesRepository,
    private val database: FavoriteCompaniesDatabase
) : CompaniesViewModel(companiesRepository, database) {
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
    fun getNews(from: String, to: String, clearList: Boolean) {
        viewModelScope.launch {
            var newsList: MutableList<News> = mutableListOf()
            if (!clearList) { // если лист не нужно очистить то сохраним старые значения
                when (mutableNewsList.value) {
                    is ViewState.Success -> newsList = (mutableNewsList.value as ViewState.Success<MutableList<News>>).result
                    is ViewState.Error -> newsList = (mutableNewsList.value as ViewState.Error<MutableList<News>, String?>).oldvalue
                }
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
    fun getRecommendationTrends() {
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
    fun peerOnClickAction(company: CompanyProfile) {
        router.navigateTo(Screens.companyProfileFragment(company), true)
    }

    fun findPeers() {
        viewModelScope.launch {
            mutableTickersList.value = ViewState.loading()
            when (val tickersResult = companiesRepository.getCompanyPeers(companyProfile?.ticker ?: " ")) {
                is OperationResult.Success -> {
                    numberOfCompanies = tickersResult.data.size
                    mutableTickersList.value = ViewState.success(tickersResult.data)
                }
                is OperationResult.Error -> mutableTickersList.value = ViewState.error(emptyList(), tickersResult.data)
            }
        }
    }

    fun exitFragment() {
        router.exit()
    }
}
