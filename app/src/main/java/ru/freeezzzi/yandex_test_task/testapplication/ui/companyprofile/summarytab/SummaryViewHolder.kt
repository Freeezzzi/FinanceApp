package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.summarytab

import android.view.View
import android.widget.TextView
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.extensions.getCurrencySymbol
import ru.freeezzzi.yandex_test_task.testapplication.ui.tabs.ViewPagerViewHodler

class SummaryViewHolder(itemView: View) : ViewPagerViewHodler(itemView) {
    private val industry: TextView = itemView.findViewById(R.id.summary_industry_value)
    private val ipo: TextView = itemView.findViewById(R.id.summary_ipo_value)
    private val capitalization: TextView = itemView.findViewById(R.id.summary_marketcapitalization_value)
    private val sharedOutstanding: TextView = itemView.findViewById(R.id.summary_sharedoutstanding_value)
    private val webUrl: TextView = itemView.findViewById(R.id.summary_weburl_value)
    private val phone: TextView = itemView.findViewById(R.id.summary_phone_value)

    fun onBind() {}

    fun setData(companyProfile: CompanyProfile) {
        companyProfile.let {
            industry.text = it.finnhubIndustry
            ipo.text = it.ipo
            capitalization.text = "${it.currency?.getCurrencySymbol()}${it.marketCapitalization.toString()}"
            sharedOutstanding.text = it.shareOutstanding.toString()
            webUrl.text = it.weburl
            phone.text = it.phone
        }
    }
}
