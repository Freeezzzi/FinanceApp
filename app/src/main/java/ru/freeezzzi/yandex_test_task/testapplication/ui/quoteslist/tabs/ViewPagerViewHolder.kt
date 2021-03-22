package ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist.tabs

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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

    fun setRefreshing(condition: Boolean){
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
