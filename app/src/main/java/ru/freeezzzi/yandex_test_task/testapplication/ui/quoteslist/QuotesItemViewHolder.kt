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
import ru.freeezzzi.yandex_test_task.testapplication.extensions.getCurrencySymbol

/**
 * Представляет краткую информацию о компании(во всех списках с компаниями)
 */
class QuotesItemViewHolder(
    private val binding: QuoteItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Для закругленяи краев картинки
    private val transformation: Transformation
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
            binding.root.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.light_blue))
        } else {
            binding.root.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.white))
        }
        setClickListeners(companyProfile, clickListener, starClickListener)
        setStar(companyProfile)
        setText(companyProfile)
        setPicture(companyProfile, isOdd)
    }

    private fun setStar(companyProfile: CompanyProfile) {
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

    private fun setText(companyProfile: CompanyProfile) {
        // set text about company
        binding.quoteItemCompanyName.text = companyProfile.name
        binding.quoteItemTicker.text = companyProfile.ticker
        binding.quoteItemPrice.text = String.format("${companyProfile.currency?.getCurrencySymbol()}%.2f", companyProfile.quote?.c ?: 0F)

        // В зависимости от изменения цены изменяем поле
        var priceChangeString = ""
        var priceChange = (companyProfile.quote?.c ?: 0.0F) - (companyProfile.quote?.pc ?: 0.0F)
        when {
            priceChange > 0 -> {
                binding.quoteItemPricechange.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
                priceChangeString += "+"
            }
            priceChange < 0 -> {
                binding.quoteItemPricechange.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                priceChangeString += "-"
                priceChange = -priceChange
            }
            else -> {
                binding.quoteItemPricechange.setTextColor(ContextCompat.getColor(itemView.context, R.color.font_black))
            }
        }
        // У некоторых компаний там 0, поэтмоу ставим в знаменатель единицу
        val percentPriceChange = priceChange / (if (companyProfile.quote?.pc ?: 1.0F == 0F) 1.0F else companyProfile.quote?.pc ?: 1.0F)*100

        priceChangeString += companyProfile.currency?.getCurrencySymbol()
        priceChangeString += "%.2f (%.2f%%)"
        binding.quoteItemPricechange.text = String.format(priceChangeString, priceChange, percentPriceChange)
    }

    private fun setClickListeners(
        companyProfile: CompanyProfile,
        clickListener: (CompanyProfile) -> Unit,
        starClickListener: (CompanyProfile) -> Unit
    ) {
        itemView.setOnClickListener { clickListener(companyProfile) }
        binding.quoteItemStar.setOnClickListener {
            if (!companyProfile.isFavorite) {
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

    private fun setPicture(companyProfile: CompanyProfile, isOdd : Boolean) {
        if (companyProfile.logo.isNullOrEmpty()) {
            binding.quoteItemImage.setImageResource(if (isOdd) R.color.light_blue else R.color.white)
        } else {
            Picasso.get().isLoggingEnabled = true
        Picasso.get()
            .load(companyProfile.logo)
            .placeholder(if (isOdd) R.color.light_blue else R.color.white)
            .error(if (isOdd) R.color.light_blue else R.color.white)
            .transform(transformation)
            .fit()
            .centerInside()
            // .centerCrop()
            .into(binding.quoteItemImage)
        }
    }
}
