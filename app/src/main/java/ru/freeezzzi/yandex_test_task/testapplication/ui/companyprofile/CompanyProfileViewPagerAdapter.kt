package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.StockCandle
import ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.newstab.NewsListAdapter
import ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.newstab.NewsTabViewHolder
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerViewHodler
import java.lang.IllegalArgumentException

// TODO добавить в candle и news свои scroll listener RecyclerView.OnScrollListener и SwipeRefreshLayout.OnRefreshListener
class CompanyProfileViewPagerAdapter(
    // Candle tab
    private val getCandleListener: (resolution: String, from: Long, to: Long) -> Unit,
    // news tab
    private val newsListAdapter: NewsListAdapter,
    private val getNewsListener: (from: String, to: String) -> Unit // TODO переделать под параметры поиска
) : RecyclerView.Adapter<ViewPagerViewHodler>() {
    private var candleTab: CandleViewHolder? = null
    private var newsTab: NewsTabViewHolder? = null

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
            VIEW_TYPE_FORECASTS -> ForecastsViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.forecasts_fragment, parent, false))
            else -> ForecastsViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.forecasts_fragment, parent, false))
        }

    override fun onBindViewHolder(holder: ViewPagerViewHodler, position: Int) {
        when (holder) {
            is CandleViewHolder -> holder.onBind(getCandleListener)
            is SummaryViewHolder -> holder.onBind()
            is NewsTabViewHolder -> holder.onBind(
                newsListAdapter,
                getNewsListener
            )
            is ForecastsViewHolder -> holder.onBind()
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> VIEW_TYPE_CHART
            1 -> VIEW_TYPE_SUMMARY
            2 -> VIEW_TYPE_NEWS
            3 -> VIEW_TYPE_FORECASTS
            else -> throw IllegalArgumentException("Wrong position")
        }

    override fun getItemCount(): Int = 4

    fun setCandleData(candle: StockCandle) {
        candleTab?.setCandleValues(candle)
    }

    fun newsSetRefreshing(state:Boolean) {
        newsTab?.setRefreshing(state)
    }

    companion object {
        const val VIEW_TYPE_CHART = 0
        const val VIEW_TYPE_SUMMARY = 1
        const val VIEW_TYPE_NEWS = 2
        const val VIEW_TYPE_FORECASTS = 3
    }
}
