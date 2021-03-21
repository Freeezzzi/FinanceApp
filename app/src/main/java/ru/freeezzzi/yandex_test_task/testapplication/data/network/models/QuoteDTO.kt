package ru.freeezzzi.yandex_test_task.testapplication.data.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.Quote
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class QuoteDTO (
    /* Open price of the day */
    @Json(name = "o")
    val o: Float? = null,
    /* High price of the day */
    @Json(name = "h")
    val h: Float? = null,
    /* Low price of the day */
    @Json(name = "l")
    val l: Float? = null,
    /* Current price */
    @Json(name = "c")
    val c: Float? = null,
    /* Previous close price */
    @Json(name = "pc")
    val pc: Float? = null
) : Serializable{
    fun toQuote() : Quote =
        Quote(
            o = o,
            h = h,
            l = l,
            c = c,
            pc = pc
        )
}