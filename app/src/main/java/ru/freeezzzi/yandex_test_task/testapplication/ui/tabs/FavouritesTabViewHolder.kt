package ru.freeezzzi.yandex_test_task.testapplication.ui.tabs

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist.QuotesListAdapter

class FavouritesTabViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val recyclerView: RecyclerView? = itemView.findViewById(R.id.favourites_recyclerview)
    private val layoutManager = LinearLayoutManager(itemView.context)
    private var adapter: QuotesListAdapter? = null

    fun onBind(
        adapter: QuotesListAdapter
    ) {
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = layoutManager
    }

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
}