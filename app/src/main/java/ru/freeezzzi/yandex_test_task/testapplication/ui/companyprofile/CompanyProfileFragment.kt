package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.freeezzzi.yandex_test_task.testapplication.App
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.databinding.CompanyProfileBinding
import ru.freeezzzi.yandex_test_task.testapplication.di.viewmodels.companyprofile.DaggerCompanyProfileViewModelComponent
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.StockCandle
import ru.freeezzzi.yandex_test_task.testapplication.ui.BaseFragment
import ru.freeezzzi.yandex_test_task.testapplication.ui.ViewState

// TODO переделать все imageView в imagebutton
class CompanyProfileFragment : BaseFragment(R.layout.company_profile) {
    private val binding by viewBinding(CompanyProfileBinding::bind)

    private val viewModel: CompanyProfileViewModel by viewModels(
        factoryProducer = { CompanyProfileViewModelFactory() })

    private val companyProfileAdapter = CompanyProfileViewPagerAdapter()

    override fun initViews(view: View) {
        super.initViews(view)

        viewModel.companyProfile = arguments?.getSerializable(COMPANY_PROFILE__KEY) as CompanyProfile

        // clicklisteners
        binding.profileStar.setOnClickListener {
            if (viewModel.companyProfile?.isFavorite == false) {
                binding.profileStar.foreground = context?.getDrawable(R.drawable.ic_star_yellow_24px)
            } else {
                binding.profileStar.foreground = context?.getDrawable(R.drawable.ic_star_outline_24px)
            }

            viewModel.addToFavorites()
        }
        binding.profileArrowback.setOnClickListener { onBackPressed() }

        binding.companyProfileViewpager.adapter = companyProfileAdapter

        // set text and star
        setText()
        setStar()

        var to: Long = System.currentTimeMillis() / 1000
        var oneMonth: Long = 60L * 60 * 24 * 30
        var from: Long = to - oneMonth
        viewModel.getStockCandle(resolution = CompanyProfileViewModel.DAY_RESOLUTION, from = from, to = to)

        viewModel.stockCandle.observe(viewLifecycleOwner, this::updateCandleData)
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

    private fun showError(msg: String) {
        var errorMsg = msg
        when (errorMsg) {
            "HTTP 429 " -> errorMsg = getString(R.string.limit_error)
            "Tickers loading.Try again!" -> return // В этом случае просто ждем
        }
        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT)
            .show()
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
