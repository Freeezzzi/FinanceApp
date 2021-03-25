package ru.freeezzzi.yandex_test_task.testapplication.domain.models

data class News(
    /* News category. */
    val category: String? = null,
    /* Published time in UNIX timestamp. */
    val datetime: Long? = null,
    /* News headline. */
    val headline: String? = null,
    /* News ID. This value can be used for <code>minId</code> params to get the latest news only. */
    val id: Long? = null,
    /* Thumbnail image URL. */
    val image: String? = null,
    /* Related stocks and companies mentioned in the article. */
    val related: String? = null,
    /* News source. */
    val source: String? = null,
    /* News summary. */
    val summary: String? = null,
    /* URL of the original article. */
    val url: String? = null
)
