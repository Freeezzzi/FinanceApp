package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.forecaststab

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.RecommendationTrend
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerViewHodler

// TODO Добавить функционал выбора периода
class ForecastsViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val pieChart: PieChart = itemView.findViewById(R.id.forecasts_piechart)
    private val refreshLayout: SwipeRefreshLayout? = itemView.findViewById(R.id.forecasts_swiperefreshlayout)

    fun onBind(
        getRecommendationTrends: () -> Unit
    ) {
        refreshLayout?.setOnRefreshListener { getRecommendationTrends() }
        getRecommendationTrends.invoke()
    }

    fun setValues(recommendationTrend: List<RecommendationTrend>) {
        if (recommendationTrend.size != 0) {
            setValue(recommendationTrend.get(0))
        }
    }

    private fun setValue(recommendationTrend: RecommendationTrend) {
        val pieEntries = mutableListOf<PieEntry>()
        val label = recommendationTrend.symbol ?: "" + " " + itemView.context.getString(R.string.recommendation_trend)

        // инициализируем данные
        pieEntries.add(PieEntry(recommendationTrend.strongBuy?.toFloat() ?: 0F, "Strong buy"))
        pieEntries.add(PieEntry(recommendationTrend.buy?.toFloat() ?: 0F, "Buy"))
        pieEntries.add(PieEntry(recommendationTrend.hold?.toFloat() ?: 0F, "Hold"))
        pieEntries.add(PieEntry(recommendationTrend.sell?.toFloat() ?: 0F, "Sell"))
        pieEntries.add(PieEntry(recommendationTrend.strongSell?.toFloat() ?: 0F, "Strong sell"))

        val colors = mutableListOf<Int>()
        val colorsArray = itemView.context.resources.getIntArray(R.array.pie_chart_colors).map { it }
/*        colors.add(ContextCompat.getColor(itemView.context, R.color.dark_green))
        colors.add(ContextCompat.getColor(itemView.context, R.color.green))
        colors.add(ContextCompat.getColor(itemView.context, R.color.very_dark_yellow))
        colors.add(ContextCompat.getColor(itemView.context, R.color.red))
        colors.add(ContextCompat.getColor(itemView.context, R.color.dark_red))*/

        val pieDataSet = PieDataSet(pieEntries, label)
        pieDataSet.valueTextSize = 12f // text size of values
        pieDataSet.setColors(colorsArray)
        val pieData = PieData(pieDataSet)

        pieData.setDrawValues(true)
        pieChart.data = pieData
        pieChart.invalidate()
        pieChart.visibility = View.VISIBLE
    }

    fun setRefreshing(state: Boolean) {
        refreshLayout?.isRefreshing = state
    }
}
