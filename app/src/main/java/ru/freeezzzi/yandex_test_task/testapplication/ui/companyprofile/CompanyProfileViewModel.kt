package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository
import javax.inject.Inject

class CompanyProfileViewModel @Inject constructor(
    private val router: Router,
    private val companiesRepository: CompaniesRepository,
    private val database: FavoriteCompaniesDatabase
) : ViewModel() {

    fun exitFragment() {
        router.exit()
    }
}
