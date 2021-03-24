package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.freeezzzi.yandex_test_task.testapplication.App
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.databinding.CompanyProfileBinding
import ru.freeezzzi.yandex_test_task.testapplication.di.viewmodels.companyprofile.DaggerCompanyProfileViewModelComponent
import ru.freeezzzi.yandex_test_task.testapplication.ui.BaseFragment

// TODO переделать все imageView в imagebutton
class CompanyProfileFragment : BaseFragment(R.layout.company_profile) {
    private val binding by viewBinding(CompanyProfileBinding::bind)

    private val viewModel: CompanyProfileViewModel by viewModels(
        factoryProducer = { CompanyProfileViewModelFactory() })

    override fun initViews(view: View) {
        super.initViews(view)

        binding.profileStar.setOnClickListener {
            it.foreground = context?.getDrawable(R.drawable.ic_star_yellow_24px)
        }
        binding.profileArrowback.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        viewModel.exitFragment()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CompanyProfileFragment()
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
