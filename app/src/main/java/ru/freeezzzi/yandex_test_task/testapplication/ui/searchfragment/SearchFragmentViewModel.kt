package ru.freeezzzi.yandex_test_task.testapplication.ui.searchfragment

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import javax.inject.Inject

class SearchFragmentViewModel @Inject constructor(
        private val router: Router,
        val database: FavoriteCompaniesDatabase
) : ViewModel() {

    fun exitFragment() {
        router.exit()
    }
}