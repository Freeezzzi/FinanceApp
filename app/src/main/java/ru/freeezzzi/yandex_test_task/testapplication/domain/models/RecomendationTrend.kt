package ru.freeezzzi.yandex_test_task.testapplication.domain.models

data class RecommendationTrend(
    /* Company symbol. */
    val symbol: String? = null,
    /* Number of recommendations that fall into the Buy category */
    val buy: Double? = null,
    /* Number of recommendations that fall into the Hold category */
    val hold: Double? = null,
    /* Updated period */
    val period: String? = null,
    /* Number of recommendations that fall into the Sell category */
    val sell: Double? = null,
    /* Number of recommendations that fall into the Strong Buy category */
    val strongBuy: Double? = null,
    /* Number of recommendations that fall into the Strong Sell category */
    val strongSell: Double? = null
)
