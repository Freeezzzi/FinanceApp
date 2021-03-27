package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.RecommendationTrend
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.StockCandle
import ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.candletab.CandleViewHolder
import ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.forecaststab.ForecastsViewHolder
import ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.newstab.NewsListAdapter
import ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.newstab.NewsTabViewHolder
import ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist.QuotesListAdapter
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.AllTabViewHodler
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerViewHodler
import java.lang.IllegalArgumentException

class CompanyProfileViewPagerAdapter(
    // Candle tab
    private val getCandleListener: (resolution: String, from: Long, to: Long) -> Unit,
    private val getPrices: ()->Pair<String,String>,
    // news tab
    private val newsListAdapter: NewsListAdapter,
    private val getNewsListener: (from: String, to: String, clearList:Boolean) -> Unit,
    // Forecasts tab
    private val getRecommendationTrends: () -> Unit,
    // Peers tab
    private val peersListAdapter: QuotesListAdapter,
    private val peersRefreshListener: SwipeRefreshLayout.OnRefreshListener,
    private val peersScrollListener: RecyclerView.OnScrollListener,
) : RecyclerView.Adapter<ViewPagerViewHodler>() {
    private var candleTab: CandleViewHolder? = null
    private var newsTab: NewsTabViewHolder? = null
    private var peersTab: AllTabViewHodler? = null
    private var forecastsTab: ForecastsViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHodler =
        when (viewType) {
            VIEW_TYPE_CHART -> { candleTab = CandleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.candle_fragment, parent, false))
                candleTab as CandleViewHolder
            }
            VIEW_TYPE_SUMMARY -> SummaryViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.summary_fragment, parent, false))
            VIEW_TYPE_NEWS -> {
                newsTab = NewsTabViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.news_fragment, parent, false))
                newsTab as NewsTabViewHolder
            }
            VIEW_TYPE_FORECASTS -> {
                forecastsTab = ForecastsViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.forecasts_fragment, parent, false))
                forecastsTab as ForecastsViewHolder
            }
            VIEW_TYPE_PEERS -> {
                peersTab = AllTabViewHodler(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.quotes_fragment_alltab, parent, false))
                peersRefreshListener.onRefresh() // Вызовем чтобы отобразить компании и не загружать лишний раз
                peersTab as AllTabViewHodler
            }
            else -> ForecastsViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.forecasts_fragment, parent, false))
        }

    override fun onBindViewHolder(holder: ViewPagerViewHodler, position: Int) {
        when (holder) {
            is CandleViewHolder -> holder.onBind(
                getCandleListener,
                getPrices
            )
            is SummaryViewHolder -> holder.onBind()
            is NewsTabViewHolder -> holder.onBind(
                newsListAdapter,
                getNewsListener
            )
            is ForecastsViewHolder -> holder.onBind(
                getRecommendationTrends
            )
            is AllTabViewHodler -> {
                holder.onBind(
                    peersListAdapter,
                    peersRefreshListener,
                    peersScrollListener
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> VIEW_TYPE_CHART
            1 -> VIEW_TYPE_SUMMARY
            2 -> VIEW_TYPE_NEWS
            3 -> VIEW_TYPE_FORECASTS
            4 -> VIEW_TYPE_PEERS
            else -> throw IllegalArgumentException("Wrong position")
        }

    override fun getItemCount(): Int = 5

    fun setCandleData(candle: StockCandle) {
        candleTab?.setCandleValue(candle)
    }

    fun candleSetRefreshing(state: Boolean) {
        candleTab?.setRefreshing(state)
    }

    fun newsSetRefreshing(state: Boolean) {
        newsTab?.setRefreshing(state)
    }

    fun forecastsSetData(trends: List<RecommendationTrend>) {
        forecastsTab?.setValues(trends)
    }
    fun forecastsSetRefreshing(state: Boolean) {
        forecastsTab?.setRefreshing(state)
    }

    fun setPeersRefreshing(state: Boolean) {
        peersTab?.setRefreshing(state)
    }

    companion object {
        const val VIEW_TYPE_CHART = 0
        const val VIEW_TYPE_SUMMARY = 1
        const val VIEW_TYPE_NEWS = 2
        const val VIEW_TYPE_FORECASTS = 3
        const val VIEW_TYPE_PEERS = 4
    }
}
