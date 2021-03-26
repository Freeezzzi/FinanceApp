package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.newstab

import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.databinding.NewsItemBinding
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.News
import java.util.*
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

    fun setText(news: News) {
        binding.newsSource.text = news.source
        binding.newsSummary.text = news.summary
        binding.newsTitle.text = news.headline
        binding.newsTime.text = toTimeAgoFormat(System.currentTimeMillis()/1000 - (news.datetime ?: System.currentTimeMillis()))
    }

    private fun toTimeAgoFormat(time: Long): String {
        // Определим какие промежутки мы хотим отображать
        val times: List<Long> = Arrays.asList(
            TimeUnit.DAYS.toSeconds(365),
            TimeUnit.DAYS.toSeconds(30),
            TimeUnit.DAYS.toSeconds(7),
            TimeUnit.DAYS.toSeconds(1),
            TimeUnit.HOURS.toSeconds(1),
            TimeUnit.MINUTES.toSeconds(1),
            TimeUnit.SECONDS.toSeconds(1)
        )
        val timesString = Arrays.asList("year", "month", "week", "day", "hour", "minute", "second")

        var res = StringBuffer()

        for (i in 0..(times.size - 1)) {
            val current = times.get(i)
            val temp = time / current
            if (temp> 0) {
                res.append(temp).append(" ").append(timesString.get(i)).append(if (temp > 1L) "s " else " ").append("ago")
                break
            }
        }

        if ("".equals(res.toString())) {
            return "0 seconds ago"
        } else {
            return res.toString()
        }
    }

    fun setPicture(news: News) {
        if (news.image.isNullOrEmpty()) {
            binding.newsImage.setImageResource(R.drawable.ic_launcher_background)
        } else {
            Picasso.get().isLoggingEnabled = true
            Picasso.get()
                .load(news.image)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_round_star_24)
                .fit()
                .centerInside()
                // .centerCrop()
                .into(binding.newsImage)
        }
    }
}
