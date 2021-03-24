package ru.freeezzzi.yandex_test_task.testapplication.domain.models

import com.squareup.moshi.Json
import java.io.Serializable

/**
 *
 * @param o Open price of the day
 * @param h High price of the day
 * @param l Low price of the day
 * @param c Current price
 * @param pc Previous close price
 */

data class Quote (
    /* Open price of the day */
    val o: Float? = null,
    /* High price of the day */
    val h: Float? = null,
    /* Low price of the day */
    val l: Float? = null,
    /* Current price */
    val c: Float? = null,
    /* Previous close price */
    val pc: Float? = null
) : Serializable