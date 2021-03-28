package ru.freeezzzi.yandex_test_task.testapplication.ui.searchfragment

import android.view.View
import android.widget.TextView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerViewHodler

class ChipsTabViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val popularChipGroup: ChipGroup = itemView.findViewById(R.id.popular_search_chip_group)
    private val recentChipGroup: ChipGroup = itemView.findViewById(R.id.recent_search_chip_group)

    fun onBind(
        poplarQueries: List<String>,
        recentQueries: List<String>,
        chipClickListener: (String) -> Unit
    ) {
        poplarQueries.forEach {
            val chip = Chip(itemView.context)
            chip.text = it
            chip.setChipBackgroundColorResource(R.color.light_blue)
            chip.setEnsureMinTouchTargetSize(false)
            chip.setOnClickListener {
                chipClickListener((it as TextView).text.toString()) }
            popularChipGroup.addView(chip)
        }
        recentQueries.asReversed().forEach {
            val chip = Chip(itemView.context)
            chip.setChipBackgroundColorResource(R.color.light_blue)
            chip.setEnsureMinTouchTargetSize(false)
            chip.setText(it)
            chip.setOnClickListener {
                chipClickListener((it as TextView).text.toString()) }
            recentChipGroup.addView(chip)
        }
    }

    /**
     * Обновляет chipGroup недавних запросов
     */
    fun updateLayout(
        recentQueries: List<String>,
        chipClickListener: (String) -> Unit
    ) {
        recentChipGroup.removeAllViews()
        recentQueries.asReversed().forEach {
            val chip = Chip(itemView.context)
            chip.setChipBackgroundColorResource(R.color.light_blue)
            chip.setEnsureMinTouchTargetSize(false)
            chip.setText(it)
            chip.setOnClickListener {
                chipClickListener((it as TextView).text.toString()) }
            recentChipGroup.addView(chip)
        }
    }
}
