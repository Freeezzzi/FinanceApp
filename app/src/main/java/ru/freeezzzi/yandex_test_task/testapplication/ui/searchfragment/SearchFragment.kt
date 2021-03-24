package ru.freeezzzi.yandex_test_task.testapplication.ui.searchfragment

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.freeezzzi.yandex_test_task.testapplication.App
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.databinding.SearchFragmentBinding
import ru.freeezzzi.yandex_test_task.testapplication.di.viewmodels.searchfragment.DaggerSearchFragmentViewModelComponent
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.ui.BaseFragment
import ru.freeezzzi.yandex_test_task.testapplication.ui.ViewState
import ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist.QuotesListAdapter

// TODO прятать клавиатуру при нажатии
// TODO сделать кнопки больше, а иконки оставить того же размера
// TODO переделать сообщения в snackbar
class SearchFragment : BaseFragment(R.layout.search_fragment) {
    private val binding by viewBinding(SearchFragmentBinding::bind)

    private var popularQueries: List<String> = listOf("Nvidia", "Apple", "Amazon", "Google", "Tesla", "Alibaba", "Facebook", "Visa")

    private val viewModel: SearchFragmentViewModel by viewModels(
            factoryProducer = { SearchFragmentViewModelFactory() }
    )

    private val quotesAllAdapter = QuotesListAdapter(
        clickListener = { viewModel.itemOnClickAction(it) },
        starClickListener = { viewModel.addToFavorites(it) }
    )

    private val quotesFavouritesAdapter = QuotesListAdapter(
        clickListener = { viewModel.itemOnClickAction(it) },
        starClickListener = { viewModel.addToFavorites(it) }
    )

    private val viewPagerAdapter = SearchViewPagerAdapter(
        allTabAdapter = quotesAllAdapter,
        favouritesAdapter = quotesFavouritesAdapter,
        popularQueries = popularQueries,
        recentQueries = emptyList(),
        refreshListener = {
            viewModel.searchAction(binding.searchBarEditText.text.toString())
        },
        scrollListener = this.OnVerticalScrollListener(),
            chipClickListener = {
                binding.searchBarEditText.setText(it)
                binding.searchBarEditText.clearFocus()
                performSearch()
            }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Открывается клавиатуру с фокусом на edittext в строке поиска
        binding.searchBarEditText.requestFocus()
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchBarEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun initViews(view: View) {
        super.initViews(view)

        viewModel.getRecentQueries()

        // cardview bindings
        binding.searchBarArrowIcon.setOnClickListener { onBackPressed() }
        binding.searchBarDeleteIcon.setOnClickListener {
            binding.searchBarEditText.setText("")
        }
        binding.searchBarEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0?.length ?: 0 > 0 && binding.searchBarDeleteIcon.visibility == View.INVISIBLE) {
                    binding.searchBarDeleteIcon.visibility = View.VISIBLE
                } else if (p0?.length ?: 0 == 0) {
                    binding.searchBarDeleteIcon.visibility = View.INVISIBLE
                }
            }
        })

        // ViewPager
        binding.searchViewpager.adapter = viewPagerAdapter
        binding.searchViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> setTextInTitles(binding.queriesTextview, binding.stocksTextview2, binding.favouritesTextview2)
                    1 -> setTextInTitles(binding.stocksTextview2, binding.favouritesTextview2, binding.queriesTextview)
                    2 -> setTextInTitles(binding.favouritesTextview2, binding.stocksTextview2, binding.queriesTextview)
                }
                super.onPageSelected(position)
            }
        })

        // set click listeners on labels
        binding.queriesTextview.setOnClickListener {
            binding.searchViewpager.setCurrentItem(0, true)
        }
        binding.stocksTextview2.setOnClickListener {
            binding.searchViewpager.setCurrentItem(1, true)
        }
        binding.favouritesTextview2.setOnClickListener {
            binding.searchViewpager.setCurrentItem(2, true)
        }
        binding.searchBarEditText.setOnKeyListener { view, i, keyEvent ->
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                    (i == KeyEvent.KEYCODE_ENTER)
            ) {
                // Perform action on key press
                    performSearch()
                    true
            }
            false
        }

        // observe
        viewModel.companies.observe(viewLifecycleOwner, this::updateAllAdapter)
        viewModel.tickersList.observe(viewLifecycleOwner, this::updateTickers)
        viewModel.localCompanies.observe(viewLifecycleOwner, this::updateFavouritesAdapter)
        viewModel.queriesList.observe(viewLifecycleOwner, this::updateQueries)
    }

    fun updateFavouritesAdapter(companies: ViewState<List<CompanyProfile>, String?>) {
        when (companies) {
            is ViewState.Success -> {
                quotesFavouritesAdapter.submitList(companies.result)
            }
            // is ViewState.Loading ->
            is ViewState.Error -> {
                quotesFavouritesAdapter.submitList(companies.oldvalue)
                showError(companies.result ?: "Couldn't load companies from local storage")
            }
        }
    }

    fun updateAllAdapter(companies: ViewState<List<CompanyProfile>, String?>) {
        when (companies) {
            is ViewState.Success -> {
                quotesAllAdapter.submitList(companies.result)
                viewPagerAdapter.setRefreshing(false)
            }
            is ViewState.Loading -> viewPagerAdapter.setRefreshing(true)
            is ViewState.Error -> {
                quotesAllAdapter.submitList(companies.oldvalue)
                showError(companies.result ?: "Couldn't load companies")
                viewPagerAdapter.setRefreshing(false)
            }
        }
    }

    fun updateTickers(tickers: ViewState<List<String>, String?>) {
        when (tickers) {
            is ViewState.Success -> {
                viewModel.clearCompaniesList()
                viewPagerAdapter.setRefreshing(false)
                viewModel.getCompanies(10) // В первый раз загружаем 10 компаний
            }
            is ViewState.Loading -> viewPagerAdapter.setRefreshing(true)
            is ViewState.Error -> {
                showError(tickers.result ?: "Couldn't load tickers")
                viewPagerAdapter.setRefreshing(false)
            }
        }
    }

    fun updateQueries(queries: ViewState<List<String>, String?>) {
        when (queries) {
            is ViewState.Success -> {
                viewPagerAdapter.submitQueries(queries.result)
            }
            is ViewState.Error -> {
                showError(queries.result ?: "Couldn't load recent queries")
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

    fun setTextInTitles(currentTextView: TextView, anotherTextView: TextView, anotherTextView2: TextView) {
        currentTextView.setTypeface(Typeface.DEFAULT_BOLD)
        currentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28F)

        anotherTextView.setTypeface(Typeface.DEFAULT)
        anotherTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18F)

        anotherTextView2.setTypeface(Typeface.DEFAULT)
        anotherTextView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18F)
    }

    fun performSearch() {
        hideKeybord()
        val symbol = binding.searchBarEditText.text.toString()
        if (symbol.isBlank()) {
            binding.searchBarEditText.setText("")
            return
        }
        viewModel.searchAction(symbol) // отправим запрос на сервер
        viewModel.showFavourites(symbol) // найдем такой тикер в favourites
        binding.searchViewpager.setCurrentItem(1, true) // переключим вкладку на список компаний
        viewModel.saveToRecentQueries(symbol)
    }
    private fun hideKeybord() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
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
            viewModel.getCompanies(7)
        }
    }

    override fun onBackPressed() {
        viewModel.exitFragment()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}

private class SearchFragmentViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DaggerSearchFragmentViewModelComponent.builder()
                .appComponent(App.appComponent)
                .build()
                .provideViewModel() as T
    }
}
