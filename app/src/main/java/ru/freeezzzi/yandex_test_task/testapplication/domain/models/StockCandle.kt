package ru.freeezzzi.yandex_test_task.testapplication.domain.models

data class StockCandle(
    /* List of open prices for returned candles. */
    val o: List<Float>? = null,
    /* List of high prices for returned candles. */
    val h: List<Float>? = null,
    /* List of low prices for returned candles. */
    val l: List<Float>? = null,
    /* List of close prices for returned candles. */
    val c: List<Float>? = null,
    /* List of volume data for returned candles. */
    val v: List<Float>? = null,
    /* List of timestamp for returned candles. */
    val t: List<Long>? = null,
    /* Status of the response. This field can either be ok or no_data. */
    val s: String? = null
)