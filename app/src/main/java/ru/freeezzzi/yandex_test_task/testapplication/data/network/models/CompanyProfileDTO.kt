package ru.freeezzzi.yandex_test_task.testapplication.data.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
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
 */
@JsonClass(generateAdapter = true)
data class CompanyProfileDTO (
    /* Country of company's headquarter. */
    @Json(name = "country")
    val country: kotlin.String? = null,
    /* Currency used in company filings. */
    @Json(name = "currency")
    val currency: kotlin.String? = null,
    /* Listed exchange. */
    @Json(name = "exchange")
    val exchange: kotlin.String? = null,
    /* Company name. */
    @Json(name = "name")
    val name: kotlin.String? = null,
    /* Company symbol/ticker as used on the listed exchange. */
    @Json(name = "ticker")
    val ticker: kotlin.String? = null,
    /* IPO date. */
    @Json(name = "ipo")
    val ipo: kotlin.String? = null,
    /* Market Capitalization. */
    @Json(name = "marketCapitalization")
    val marketCapitalization: kotlin.Float? = null,
    /* Number of oustanding shares. */
    @Json(name = "shareOutstanding")
    val shareOutstanding: kotlin.Float? = null,
    /* Logo image. */
    @Json(name = "logo")
    val logo: kotlin.String? = null,
    /* Company phone number. */
    @Json(name = "phone")
    val phone: kotlin.String? = null,
    /* Company website. */
    @Json(name = "weburl")
    val weburl: kotlin.String? = null,
    /* Finnhub industry classification. */
    @Json(name = "finnhubIndustry")
    val finnhubIndustry: kotlin.String? = null
) : Serializable {

    fun toCompanyProfile(): CompanyProfile =
        CompanyProfile(
            country = country,
            currency = currency,
            exchange = exchange,
            name = name,
            ticker = ticker,
            ipo = ipo,
            marketCapitalization = marketCapitalization,
            shareOutstanding = shareOutstanding,
            logo = logo,
            phone = phone,
            weburl = weburl,
            finnhubIndustry = finnhubIndustry
        )
}
