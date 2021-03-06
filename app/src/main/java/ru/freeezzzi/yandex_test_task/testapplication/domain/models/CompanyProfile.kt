package ru.freeezzzi.yandex_test_task.testapplication.domain.models

import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.CompanyProfileEntity
import java.io.Serializable

/**
 *
 * @param country Country of company's headquarter.
 * @param currency Currency used in company filings.
 * @param exchange Listed exchange.
 * @param name Company name.
 * @param ticker Company symbol/ticker as used on the listed exchange.
 * @param ipo IPO date.
 * @param marketCapitalization Market Capitalization.
 * @param shareOutstanding Number of oustanding shares.
 * @param logo Logo image.
 * @param phone Company phone number.
 * @param weburl Company website.
 * @param finnhubIndustry Finnhub industry classification.
 * @param isFavorite Is user add company to favorites list
 */

data class CompanyProfile(
    val country: String? = null,
    val currency: String? = null,
    val exchange: String? = null,
    val name: String? = null,
    val ticker: String? = null,
    val ipo: String? = null,
    val marketCapitalization: Float? = null,
    val shareOutstanding: Float? = null,
    val logo: String? = null,
    val phone: String? = null,
    val weburl: String? = null,
    val finnhubIndustry: String? = null,
    var isFavorite: Boolean = false,
    @Transient var quote: Quote? = null
) : Serializable{

        override fun equals(other: Any?): Boolean{
                if (other == null) return false;
                if (other !is CompanyProfile) return false
                if (this.ticker == other.ticker) return true
                return false
        }

        fun toCompanyProfileEntity(): CompanyProfileEntity =
                CompanyProfileEntity(
                        currency = currency,
                        name = name,
                        ticker = ticker!!,
                        logo = logo,
                        isFavorite = isFavorite,
                        currentPrice = (quote?.c ?: 0F),
                        previousPrice = (quote?.pc ?: 0F),
                        ipo = ipo,
                        marketCapitalization = marketCapitalization,
                        shareOutstanding = shareOutstanding,
                        phone = phone,
                        weburl = weburl,
                        finnhubIndustry = finnhubIndustry,
                )
}
