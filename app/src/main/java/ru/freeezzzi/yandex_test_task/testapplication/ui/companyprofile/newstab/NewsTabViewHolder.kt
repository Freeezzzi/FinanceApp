package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.newstab

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.News
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerViewHodler

// TODO менять дату в зависимости от выбора пользователя
// TODO Добавить отображение даты
class NewsTabViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val recyclerView: RecyclerView? = itemView.findViewById(R.id.news_recyclerview)
    private val refreshLayout: SwipeRefreshLayout? = itemView.findViewById(R.id.news_swiperefreshlayout)
    private val layoutManager = LinearLayoutManager(itemView.context)
    private var adapter: NewsListAdapter? = null
    private var getNewsListener: ((String, String) -> Unit)? = null

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
            getNewsListener("2021-02-22", "2021-03-22")
        }
    }

    fun setRefreshing(state: Boolean) {
        refreshLayout?.isRefreshing = state
    }

    inner class OnVerticalScrollListener : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (!recyclerView.canScrollVertically(-1)) {
                onScrolledToTop()
            } else if (!recyclerView.canScrollVertically(1)) {
                onScrolledToBottom()
            } else if (dy < 0) {
                onScrolledUp()
            } else if (dy > 0) {
                onScrolledDown()
            }
        }

        fun onScrolledUp() {
        }

        fun onScrolledDown() {}

        fun onScrolledToTop() {
        }

        fun onScrolledToBottom() {
            getNewsListener?.invoke("2021-02-22", "2021-03-22")
        }
    }
}
