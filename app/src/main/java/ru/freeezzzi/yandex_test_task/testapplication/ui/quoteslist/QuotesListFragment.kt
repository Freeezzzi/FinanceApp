package ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.freeezzzi.yandex_test_task.testapplication.App
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.databinding.QuotesListFragmentBinding
import ru.freeezzzi.yandex_test_task.testapplication.di.viewmodels.quoteslist.DaggerQuotesListViewModelComponent
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.ui.BaseFragment
import ru.freeezzzi.yandex_test_task.testapplication.ui.ViewState

class QuotesListFragment : BaseFragment(R.layout.quotes_list_fragment) {
    private val binding by viewBinding(QuotesListFragmentBinding::bind)

    private val quotesAdapter = QuotesListAdapter(
            clickListener = { viewModel.itemOnClickAction(it) },
            starClickListener = { viewModel.addToFavorites(it) }
    )

    private val viewModel: QuotesListViewModel by viewModels(
            factoryProducer = { QuotesListViewModelFactory() })

    private var layoutManager: LinearLayoutManager? = null

    override fun initViews(view: View) {
        super.initViews(view)
        (activity as AppCompatActivity)?.supportActionBar?.hide()

        viewModel.getTickers()

        layoutManager = LinearLayoutManager(requireContext())

        binding.tradesRecyclerview.layoutManager = layoutManager
        binding.tradesRecyclerview.adapter = quotesAdapter
        binding.tradesRecyclerview.addOnScrollListener(this.OnVerticalScrollListener(layoutManager = layoutManager!!))
        binding.tradesSwipeRefreshLayout.setOnRefreshListener {
            viewModel.getTickers()
            binding.tradesSwipeRefreshLayout.isRefreshing = false
        }

        viewModel.companies.observe(viewLifecycleOwner, this::updateAdapter)
        viewModel.tickersList.observe(viewLifecycleOwner, this::updateTickers)
    }

    fun updateAdapter(companies: ViewState<List<CompanyProfile>, String?>) {
        when (companies) {
            is ViewState.Success -> {
                quotesAdapter.submitList(companies.result)
                binding.tradesSwipeRefreshLayout.isRefreshing = false
            }
            is ViewState.Loading -> binding.tradesSwipeRefreshLayout.isRefreshing = true
            is ViewState.Error -> {
                quotesAdapter.submitList(companies.oldvalue)
                showError(companies.result ?: "Couldn't load companies")
                binding.tradesSwipeRefreshLayout.isRefreshing = false
            }
        }
    }

    fun updateTickers(tickers: ViewState<List<String>, String?>) {
        when(tickers){
            is ViewState.Success -> {
                viewModel.clearCompaniesList()
                viewModel.getCompanies(10) // В первый раз загружаем 10 компаний
            }
            is ViewState.Error -> {
                showError(tickers.result?:"Couldn't load tickers")
            }
        }
    }

    private fun showError(msg: String) {
        var errorMsg = msg
        when (errorMsg) {
            "HTTP 429 " -> errorMsg = getString(R.string.limit_error)
            "Tickers loading.Try again!" -> return // В этом случае просто ждем
        }
        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT)
                .show()
    }

    override fun onBackPressed() {
        viewModel.exitFragment()
    }

    inner class OnVerticalScrollListener(
        val layoutManager: LinearLayoutManager,
    ) : RecyclerView.OnScrollListener() {

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
            // viewModel.getTickers()
        }

        fun onScrolledToBottom() {
            viewModel.getCompanies(7)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = QuotesListFragment()
    }
}

private class QuotesListViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DaggerQuotesListViewModelComponent.builder()
            .appComponent(App.appComponent)
            .build()
            .provideViewModel() as T
    }
}
