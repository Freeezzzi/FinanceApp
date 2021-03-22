package ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.freeezzzi.yandex_test_task.testapplication.databinding.QuoteItemBinding
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile

class QuotesListAdapter(
    private val clickListener: (CompanyProfile) -> Unit,
    private val starClickListener: (CompanyProfile) -> Unit
) : ListAdapter<CompanyProfile, QuotesListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotesListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = QuoteItemBinding.inflate(layoutInflater, parent, false)
        return QuotesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuotesListViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            clickListener = clickListener,
            starClickListener = starClickListener,
            position % 2 != 0
        )
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CompanyProfile>() {
            override fun areItemsTheSame(oldItem: CompanyProfile, newItem: CompanyProfile): Boolean =
                oldItem.ticker == newItem.ticker

            override fun areContentsTheSame(oldItem: CompanyProfile, newItem: CompanyProfile): Boolean =
                oldItem == newItem
        }
    }
}
