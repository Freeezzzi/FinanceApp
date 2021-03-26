package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.newstab

import android.text.format.DateFormat
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerViewHodler
import java.util.*

class NewsTabViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val recyclerView: RecyclerView? = itemView.findViewById(R.id.news_recyclerview)
    private val refreshLayout: SwipeRefreshLayout? = itemView.findViewById(R.id.news_swiperefreshlayout)
    private val layoutManager = LinearLayoutManager(itemView.context)
    private var adapter: NewsListAdapter? = null
    private var getNewsListener: ((String, String, Boolean) -> Unit)? = null
    private var previousQueryTime = System.currentTimeMillis()

    fun onBind(
        adapter: NewsListAdapter,
        getNewsListener: (from: String, to: String, clearList: Boolean) -> Unit
    ) {
        this.getNewsListener = getNewsListener
        this.adapter = adapter
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter
        recyclerView?.addOnScrollListener(this.OnVerticalScrollListener())
        refreshLayout?.setOnRefreshListener {
            previousQueryTime = System.currentTimeMillis()
            invokeGetNews(true)
        }
        invokeGetNews(false)
    }

    fun setRefreshing(state: Boolean) {
        refreshLayout?.isRefreshing = state
    }

    private fun invokeGetNews(clearList: Boolean) {
        val fromToValues = getDates(previousQueryTime)
        getNewsListener?.invoke(fromToValues.first, fromToValues.second, clearList)
    }

    /**
     * Возвращает 2 даты -30 днйе назад и текущую в формате YYYY-MM-DD
     */
    private fun getDates(from: Long): Pair<String, String> {
        val toDate = Date(from)
        val fromDate = Date(from - ONE_MONTH)

        previousQueryTime = from - ONE_MONTH
        return DateFormat.format(DATE_FORMAT, fromDate).toString() to DateFormat.format(DATE_FORMAT, toDate).toString()
    }

    inner class OnVerticalScrollListener : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (!recyclerView.canScrollVertically(1)) {
                onScrolledToBottom()
            }
        }

        fun onScrolledToBottom() {
            invokeGetNews(false)
        }
    }

    companion object {
        const val ONE_MONTH = 1000L * 60 * 60 * 24 * 30 // 1 месяц в миллисукндах
        const val DATE_FORMAT = "yyyy-MM-dd"
    }
}
