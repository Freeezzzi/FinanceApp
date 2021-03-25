package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.freeezzzi.yandex_test_task.testapplication.App
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.databinding.CompanyProfileFragmentBinding
import ru.freeezzzi.yandex_test_task.testapplication.di.viewmodels.companyprofile.DaggerCompanyProfileViewModelComponent
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.News
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.StockCandle
import ru.freeezzzi.yandex_test_task.testapplication.ui.BaseFragment
import ru.freeezzzi.yandex_test_task.testapplication.ui.ViewState

// TODO переделать все imageView в imagebutton
class CompanyProfileFragment : BaseFragment(R.layout.company_profile_fragment) {
    private val binding by viewBinding(CompanyProfileFragmentBinding::bind)

    private val viewModel: CompanyProfileViewModel by viewModels(
        factoryProducer = { CompanyProfileViewModelFactory() })

    private val companyProfileAdapter = CompanyProfileViewPagerAdapter(
        getCandleListener = { resolution, from, to -> viewModel.getStockCandle(
            resolution = resolution,
            from = from,
            to = to
        ) },
        newsClickListener = { viewModel.newsClickedAction(it) },
        getNewsListener = { from, to -> viewModel.getNews(from, to) }
    )

    override fun initViews(view: View) {
        super.initViews(view)

        viewModel.companyProfile = arguments?.getSerializable(COMPANY_PROFILE__KEY) as CompanyProfile

        // clicklisteners
        setClickListeners()

        // set viewpager settings
        binding.companyProfileViewpager.adapter = companyProfileAdapter
        binding.companyProfileViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        setTextInTitles(binding.chartTextview, binding.summaryTextview, binding.newsTextview, binding.forecastsTextview)
                        binding.horizontalScrollView.smoothScrollTo(0, 0)
                    }
                    1 -> {
                        setTextInTitles(binding.summaryTextview, binding.chartTextview, binding.newsTextview, binding.forecastsTextview)
                        binding.horizontalScrollView.smoothScrollTo(binding.chartTextview.left, 0)
                    }
                    2 -> {
                        setTextInTitles(binding.newsTextview, binding.chartTextview, binding.summaryTextview, binding.forecastsTextview)
                        binding.horizontalScrollView.smoothScrollTo(binding.summaryTextview.left, 0)
                    }
                    3 -> {
                        setTextInTitles(binding.forecastsTextview, binding.chartTextview, binding.summaryTextview, binding.newsTextview)
                        binding.horizontalScrollView.smoothScrollTo(binding.newsTextview.left, 0)
                    }
                }
                super.onPageSelected(position)
            }
        })

        // set text and star
        setText()
        setStar()

        var to: Long = System.currentTimeMillis() / 1000
        var oneMonth: Long = 60L * 60 * 24 * 30
        var from: Long = to - oneMonth
        viewModel.getStockCandle(
            resolution = CompanyProfileViewModel.DAY_RESOLUTION,
            from = from,
            to = to
        )
        viewModel.getNews("2021-02-22", "2021-03-22")

        viewModel.stockCandle.observe(viewLifecycleOwner, this::updateCandleData)
        viewModel.newsList.observe(viewLifecycleOwner, this::updateNewsList)
    }

    fun updateCandleData(candle: ViewState<StockCandle, String?>) {
        when (candle) {
            is ViewState.Success -> {
                companyProfileAdapter.setCandleData(candle.result)
            }
            is ViewState.Error -> {
                showError(candle.result ?: "Couldn't load candle data")
            }
        }
    }

    fun updateNewsList(news: ViewState<List<News>, String?>) {
        when (news) {
            is ViewState.Success -> {
                companyProfileAdapter.setNews(news.result)
            }
            is ViewState.Error -> {
                companyProfileAdapter.setNews(news.oldvalue)
                showError(news.result ?: "Couldn't load candle data")
            }
        }
    }

    private fun setTextInTitles(currentTextView: TextView, anotherTextView: TextView, anotherTextView2: TextView, anotherTextView3: TextView) {
        currentTextView.setTypeface(Typeface.DEFAULT_BOLD)
        anotherTextView.setTypeface(Typeface.DEFAULT)
        anotherTextView2.setTypeface(Typeface.DEFAULT)
        anotherTextView3.setTypeface(Typeface.DEFAULT)
    }

    private fun setClickListeners() {
        binding.profileStar.setOnClickListener {
            if (viewModel.companyProfile?.isFavorite == false) {
                binding.profileStar.foreground = context?.getDrawable(R.drawable.ic_star_yellow_24px)
            } else {
                binding.profileStar.foreground = context?.getDrawable(R.drawable.ic_star_outline_24px)
            }

            viewModel.addToFavorites()
        }
        binding.profileArrowback.setOnClickListener { onBackPressed() }

        // set click listeners on titles
        binding.chartTextview.setOnClickListener {
            binding.companyProfileViewpager.setCurrentItem(0, true)
            binding.horizontalScrollView.scrollTo(binding.chartTextview.left, 0)
        }
        binding.summaryTextview.setOnClickListener {
            binding.companyProfileViewpager.setCurrentItem(1, true)
            binding.horizontalScrollView.scrollTo(binding.summaryTextview.left, 0)
        }
        binding.newsTextview.setOnClickListener {
            binding.companyProfileViewpager.setCurrentItem(2, true)
            binding.horizontalScrollView.scrollTo(binding.newsTextview.left, 0)
        }
        binding.forecastsTextview.setOnClickListener {
            binding.companyProfileViewpager.setCurrentItem(3, true)
            binding.horizontalScrollView.scrollTo(binding.forecastsTextview.left, 0)
        }
    }

    private fun setText() {
        binding.profileTicker.setText(viewModel.companyProfile?.ticker)
        binding.profileCompanyname.setText(viewModel.companyProfile?.name)
    }

    private fun setStar() {
        if (viewModel.companyProfile?.isFavorite == true) {
            binding.profileStar.foreground = context?.getDrawable(R.drawable.ic_star_yellow_24px)
        } else {
            binding.profileStar.foreground = context?.getDrawable(R.drawable.ic_star_outline_24px)
        }
    }

    override fun onBackPressed() {
        viewModel.exitFragment()
    }

    companion object {

        private const val COMPANY_PROFILE__KEY = "company_profile"
        @JvmStatic
        fun newInstance(companyProfile: CompanyProfile): CompanyProfileFragment {
            val fragment = CompanyProfileFragment()
            val bundle = Bundle()
            bundle.putSerializable(COMPANY_PROFILE__KEY, companyProfile)
            fragment.arguments = bundle
            return fragment
        }
    }
}

private class CompanyProfileViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DaggerCompanyProfileViewModelComponent.builder()
            .appComponent(App.appComponent)
            .build()
            .provideViewModel() as T
    }
}
