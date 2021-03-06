package ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.freeezzzi.yandex_test_task.testapplication.App
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.databinding.QuotesListFragmentBinding
import ru.freeezzzi.yandex_test_task.testapplication.di.viewmodels.quoteslist.DaggerQuotesListViewModelComponent
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.ui.BaseFragment
import ru.freeezzzi.yandex_test_task.testapplication.ui.ViewState
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerAdapter

class QuotesListFragment : BaseFragment(R.layout.quotes_list_fragment) {
    private val binding by viewBinding(QuotesListFragmentBinding::bind)

    // Он инициализируется здесь, т.к. вкладка favourites создается после того как туда передается лист
    // по-хорошему его надо создавать в viewholder вкладки
    private val quotesAllAdapter = QuotesListAdapter(
        clickListener = { viewModel.itemOnClickAction(it) },
        starClickListener = { viewModel.addToFavorites(it) }
    )

    private val quotesFavouritesAdapter = QuotesListAdapter(
        clickListener = { viewModel.itemOnClickAction(it) },
        starClickListener = { viewModel.addToFavorites(it) }
    )

    private val viewPagerAdapter = ViewPagerAdapter(
        allTabAdapter = quotesAllAdapter,
        favouritesAdapter = quotesFavouritesAdapter,
        refreshListener = {
            viewModel.getTickers()
        },
        scrollListener = this.OnVerticalScrollListener()
    )

    private val viewModel: QuotesListViewModel by viewModels(
            factoryProducer = { QuotesListViewModelFactory() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getTickers()
    }

    override fun initViews(view: View) {
        super.initViews(view)

        setUpClickListeners()
        viewModel.showFavourites()

        // observe
        viewModel.companies.removeObservers(viewLifecycleOwner)
        viewModel.companies.observe(viewLifecycleOwner, this::updateAllAdapter)
        viewModel.tickersList.observe(viewLifecycleOwner, this::updateTickers)
        viewModel.localCompanies.observe(viewLifecycleOwner, this::updateFavouritesAdapter)
    }

    private fun updateFavouritesAdapter(companies: ViewState<List<CompanyProfile>, String?>) {
        when (companies) {
            is ViewState.Success -> {
                quotesFavouritesAdapter.submitList(companies.result) }
            // is ViewState.Loading ->
            is ViewState.Error -> {
                quotesFavouritesAdapter.submitList(companies.oldvalue)
                val errorMsg = getString(R.string.load_error_msg, getString(R.string.companies))
                showError(companies.result ?: errorMsg, binding.root)
            }
        }
    }

    private fun updateAllAdapter(companies: ViewState<List<CompanyProfile>, String?>) {
        when (companies) {
            is ViewState.Success -> {
                quotesAllAdapter.submitList(companies.result)
                viewPagerAdapter.setRefreshing(false)
            }
            is ViewState.Loading -> viewPagerAdapter.setRefreshing(true)
            is ViewState.Error -> {
                quotesAllAdapter.submitList(companies.oldvalue)
                val errorMsg = getString(R.string.load_error_msg, getString(R.string.companies))
                showError(companies.result ?: errorMsg, binding.root)
                viewPagerAdapter.setRefreshing(false)
            }
        }
    }

    private fun updateTickers(tickers: ViewState<List<String>, String?>) {
        when (tickers) {
            is ViewState.Success -> {
                viewModel.clearCompaniesList()
                viewModel.getCompanies(10) // В первый раз загружаем 10 компаний
            }
            is ViewState.Loading -> viewPagerAdapter.setRefreshing(true)
            is ViewState.Error -> {
                val errorMsg = getString(R.string.load_error_msg, getString(R.string.ticker))
                showError(tickers.result ?: errorMsg, binding.root)
                viewPagerAdapter.setRefreshing(false)
            }
        }
    }

    private fun setUpClickListeners() {
        // ViewPager
        binding.tradesViewpager.adapter = viewPagerAdapter
        binding.tradesViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> setTextInTitles(binding.stocksTextview, binding.favouritesTextview)
                    1 -> setTextInTitles(binding.favouritesTextview, binding.stocksTextview)
                }
                super.onPageSelected(position)
            }
        })

        // set click listeners on labels
        binding.stocksTextview.setOnClickListener {
            setTextInTitles(binding.stocksTextview, binding.favouritesTextview)
            binding.tradesViewpager.setCurrentItem(0, true)
        }
        binding.favouritesTextview.setOnClickListener {
            setTextInTitles(binding.favouritesTextview, binding.stocksTextview)
            binding.tradesViewpager.setCurrentItem(1, true)
        }
        // set click listener on searchView
        binding.searchCardview.setOnClickListener { viewModel.searchAction() }
        binding.tradesListEdittext.setOnClickListener { viewModel.searchAction() }
    }

    fun setTextInTitles(currentTextView: TextView, anotherTextView: TextView) {
        currentTextView.setTypeface(Typeface.DEFAULT_BOLD)
        currentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28F)

        anotherTextView.setTypeface(Typeface.DEFAULT)
        anotherTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18F)
    }

    override fun onBackPressed() {
        viewModel.exitFragment()
    }

    inner class OnVerticalScrollListener : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (!recyclerView.canScrollVertically(1)) {
                onScrolledToBottom()
            }
        }


        private fun onScrolledToBottom() {
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
