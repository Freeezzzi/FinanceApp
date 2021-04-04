package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.candletab

import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.StockCandle
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerViewHodler

class CandleViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val priceTextView: TextView = itemView.findViewById(R.id.candle_price)
    private val priceChangeTextView: TextView = itemView.findViewById(R.id.candle_pricechange)
    private val candleStickChart: CandleStickChart? = itemView.findViewById(R.id.candle_stick_chart)
    private val refreshLayout: SwipeRefreshLayout? = itemView.findViewById(R.id.chart_swipterefreshlayout)
    private val chipGroup: ChipGroup = itemView.findViewById(R.id.candles_chip_group)
    private var currency: Char? = null

    fun onBind(
        getCandleListener: (resolution: String, from: Long, to: Long) -> Unit,
        getPrices: () -> Pair<String, String>
    ) {
        val getCandleFunc = {
            val pair = when (getSelectedChipContent()) {
                itemView.context.getString(R.string.day_chip) -> FIFTEEN_MINUTES_RESOLUTION to DAY
                itemView.context.getString(R.string.week_chip) -> DAY_RESOLUTION to WEEK
                itemView.context.getString(R.string.two_weeks_chip) -> DAY_RESOLUTION to TWO_WEEKS
                itemView.context.getString(R.string.month_chip) -> DAY_RESOLUTION to MONTH
                itemView.context.getString(R.string.six_month_chip) -> WEEK_RESOLUTION to SIX_MONTH
                itemView.context.getString(R.string.year_chip) -> WEEK_RESOLUTION to YEAR
                else -> DAY_RESOLUTION to MONTH
            }
            val resolution = pair.first
            val interval = pair.second

            val to = System.currentTimeMillis() / 1000
            getCandleListener(
                resolution,
                to - interval,
                to
            )
        }
        refreshLayout?.setOnRefreshListener { getCandleFunc() }

        // здесь можно было бы понять какой чип по id чипа, но для краткости я использовал ту же функцию
        chipGroup.setOnCheckedChangeListener { _, _ -> getCandleFunc() }

        getCandleFunc.invoke()
        val prices = getPrices.invoke()
        setPrices(prices.first, prices.second)

        currency = prices.first[0]
        val customMarkerView = CustomMarkerView(itemView.context, R.layout.custom_marker_view, currency ?: ' ')
        candleStickChart?.marker = customMarkerView
    }

    fun setCandleValue(
        stockCandle: StockCandle
    ) {
        val valsCandleStick = mutableListOf<CandleEntry>()
        val minValue = minOf(stockCandle.c?.size ?: 0, stockCandle.h?.size ?: 0, stockCandle.l?.size ?: 0,
            stockCandle.o?.size ?: 0, stockCandle.t?.size ?: 0)
        if (minValue == 0) return
        // Возьмем минимальную длину из полученных массивов(на всякий случай)
        for (i in 0 until minValue) {
            valsCandleStick.add(
                CandleEntry(
                    i.toFloat(),
                    stockCandle.h?.get(i) ?: 0F,
                    stockCandle.l?.get(i) ?: 0F,
                    stockCandle.o?.get(i) ?: 0F,
                    stockCandle.c?.get(i) ?: 0F,
                    stockCandle.t?.get(i) ?: " "
                )
            )
        }

        val data = setUpCandle(valsCandleStick)
        candleStickChart?.data = data
        candleStickChart?.invalidate()
        candleStickChart?.visibility = View.VISIBLE
    }

    private fun setUpCandle(valsCandleStick: MutableList<CandleEntry>): CandleData {
        // scaling and dragging
        candleStickChart?.isDragEnabled = false
        candleStickChart?.setScaleEnabled(false)
        candleStickChart?.setTouchEnabled(true)

        // hidfe background grids
        candleStickChart?.setDrawBorders(false)
        candleStickChart?.setDrawGridBackground(false)

        val yAxis = candleStickChart?.axisLeft
        val rightAxis = candleStickChart?.axisRight
        yAxis?.setDrawGridLines(false)
        rightAxis?.setDrawGridLines(false)
        val xAxis = candleStickChart?.xAxis
        xAxis?.setDrawGridLines(false)
        xAxis?.setDrawLabels(false)
        yAxis?.setDrawLabels(false)
        rightAxis?.setDrawLabels(false)

        // hide frame
        xAxis?.setDrawAxisLine(false)
        yAxis?.setDrawAxisLine(false)
        rightAxis?.setDrawAxisLine(false)

        /*xAxis?.granularity = 1f
        xAxis?.isGranularityEnabled = true
        xAxis?.setAvoidFirstLastClipping(true)*/

        // чтобы можно было листать viewpager
        candleStickChart?.isHighlightPerDragEnabled = false

        candleStickChart?.requestDisallowInterceptTouchEvent(true)

        // disable legend
        candleStickChart?.legend?.isEnabled = false
        candleStickChart?.description?.isEnabled = false

        // настрйока цветов candles
        val set1 = CandleDataSet(valsCandleStick, "Company")
        set1.setColor(Color.rgb(80, 80, 80))
        set1.shadowColor = ContextCompat.getColor(itemView.context, R.color.Grey)
        set1.shadowWidth = 1f
        set1.decreasingColor = ContextCompat.getColor(itemView.context, R.color.red)
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = ContextCompat.getColor(itemView.context, R.color.green)
        set1.increasingPaintStyle = Paint.Style.FILL
        set1.neutralColor = ContextCompat.getColor(itemView.context, R.color.light_grey)
        set1.setDrawValues(false)

        set1.setDrawHorizontalHighlightIndicator(false)
        set1.setDrawVerticalHighlightIndicator(false)

        return CandleData(set1)
    }

    private fun setPrices(price: String, priceChange: String) {
        if (priceChange[0] == '+') {
            priceChangeTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
        } else if (priceChange[0] == '-') {
            priceChangeTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
        } else {
            priceChangeTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.font_black))
        }

        priceTextView.text = price
        priceChangeTextView.text = priceChange
    }

    private fun getSelectedChipContent(): String =
        chipGroup.findViewById<Chip>(chipGroup.checkedChipId).text.toString()

    fun setRefreshing(state: Boolean) {
        refreshLayout?.isRefreshing = state
    }

    companion object {
        const val YEAR = 60L * 60 * 24 * 350 // 340 дней для того чтобы api точно пропустило
        const val SIX_MONTH = 60L * 60 * 24 * 30 * 6
        const val MONTH = 60L * 60 * 24 * 30
        const val TWO_WEEKS = 60L * 60 * 24 * 14
        const val WEEK = 60L * 60 * 24 * 7
        const val DAY = 60L * 60 * 24

        const val WEEK_RESOLUTION = "W"
        const val DAY_RESOLUTION = "D"
        const val SIXTY_MINUTES_RESOLUTION = "60"
        const val FIFTEEN_MINUTES_RESOLUTION = "15"
        const val THIRTY_MINUTES_RESOLUTION = "30"
    }
}
