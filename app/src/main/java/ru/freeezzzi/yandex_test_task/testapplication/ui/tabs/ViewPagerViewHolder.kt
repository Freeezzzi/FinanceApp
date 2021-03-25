package ru.freeezzzi.yandex_test_task.testapplication.ui.tabs

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist.QuotesListAdapter

abstract class ViewPagerViewHodler(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun submitData(companyProfiles: List<CompanyProfile>)
}

class AllTabViewHodler(itemView: View) : ViewPagerViewHodler(itemView) {
    private val recyclerView: RecyclerView? = itemView.findViewById(R.id.trades_recyclerview)
    private val refreshLayout: SwipeRefreshLayout? = itemView.findViewById(R.id.trades_swipeRefreshLayout)
    private val layoutManager = LinearLayoutManager(itemView.context)
    private var adapter: QuotesListAdapter? = null

    fun onBind(
        clickListener: (CompanyProfile) -> Unit,
        starClickListener: (CompanyProfile) -> Unit,
        refreshListener: SwipeRefreshLayout.OnRefreshListener,
        scrollListener: RecyclerView.OnScrollListener,
    ) {
        adapter = QuotesListAdapter(
            clickListener = clickListener,
            starClickListener = starClickListener
        )
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter
        recyclerView?.addOnScrollListener(scrollListener)
        refreshLayout?.setOnRefreshListener(refreshListener)
    }

    fun setRefreshing(condition: Boolean) {
        refreshLayout?.isRefreshing = condition
    }

    override fun submitData(companyProfiles: List<CompanyProfile>) {
        adapter?.submitList(companyProfiles)
    }
}

class FavouritesTabViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val recyclerView: RecyclerView? = itemView.findViewById(R.id.favourites_recyclerview)
    private val layoutManager = LinearLayoutManager(itemView.context)
    private var adapter: QuotesListAdapter? = null

    fun onBind(
        clickListener: (CompanyProfile) -> Unit,
        starClickListener: (CompanyProfile) -> Unit
    ) {
        adapter = QuotesListAdapter(
            clickListener = clickListener,
            starClickListener = starClickListener
        )
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = layoutManager
    }

    override fun submitData(companyProfiles: List<CompanyProfile>) {
        adapter?.submitList(companyProfiles)
    }
}
