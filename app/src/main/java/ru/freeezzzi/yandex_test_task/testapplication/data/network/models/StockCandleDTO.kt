package ru.freeezzzi.yandex_test_task.testapplication.data.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.StockCandle
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class StockCandleDTO(
    /* List of open prices for returned candles. */
    @Json(name = "o")
    val o: List<Float>? = null,
    /* List of high prices for returned candles. */
    @Json(name = "h")
    val h: List<Float>? = null,
    /* List of low prices for returned candles. */
    @Json(name = "l")
    val l: List<Float>? = null,
    /* List of close prices for returned candles. */
    @Json(name = "c")
    val c: List<Float>? = null,
    /* List of volume data for returned candles. */
    @Json(name = "v")
    val v: List<Float>? = null,
    /* List of timestamp for returned candles. */
    @Json(name = "t")
    val t: List<Long>? = null,
    /* Status of the response. This field can either be ok or no_data. */
    @Json(name = "s")
    val s: String? = null
) : Serializable {
    fun toStockCandle(): StockCandle =
        StockCandle(
            o = o,
            h = h,
            l = l,
            c = c,
            v = v,
            t = t,
            s = s
        )
}
