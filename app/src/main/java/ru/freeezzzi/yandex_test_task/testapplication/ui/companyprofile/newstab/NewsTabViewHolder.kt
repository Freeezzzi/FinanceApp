package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.newstab

import android.text.format.DateFormat
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerViewHodler
import java.util.*

// TODO Добавить отображение даты
class NewsTabViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val recyclerView: RecyclerView? = itemView.findViewById(R.id.news_recyclerview)
    private val refreshLayout: SwipeRefreshLayout? = itemView.findViewById(R.id.news_swiperefreshlayout)
    private val layoutManager = LinearLayoutManager(itemView.context)
    private var adapter: NewsListAdapter? = null
    private var getNewsListener: ((String, String) -> Unit)? = null
    private var previousQueryTime = System.currentTimeMillis()

    fun onBind(
        adapter: NewsListAdapter,
        getNewsListener: (from: String, to: String) -> Unit
    ) {
        this.getNewsListener = getNewsListener
        this.adapter = adapter
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter
        recyclerView?.addOnScrollListener(this.OnVerticalScrollListener())
        refreshLayout?.setOnRefreshListener {
            previousQueryTime = System.currentTimeMillis()
            invokeGetNews()
        }
        invokeGetNews()
    }

    fun setRefreshing(state: Boolean) {
        refreshLayout?.isRefreshing = state
    }

    private fun invokeGetNews(){
        val fromToValues = getDates(previousQueryTime)
        getNewsListener?.invoke(fromToValues.first, fromToValues.second)
    }

    /**
     * Возвращает 2 даты - текущую и 30 днйе назад в формате YYYY-MM-DD
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
            invokeGetNews()
        }
    }

    companion object {
        const val ONE_MONTH = 1000L * 60 * 60 * 24 * 30 // 1 месяц в миллисукндах
        const val DATE_FORMAT = "yyyy-MM-dd"
    }
}
