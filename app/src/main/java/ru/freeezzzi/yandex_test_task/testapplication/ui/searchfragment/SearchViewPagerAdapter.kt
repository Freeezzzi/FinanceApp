package ru.freeezzzi.yandex_test_task.testapplication.ui.searchfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.AllTabViewHodler
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.FavouritesTabViewHolder
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerViewHodler
import java.lang.IllegalArgumentException

/**
 * Класс по функциональности похож на tabs.ViewPagerAdapter, но потребовалось добавить 3 вкладку в начало, поэтому создан другой класс
 */
class SearchViewPagerAdapter(
    private val popularQueries: List<String>,
    private var recentQueries: List<String>,
    private val refreshListener: SwipeRefreshLayout.OnRefreshListener,
    private val scrollListener: RecyclerView.OnScrollListener,
    private val chipClickListener: (String) -> Unit,
    private val clickListener: (CompanyProfile) -> Unit,
    private val starClickListener: (CompanyProfile) -> Unit
) : RecyclerView.Adapter<ViewPagerViewHodler>() {
    // Храним viewHodler чтобы можно было прятать анимацию загрузки когда потребуется через функцтю setRefreshing
    private var allTab: AllTabViewHodler? = null
    private var favoritesTab: FavouritesTabViewHolder? = null
    private var chipsTab: ChipsTabViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHodler =
        when (viewType) {
            VIEW_TYPE_FAVOURITES -> {
                favoritesTab = FavouritesTabViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.quotes_fragment_favouritestab, parent, false))
                favoritesTab as FavouritesTabViewHolder
            }
            VIEW_TYPE_ALL -> {
                allTab = AllTabViewHodler(LayoutInflater.from(parent.context).inflate(R.layout.quotes_fragment_alltab, parent, false))
                allTab as AllTabViewHodler
            }
            else -> { chipsTab = ChipsTabViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.search_fragment_chips, parent, false)
            )
                chipsTab as ChipsTabViewHolder
            }
        }

    override fun onBindViewHolder(holder: ViewPagerViewHodler, position: Int) {
        when (holder) {
            is AllTabViewHodler -> { holder.onBind(
                clickListener = clickListener,
                starClickListener = starClickListener,
                refreshListener,
                scrollListener
            ) }
            is FavouritesTabViewHolder -> { holder.onBind(
                clickListener = clickListener,
                starClickListener = starClickListener
            ) }
            is ChipsTabViewHolder -> holder.onBind(
                popularQueries,
                recentQueries,
                    chipClickListener
            )
        }
    }

    override fun getItemCount(): Int = 3

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> VIEW_TYPE_QUERIES
            1 -> VIEW_TYPE_ALL
            2 -> VIEW_TYPE_FAVOURITES
            else -> throw IllegalArgumentException("Wrong position")
        }

    fun setRefreshing(condition: Boolean) {
        allTab?.setRefreshing(condition)
    }

    fun submitFavorites(companyProfiles: List< CompanyProfile>) {
        favoritesTab?.submitData(companyProfiles)
    }

    fun submitAll(companyProfiles: List<CompanyProfile>) {
        allTab?.submitData(companyProfiles)
    }

    fun submitQueries(list: List<String>) {
        recentQueries = list
        chipsTab?.updateLayout(recentQueries, chipClickListener)
    }

    companion object {
        const val VIEW_TYPE_QUERIES = 0
        const val VIEW_TYPE_ALL = 1
        const val VIEW_TYPE_FAVOURITES = 2
    }
}
