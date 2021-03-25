package ru.freeezzzi.yandex_test_task.testapplication.ui.companyprofile.newstab

import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.databinding.NewsItemBinding
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.News

class NewsItemViewHolder(
    private val binding: NewsItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(
        news: News,
        clickListener: (News) -> Unit
    ) {
        binding.newsCardview.setOnClickListener { clickListener(news)}
        setText(news)
        setPicture(news)
    }

    fun setText(news: News) {
        binding.newsSource.text = news.source
        binding.newsSummary.text = news.summary
        // TODO set time
        binding.newsTitle.text = news.headline
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
