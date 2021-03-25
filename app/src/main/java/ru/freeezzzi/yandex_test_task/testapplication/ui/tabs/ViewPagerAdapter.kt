package ru.freeezzzi.yandex_test_task.testapplication.ui.tabs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist.QuotesListAdapter
import java.lang.IllegalArgumentException

class ViewPagerAdapter(
    private val allTabAdapter: QuotesListAdapter,
    private val favouritesAdapter: QuotesListAdapter,
    private val refreshListener: SwipeRefreshLayout.OnRefreshListener,
    private val scrollListener: RecyclerView.OnScrollListener,
) : RecyclerView.Adapter<ViewPagerViewHodler>() {
    // Храним viewHodler чтобы можно было прятать анимацию загрузки когда потребуется через функцтю setRefreshing
    private var allTab: AllTabViewHodler? = null
    private var favoritesTab: FavouritesTabViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHodler =
        when (viewType) {
            VIEW_TYPE_FAVOURITES -> {
                favoritesTab = FavouritesTabViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.quotes_fragment_favouritestab, parent, false))
                favoritesTab as FavouritesTabViewHolder
            }
            else -> {
                allTab = AllTabViewHodler(LayoutInflater.from(parent.context).inflate(R.layout.quotes_fragment_alltab, parent, false))
            allTab as AllTabViewHodler
            }
        }

    override fun onBindViewHolder(holder: ViewPagerViewHodler, position: Int) {
        when (holder) {
            is AllTabViewHodler -> { holder.onBind(
                adapter = allTabAdapter,
                refreshListener = refreshListener,
                scrollListener = scrollListener
            ) }
            is FavouritesTabViewHolder -> { holder.onBind(
                favouritesAdapter
            ) }
        }
    }

    override fun getItemCount(): Int = 2

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> VIEW_TYPE_ALL
            1 -> VIEW_TYPE_FAVOURITES
            else -> throw IllegalArgumentException("Wrong position")
        }

    fun setRefreshing(condition: Boolean) {
        allTab?.setRefreshing(condition)
    }

    companion object {
        const val VIEW_TYPE_ALL = 1
        const val VIEW_TYPE_FAVOURITES = 2
    }
}
