package ru.freeezzzi.yandex_test_task.testapplication.data.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.News
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class NewsDTO(
    /* News category. */
    @Json(name = "category")
    val category: String? = null,
    /* Published time in UNIX timestamp. */
    @Json(name = "datetime")
    val datetime: Long? = null,
    /* News headline. */
    @Json(name = "headline")
    val headline: String? = null,
    /* News ID. This value can be used for <code>minId</code> params to get the latest news only. */
    @Json(name = "id")
    val id: Long? = null,
    /* Thumbnail image URL. */
    @Json(name = "image")
    val image: String? = null,
    /* Related stocks and companies mentioned in the article. */
    @Json(name = "realted")
    val related: String? = null,
    /* News source. */
    @Json(name = "source")
    val source: String? = null,
    /* News summary. */
    @Json(name = "summary")
    val summary: String? = null,
    /* URL of the original article. */
    @Json(name = "url")
    val url: String? = null
) : Serializable {
    fun toNews(): News =
        News(
            category = category,
            datetime = datetime,
            headline = headline,
            id = id,
            image = image,
            related = related,
            source = source,
            summary = summary,
            url = url
        )
}
