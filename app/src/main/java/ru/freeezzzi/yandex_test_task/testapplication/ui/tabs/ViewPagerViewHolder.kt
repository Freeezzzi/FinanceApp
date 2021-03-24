package ru.freeezzzi.yandex_test_task.testapplication.ui.tabs

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist.QuotesListAdapter

abstract class ViewPagerViewHodler(itemView: View) : RecyclerView.ViewHolder(itemView)

class AllTabViewHodler(itemView: View) : ViewPagerViewHodler(itemView) {
    private val recyclerView: RecyclerView? = itemView.findViewById(R.id.trades_recyclerview)
    private val refreshLayout: SwipeRefreshLayout? = itemView.findViewById(R.id.trades_swipeRefreshLayout)
    private val layoutManager = LinearLayoutManager(itemView.context)

    fun onBind(
        adapter: QuotesListAdapter,
        refreshListener: SwipeRefreshLayout.OnRefreshListener,
        scrollListener: RecyclerView.OnScrollListener,
    ) {
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter
        recyclerView?.addOnScrollListener(scrollListener)
        refreshLayout?.setOnRefreshListener(refreshListener)
    }

    fun setRefreshing(condition: Boolean) {
        refreshLayout?.isRefreshing = condition
    }
}

class FavouritesTabViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val recyclerView: RecyclerView? = itemView.findViewById(R.id.favourites_recyclerview)
    private val layoutManager = LinearLayoutManager(itemView.context)

    fun onBind(
        adapter: QuotesListAdapter
    ) {
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = layoutManager
    }
}

class ChipsTabViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val popularChipGroup: ChipGroup = itemView.findViewById(R.id.popular_search_chip_group)
    private val recentChipGroup: ChipGroup = itemView.findViewById(R.id.recent_search_chip_group)

    fun onBind(
        poplarQueries: List<String>,
        recentQueries: List<String>,
    ) {
        val backgroundColor = ContextCompat.getColor(itemView.context,R.color.light_blue)
        poplarQueries.forEach {
            val chip = Chip(itemView.context)
            chip.setText(it)
            chip.setBackgroundColor(backgroundColor)
            popularChipGroup.addView(chip)
        }
        //TODO в chipgroup постоянно завново записываются все чипы, а старые не удаляются
        //TODO сделать отображение нового чипа и удаление старых в этом методе
        recentQueries.asReversed().forEach {
            val chip = Chip(itemView.context)
            chip.setBackgroundColor(backgroundColor)
            chip.setEnsureMinTouchTargetSize(false)
            chip.setText(it)
            recentChipGroup.addView(chip)
        }
        recentChipGroup
    }
}
