package ru.freeezzzi.yandex_test_task.testapplication.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import ru.freeezzzi.yandex_test_task.testapplication.R

abstract class BaseFragment(layoutResource: Int) : Fragment(layoutResource) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    open fun initViews(view: View) {}

    open fun onBackPressed() {}

    open fun showError(msg: String) {
        var errorMsg = msg
        when (errorMsg) {
            "HTTP 429 " -> errorMsg = getString(R.string.limit_error)
            "Tickers loading.Try again!" -> return // В этом случае просто ждем
            "HTTP 403 " -> errorMsg = getString(R.string.access_denied)
        }
        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT)
            .show()
    }
}
