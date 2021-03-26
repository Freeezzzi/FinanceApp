package ru.freeezzzi.yandex_test_task.testapplication.data.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.RecommendationTrend
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class RecomendationTrendDTO(
    /* Company symbol. */
    @Json(name = "symbol")
    val symbol: String? = null,
    /* Number of recommendations that fall into the Buy category */
    @Json(name = "buy")
    val buy: Double? = null,
    /* Number of recommendations that fall into the Hold category */
    @Json(name = "hold")
    val hold: Double? = null,
    /* Updated period */
    @Json(name = "period")
    val period: String? = null,
    /* Number of recommendations that fall into the Sell category */
    @Json(name = "sell")
    val sell: Double? = null,
    /* Number of recommendations that fall into the Strong Buy category */
    @Json(name = "strongBuy")
    val strongBuy: Double? = null,
    /* Number of recommendations that fall into the Strong Sell category */
    @Json(name = "strongSell")
    val strongSell: Double? = null
) : Serializable {
    fun toRecomendationTrend(): RecommendationTrend =
        RecommendationTrend(
            symbol = symbol,
            buy = buy,
            hold = hold,
            period = period,
            sell = sell,
            strongBuy = strongBuy,
            strongSell = strongSell
        )
}
