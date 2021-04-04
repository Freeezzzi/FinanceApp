package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.newstab

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.databinding.NewsItemBinding
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.News
import java.util.concurrent.TimeUnit

class NewsItemViewHolder(
    private val binding: NewsItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(
        news: News,
        clickListener: (News) -> Unit
    ) {
        binding.newsCardview.setOnClickListener { clickListener(news) }
        setText(news)
        setPicture(news)
    }

    private fun setText(news: News) {
        binding.newsSource.text = news.source
        binding.newsSummary.text = news.summary
        binding.newsTitle.text = news.headline
        binding.newsTime.text = toTimeAgoFormat(System.currentTimeMillis()/1000 - (news.datetime ?: System.currentTimeMillis()))
    }

    private fun toTimeAgoFormat(time: Long): String {
        // Определим какие промежутки мы хотим отображать
        val times: List<Long> = listOf(
            TimeUnit.DAYS.toSeconds(365),
            TimeUnit.DAYS.toSeconds(30),
            TimeUnit.DAYS.toSeconds(7),
            TimeUnit.DAYS.toSeconds(1),
            TimeUnit.HOURS.toSeconds(1),
            TimeUnit.MINUTES.toSeconds(1),
            TimeUnit.SECONDS.toSeconds(1)
        )
        val timesString = binding.root.context.resources.getStringArray(R.array.news_ago_dimensions)
        val agoString = binding.root.context.getString(R.string.ago)
        val pluralEnding = binding.root.context.getString(R.string.plural_ending)

        val res = StringBuffer()

        for (i in times.indices) {
            val current = times[i]
            val temp = time / current
            if (temp> 0) {
                res.append("$temp ${timesString[i]}${if (temp > 1L) pluralEnding else ""} $agoString")
                break
            }
        }

        return res.toString()
    }

    private fun setPicture(news: News) {
        if (news.image.isNullOrEmpty()) {
            binding.newsImage.setImageResource(R.color.white)
        } else {
            Picasso.get().isLoggingEnabled = true
            Picasso.get()
                .load(news.image)
                .placeholder(R.color.white)
                .error(R.color.white)
                .fit()
                .centerInside()
                .into(binding.newsImage)
        }
    }
}
