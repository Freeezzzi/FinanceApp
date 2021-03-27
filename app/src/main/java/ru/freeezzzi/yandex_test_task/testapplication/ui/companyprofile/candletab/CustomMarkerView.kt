package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.candletab

import android.content.Context
import android.text.format.DateFormat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import ru.freeezzzi.yandex_test_task.testapplication.databinding.CustomMarkerViewBinding
import java.util.*

class CustomMarkerView(
    context: Context,
    layoutResource: Int,
    private val currency: Char
) : MarkerView(context, layoutResource) {
    private val binding by viewBinding(CustomMarkerViewBinding::bind)
    private var mOffset: MPPointF? = null

    override fun refreshContent(e: Entry?, highlight: Highlight?) {

        binding.markerViewPrice.text = String.format("%c%.2f", currency, e?.y)
        binding.markerViewDate.text = getDate(e?.data as Long)
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        if (mOffset == null) {
            mOffset = MPPointF(-(width.toFloat()) / 2F, -height.toFloat() * 1.5F)
        }
        return mOffset!!
    }

    /**
     * Возвращает 2 даты -30 днйе назад и текущую в формате YYYY-MM-DD
     */
    private fun getDate(time: Long): String {
        val date = Date(time * 1000)

        return DateFormat.format(DATE_FORMAT, date).toString()
    }

    companion object {
        private const val DATE_FORMAT = "d MMM yyyy"
    }
}
