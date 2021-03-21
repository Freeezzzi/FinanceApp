package ru.freeezzzi.yandex_test_task.testapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.Creator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist.QuotesListFragment

object Screens {
    fun tradesListFragment() : FragmentScreen =
            FragmentScreen(
                    fragmentCreator = FragmentCreator(QuotesListFragment.newInstance())
            )

    class FragmentCreator(private val fragment: Fragment) : Creator<FragmentFactory, Fragment> {
        override fun create(argument: FragmentFactory): Fragment =
                fragment
    }
}