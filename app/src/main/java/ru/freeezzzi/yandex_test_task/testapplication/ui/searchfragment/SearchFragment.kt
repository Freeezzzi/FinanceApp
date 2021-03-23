package ru.freeezzzi.yandex_test_task.testapplication.ui.searchfragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.freeezzzi.yandex_test_task.testapplication.App
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.databinding.SearchFragmentBinding
import ru.freeezzzi.yandex_test_task.testapplication.di.viewmodels.searchfragment.DaggerSearchFragmentViewModelComponent
import ru.freeezzzi.yandex_test_task.testapplication.ui.BaseFragment

class SearchFragment : BaseFragment(R.layout.search_fragment) {
    private val binding by viewBinding(SearchFragmentBinding::bind)

    private val viewModel : SearchFragmentViewModel by viewModels(
            factoryProducer = {SearchFragmentViewModelFactory()}
    )

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