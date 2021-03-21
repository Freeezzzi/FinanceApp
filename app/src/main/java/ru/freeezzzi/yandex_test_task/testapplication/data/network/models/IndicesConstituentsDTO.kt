package ru.freeezzzi.yandex_test_task.testapplication.data.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class IndicesConstituentsDTO(
        /* Index's symbol. */
        @Json(name = "symbol")
        val symbol: String? = null,
        /* Array of constituents. */
        @Json(name = "constituents")
        val constituents: List<String>? = null
) : Serializable
