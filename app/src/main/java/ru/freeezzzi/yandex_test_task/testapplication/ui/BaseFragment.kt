package ru.freeezzzi.yandex_test_task.testapplication.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment(layoutResource: Int) : Fragment(layoutResource) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    open fun initViews(view: View) {}

    open fun onBackPressed() {}
}
