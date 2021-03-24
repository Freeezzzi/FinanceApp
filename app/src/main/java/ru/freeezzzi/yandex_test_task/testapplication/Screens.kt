package ru.freeezzzi.yandex_test_task.testapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.Creator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.CompanyProfileFragment
import ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist.QuotesListFragment
import ru.freeezzzi.yandex_test_task.testapplication.ui.searchfragment.SearchFragment

object Screens {
    fun tradesListFragment(): FragmentScreen =
            FragmentScreen(
                    fragmentCreator = FragmentCreator(QuotesListFragment.newInstance())
            )

    fun searchFragment(): FragmentScreen =
            FragmentScreen(
                    fragmentCreator = FragmentCreator(SearchFragment.newInstance())
            )

    fun companyProfileFragment(companyProfile:CompanyProfile): FragmentScreen =
        FragmentScreen(
            fragmentCreator = FragmentCreator(CompanyProfileFragment.newInstance(companyProfile))
        )

    class FragmentCreator(private val fragment: Fragment) : Creator<FragmentFactory, Fragment> {
        override fun create(argument: FragmentFactory): Fragment =
                fragment
    }
}
