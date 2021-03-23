package ru.freeezzzi.yandex_test_task.testapplication.data.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class SymbolLookupDTO(
    /* Index's symbol. */
    @Json(name = "count")
    val count: Int? = null,
    /* Array of constituents. */
    @Json(name = "result")
    val result: List<Result?>? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "displayName")
    val displayName: String? = null,
    @Json(name = "symbol")
    val symbol: String? = null,
    @Json(name = "type")
    val type: String? = null
) : Serializable
