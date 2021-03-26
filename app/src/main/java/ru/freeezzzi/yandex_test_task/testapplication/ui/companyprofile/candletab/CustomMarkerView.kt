package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.candletab

import android.content.Context
import android.util.Log
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import ru.freeezzzi.yandex_test_task.testapplication.databinding.CustomMarkerViewBinding

class CustomMarkerView(
    context: Context,
    layoutResource: Int
) : MarkerView(context, layoutResource) {
    private val binding by viewBinding(CustomMarkerViewBinding::bind)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)

        binding.markerViewPrice.text = "${e?.y}"
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width)/2F, -height.toFloat())
    }
}
