package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile

import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.StockCandle
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerViewHodler

class CandleViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val candleStickChart: CandleStickChart? = itemView.findViewById(R.id.candle_stick_chart)

    fun onBind() {
    }

    fun setCandleValues(
        stockCandle: StockCandle
    ) {
        var timeDifference: Long = 1
        var firstMeasure: Long = 0
        if (stockCandle.t?.size ?: 0 >= 2) {
            timeDifference = stockCandle.t?.get(1) ?: 0 - (stockCandle.t?.get(0) ?: 0)
            firstMeasure = stockCandle.t?.get(0) ?: 0
        }
        // scalling and dragging
        candleStickChart?.isDragEnabled = false
        candleStickChart?.setScaleEnabled(false)

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

        //hide frame
        xAxis?.setDrawAxisLine(false)
        yAxis?.setDrawAxisLine(false)
        rightAxis?.setDrawAxisLine(false)

        /*xAxis?.granularity = 1f
        xAxis?.isGranularityEnabled = true
        xAxis?.setAvoidFirstLastClipping(true)*/

        // чтобы можно было листать recyclerview
        candleStickChart?.isHighlightPerDragEnabled = false

        candleStickChart?.requestDisallowInterceptTouchEvent(true)

        // disable legend
        candleStickChart?.legend?.isEnabled = false
        candleStickChart?.description?.isEnabled = false

        val valsCandleStick = mutableListOf<CandleEntry>()
        val minValue = minOf(stockCandle.c?.size ?: 0, stockCandle.h?.size ?: 0, stockCandle.l?.size ?: 0,
            stockCandle.o?.size ?: 0, stockCandle.t?.size ?: 0)
        // Возьмем минимальную длину из полученных массивов(на всякий случай)
        for (i in 0..(minValue - 1)) {
            valsCandleStick.add(
                CandleEntry(
                    i.toFloat(),
                    stockCandle?.h?.get(i) ?: 0F,
                    stockCandle?.l?.get(i) ?: 0F,
                    stockCandle?.o?.get(i) ?: 0F,
                    stockCandle?.c?.get(i) ?: 0F
                )
            )
        }

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

        val data = CandleData(set1)
        candleStickChart?.data = data
        candleStickChart?.invalidate()
        candleStickChart?.visibility = View.VISIBLE
    }

    /**
     * Преобразует значение в одно из значений 0,1,2... для равномерного арсположения на оси
     */
    private fun convertToNormalized(value: Long, firstMeasure: Long, timeDiff: Long) = (value - firstMeasure).toFloat() / timeDiff
}

class SummaryViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    fun onBind() {
    }
}

class NewsTabViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    fun onBind() {
    }
}

class ForecastsViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    fun onBind() {
    }
}
