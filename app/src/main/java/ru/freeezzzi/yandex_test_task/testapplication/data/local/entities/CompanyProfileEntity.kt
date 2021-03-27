package ru.freeezzzi.yandex_test_task.testapplication.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.Quote

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

@Entity(tableName = "Favorite_companies")
data class CompanyProfileEntity(
    @ColumnInfo(name = "currency") val currency: String?,
    @ColumnInfo(name = "name") val name: String?,
    @PrimaryKey @ColumnInfo(name = "ticker") val ticker: String = "",
    @ColumnInfo(name = "logo") val logo: String?,
    @ColumnInfo(name = "isFavorite") val isFavorite: Boolean,
    @ColumnInfo(name = "currentPrice") val currentPrice: Float,
    @ColumnInfo(name = "previousPrice") val previousPrice: Float,
)

fun CompanyProfileEntity.toCompanyProfile(): CompanyProfile =
    CompanyProfile(
        currency = currency,
        name = name,
        ticker = ticker,
        logo = logo,
        isFavorite = isFavorite,
        quote = Quote(
            c = currentPrice,
            pc = previousPrice
        )
    )

/*
@ColumnInfo(name = "ipo") val ipo: String?,
@ColumnInfo(name = "marketCapitalization") val marketCapitalization: Float?,
@ColumnInfo(name = "shareOutstanding") val shareOutstanding: Float?,
@ColumnInfo(name = "phone") val phone: String?,
@ColumnInfo(name = "weburl") val weburl: String?,
@ColumnInfo(name = "finnhubIndustry") val finnhubIndustry: String?,
ipo = ipo,
marketCapitalization = marketCapitalization,
shareOutstanding = shareOutstanding,
phone = phone,
weburl = weburl,
finnhubIndustry = finnhubIndustry,*/
