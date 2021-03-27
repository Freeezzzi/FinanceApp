package ru.freeezzzi.yandex_test_task.testapplication.extensions

import android.content.Context
import android.content.res.Resources
import android.icu.util.Currency
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.util.*

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun String.getCurrencySymbol(): String {
    val currency = Currency.getInstance(this)
    return currency.getSymbol(Locale.US)
}
