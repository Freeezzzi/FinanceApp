package ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.databinding.QuoteItemBinding
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile

class QuotesListViewHolder(
    private val binding: QuoteItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Для закругленяи краев картинки
    val transformation: Transformation
    init {
        val dimension = itemView.resources.getDimension(R.dimen.corner_rad)
        val cornerRadius = dimension.toInt()
        transformation = RoundedCornersTransformation(cornerRadius, 0)
    }

    fun bind(
        companyProfile: CompanyProfile,
        clickListener: (CompanyProfile) -> Unit,
        starClickListener: (CompanyProfile) -> Unit,
        isOdd: Boolean
    ) {
        if (isOdd) {
            binding.root.setCardBackgroundColor(itemView.resources.getColor(R.color.light_blue))
        } else {
            binding.root.setCardBackgroundColor(itemView.resources.getColor(R.color.white))
        }
        setClickListeners(companyProfile, clickListener, starClickListener)
        setStar(companyProfile)
        setText(companyProfile)
        setPicture(companyProfile)
    }

    fun setStar(companyProfile: CompanyProfile) {
        // set star if company in favorites list
        if (companyProfile.isFavorite) {
            ImageViewCompat.setImageTintList(
                binding.quoteItemStar,
                ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.Yellow))
            )
        } else {
            ImageViewCompat.setImageTintList(
                binding.quoteItemStar,
                ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.Grey))
            )
        }
    }

    fun setText(companyProfile: CompanyProfile) {
        // set text about company
        binding.quoteItemCompanyName.text = companyProfile.name
        binding.quoteItemTicker.text = companyProfile.ticker
        binding.quoteItemPrice.text = String.format("${when (companyProfile.currency){
            "USD" -> "$ "
            "EUR" -> "€ "
            "RUB" -> "₽ "
            else -> "$ "
        }}%.2f", companyProfile.quote?.c ?: 0F)

        // В зависимости от изменения цены изменяем поле
        var priceChangeString = ""
        var priceChange = (companyProfile.quote?.c ?: 0.0F) - (companyProfile.quote?.pc ?: 0.0F)
        if (priceChange > 0) {
            binding.quoteItemPricechange.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
            priceChangeString += "+"
        } else if (priceChange < 0) {
            binding.quoteItemPricechange.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            priceChangeString += "-"
            priceChange = -priceChange
        }
        val percentPriceChange = priceChange / (companyProfile.quote?.pc ?: 1.0F)

        when (companyProfile.currency) {
            "USD" -> priceChangeString += "$ "
            "EUR" -> priceChangeString += "€ "
            "RUB" -> priceChangeString += "₽ "
        }
        priceChangeString += "%.2f (%.2f%%)"
        binding.quoteItemPricechange.text = String.format(priceChangeString, priceChange, percentPriceChange)
    }

    fun setClickListeners(
        companyProfile: CompanyProfile,
        clickListener: (CompanyProfile) -> Unit,
        starClickListener: (CompanyProfile) -> Unit
    ) {
        // set clicklisteners
        itemView.setOnClickListener { clickListener(companyProfile) }
        binding.quoteItemStar.setOnClickListener {
            if (!companyProfile.isFavorite) { // если сейчас не в favorites то стави значок
                ImageViewCompat.setImageTintList(
                    binding.quoteItemStar,
                    ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.Yellow))
                )
            } else {
                ImageViewCompat.setImageTintList(
                    binding.quoteItemStar,
                    ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.Grey))
                )
            }
            starClickListener(companyProfile)
        }
    }

    fun setPicture(companyProfile: CompanyProfile) {
        if (companyProfile.logo.isNullOrEmpty()) {
            binding.quoteItemImage.setImageResource(R.drawable.ic_launcher_background)
        } else {
            Picasso.get().isLoggingEnabled = true
        Picasso.get()
            .load(companyProfile.logo)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_round_star_24)
            .transform(transformation)
            .fit()
            .centerInside()
            // .centerCrop()
            .into(binding.quoteItemImage)
        }
    }
}
