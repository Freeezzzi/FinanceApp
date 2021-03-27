package ru.freeezzzi.yandex_test_task.testapplication.ui.tabs

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist.QuotesListAdapter

class AllTabViewHodler(itemView: View) : ViewPagerViewHodler(itemView) {
    private val recyclerView: RecyclerView? = itemView.findViewById(R.id.trades_recyclerview)
    private val refreshLayout: SwipeRefreshLayout? = itemView.findViewById(R.id.trades_swipeRefreshLayout)
    private val layoutManager = LinearLayoutManager(itemView.context)
    private var adapter: QuotesListAdapter? = null

    fun onBind(
        adapter: QuotesListAdapter,
        refreshListener: SwipeRefreshLayout.OnRefreshListener,
        scrollListener: RecyclerView.OnScrollListener,
    ) {
        this.adapter = adapter
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter
        recyclerView?.addOnScrollListener(scrollListener)
        refreshLayout?.setOnRefreshListener(refreshListener)
    }

    fun setRefreshing(condition: Boolean) {
        refreshLayout?.isRefreshing = condition
    }
}