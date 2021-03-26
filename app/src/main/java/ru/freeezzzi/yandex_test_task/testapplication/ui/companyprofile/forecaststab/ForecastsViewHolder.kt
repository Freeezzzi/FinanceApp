package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.forecaststab

import android.graphics.Typeface
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.RecommendationTrend
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerViewHodler

// TODO Добавить функционал выбора периода
class ForecastsViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val barChart: BarChart = itemView.findViewById(R.id.forecasts_barchart)
    private val refreshLayout: SwipeRefreshLayout? = itemView.findViewById(R.id.forecasts_swiperefreshlayout)

    fun onBind(
        getRecommendationTrends: () -> Unit
    ) {
        refreshLayout?.setOnRefreshListener { getRecommendationTrends() }
        getRecommendationTrends.invoke()
    }

    fun setValues(recommendationTrends: List<RecommendationTrend>) {
        if (recommendationTrends.isEmpty()) return

        val barEntries = mutableListOf<BarEntry>() // Для каждого измерения
        val dateEntries = mutableListOf<String>() // Даты для каждого столбца
        val label = recommendationTrends.get(0).symbol ?: "" + " " + itemView.context.getString(R.string.recommendation_trend)

        barEntries.add(BarEntry(
            0F,
            floatArrayOf(
                recommendationTrends.get(0).strongSell?.toFloat() ?: 0F,
                recommendationTrends.get(0).sell?.toFloat() ?: 0F,
                recommendationTrends.get(0).hold?.toFloat() ?: 0F,
                recommendationTrends.get(0).buy?.toFloat() ?: 0F,
                recommendationTrends.get(0).strongBuy?.toFloat() ?: 0F,
            ),
            recommendationTrends.get(0).period ?: ""
        ))
        /*recommendationTrends.forEachIndexed { index, recommendationTrend ->
            if (index <= 3) barEntries.add(
                BarEntry(
                    index.toFloat(),
                    floatArrayOf(
                        recommendationTrend.strongSell?.toFloat() ?: 0F,
                        recommendationTrend.sell?.toFloat() ?: 0F,
                        recommendationTrend.hold?.toFloat() ?: 0F,
                        recommendationTrend.buy?.toFloat() ?: 0F,
                        recommendationTrend.strongBuy?.toFloat() ?: 0F,
                    ),
                    recommendationTrend.period ?: ""
                )
            )
            dateEntries.add(recommendationTrend.period ?: "")
        }*/


        barChart.data = setUpBar(barEntries)
        barChart.invalidate()
        barChart.visibility = View.VISIBLE
    }

    private fun setUpBar(valsbar: MutableList<BarEntry>): BarData {
        val colorsArray = itemView.context.resources.getIntArray(R.array.bar_chart_colors).map { it }
        val labelsArray = itemView.context.resources.getStringArray(R.array.bar_chart_labels)

        // scaling and dragging
        barChart.isDragEnabled = false
        barChart?.setScaleEnabled(false)

        // hidfe background grids
        barChart.setDrawBorders(false)
        barChart.setDrawGridBackground(false)

        val yAxis = barChart?.axisLeft
        val rightAxis = barChart?.axisRight
        yAxis?.setDrawGridLines(true) //
        rightAxis?.setDrawGridLines(false)
        val xAxis = barChart?.xAxis
        xAxis?.setDrawGridLines(false)
        xAxis?.setDrawLabels(false) //
        yAxis?.setDrawLabels(true) //
        rightAxis?.setDrawLabels(false)

        // hide frame
        xAxis?.setDrawAxisLine(false)
        yAxis?.setDrawAxisLine(false)
        rightAxis?.setDrawAxisLine(false)

        // чтобы можно было листать viewpager
        barChart.isHighlightPerDragEnabled = false
        barChart.isHighlightPerTapEnabled = false

        barChart.requestDisallowInterceptTouchEvent(true)

        // set up legend
        val legendArray = mutableListOf<LegendEntry>()
        colorsArray.forEachIndexed { index, i ->
            legendArray.add(
                LegendEntry(
                    labelsArray.get(index),
                    Legend.LegendForm.CIRCLE,
                    14f,
                    9f,
                    null,
                    i
                )
            )
        }
        barChart.legend?.isEnabled = true
        barChart.legend.setCustom(legendArray)
        barChart.legend?.textSize = 12f
        barChart.legend?.typeface = Typeface.create("Legend bold",Typeface.BOLD)
        barChart.description?.isEnabled = false

        val barDataSet = BarDataSet(valsbar, "11111")
        barDataSet.setDrawValues(false)
        barDataSet.colors = colorsArray
        barDataSet.valueTextSize = 16f // text size of values
        barDataSet.valueTypeface = Typeface.create("Value bold",Typeface.BOLD)


        val barData = BarData(barDataSet)
        barData.setDrawValues(true)
        barData.barWidth = 0.2f

        return barData
    }

    fun setRefreshing(state: Boolean) {
        refreshLayout?.isRefreshing = state
    }
}
