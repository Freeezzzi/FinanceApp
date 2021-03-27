package ru.freeezzzi.yandex_test_task.testapplication.ui.quoteslist

import android.content.res.ColorStateList
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import ru.freeezzzi.yandex_test_task.testapplication.R
import ru.freeezzzi.yandex_test_task.testapplication.databinding.QuoteItemBinding
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile

class QuotesItemViewHolder(
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
        val theme = itemView.context.theme
        val typedValue = TypedValue()
        if (isOdd) {
            theme.resolveAttribute(R.attr.colorPrimaryVariant, typedValue, false)
            binding.root.setCardBackgroundColor(itemView.resources.getColor(typedValue.data, theme))
        } else {
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, false)
            binding.root.setCardBackgroundColor(itemView.resources.getColor(typedValue.data, theme))
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
        } else {
            binding.quoteItemPricechange.setTextColor(ContextCompat.getColor(itemView.context, R.color.font_black))
        }
        // У некоторых компаний там 0, поэтмоу ставим в знаменатель единицу
        val percentPriceChange = priceChange / (if (companyProfile.quote?.pc ?: 1.0F == 0F) 1.0F else companyProfile.quote?.pc ?: 1.0F)

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
