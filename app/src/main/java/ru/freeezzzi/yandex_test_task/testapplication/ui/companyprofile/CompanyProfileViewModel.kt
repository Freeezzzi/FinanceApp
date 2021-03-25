package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.launch
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import ru.freeezzzi.yandex_test_task.testapplication.domain.OperationResult
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.News
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.StockCandle
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.toCompanyProfileEntity
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository
import ru.freeezzzi.yandex_test_task.testapplication.ui.ViewState
import javax.inject.Inject

class CompanyProfileViewModel @Inject constructor(
    private val router: Router,
    private val companiesRepository: CompaniesRepository,
    private val database: FavoriteCompaniesDatabase
) : ViewModel() {
    private var mutableStockCandle: MutableLiveData<ViewState<StockCandle, String?>> = MutableLiveData<ViewState<StockCandle, String?>>()
    val stockCandle: LiveData<ViewState<StockCandle, String?>> get() = mutableStockCandle

    private var mutableNewsList: MutableLiveData<ViewState<List<News>, String?>> = MutableLiveData<ViewState<List<News>, String?>>()
    val newsList: LiveData<ViewState<List<News>, String?>> get() = mutableNewsList

    var companyProfile: CompanyProfile? = null

    fun newsClickedAction(news: News) {
        // TODO
    }

    fun getNews(from: String, to: String) {
        viewModelScope.launch {
            mutableNewsList.value = ViewState.loading()
            when (val tickersResult = companiesRepository.getCompanyNews(companyProfile?.ticker ?: " ", from, to)) {
                is OperationResult.Success -> mutableNewsList.value = ViewState.success(tickersResult.data)
                is OperationResult.Error -> mutableNewsList.value = ViewState.error(emptyList(), tickersResult.data)
            }
        }
    }

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

    fun exitFragment() {
        router.exit()
    }

    companion object {
        const val ONE_DAY_RESOLUTION = "1"
        const val FIVE_DAYS_RESOLUTION = "5"
        const val FIFTEEN_DAYS_RESOLUTION = "15"
        const val THIRTY_DAYS_RESOLUTION = "30"
        const val SIXTY_DAYS_RESOLUTION = "60"
        const val DAY_RESOLUTION = "D"
        const val WEEK_RESOLUTION = "W"
        const val MONTH_RESOLUTION = "M"
    }
}
