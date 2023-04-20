package com.digeltech.appdiscountone.ui.deal

import android.os.Bundle
import android.view.View
import androidx.core.text.parseAsHtml
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentDealBinding
import com.digeltech.appdiscountone.ui.common.addToBookmark
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.removeFromBookmark
import com.digeltech.appdiscountone.util.capitalizeFirstLetter
import com.digeltech.appdiscountone.util.copyTextToClipboard
import com.digeltech.appdiscountone.util.isNotNullAndNotEmpty
import com.digeltech.appdiscountone.util.view.*

class DealFragment : BaseFragment(R.layout.fragment_deal) {

    private val binding by viewBinding(FragmentDealBinding::bind)
    override val viewModel: DealViewModel by viewModels()

    private val args: DealFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        initCoupon(args.deal)
    }

    private fun observeData() = Unit

    private fun initCoupon(deal: DealParcelable) {
        with(binding) {
            initListeners(deal)

            deal.imageUrl?.let(ivDealImage::loadImage)
            if (deal.isAddedToBookmark) {
                ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_bookmark_deal_solid))
            }

            if (deal.sale.isNotNullAndNotEmpty()) {
                tvPrice.text = deal.sale
                tvPriceWithDiscount.gone()
            } else {
                tvPrice.setStrikethrough(deal.priceCurrency + deal.oldPrice)
                tvPriceWithDiscount.text = deal.priceCurrency + deal.discountPrice
            }
            tvPublishedDate.text = getString(R.string.fr_deal_published, deal.publishedDate)
            tvDealName.text = deal.title

//            ivCouponCompanyLogo.setImageDrawable(ivCouponCompanyLogo.getImageDrawable(deal.companyLogo))
            if (deal.companyName.isNotEmpty()) {
                tvCouponCompany.text = deal.companyName.capitalizeFirstLetter()
            }

            if (deal.validDate.isNotEmpty()) {
                tvTimeValid.text = deal.validDate
                ivTimeValid.visible()
                tvTimeValid.visible()
            }

            tvRate.text = deal.rating.toString()

            btnGetDeal.setOnClickListener { it.openLink(deal.link) }

            if (deal.promocode.isNotEmpty()) {
                tvCouponText.text = deal.promocode
                btnCopy.visible()
            } else {
                btnCopy.gone()
            }

            tvMoreAboutDiscountText.text = deal.description.parseAsHtml()
        }
    }

    private fun FragmentDealBinding.initListeners(coupon: DealParcelable) {
        ivBack.setOnClickListener {
            navigateBack()
        }
        ivBookmark.setOnClickListener {
            if (coupon.isAddedToBookmark) {
                coupon.isAddedToBookmark = false
                removeFromBookmark(coupon.id)
                ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark_deal))
                it.context.toast(it.getString(R.string.removed_from_bookmarks))
            } else {
                coupon.isAddedToBookmark = true
                addToBookmark(coupon)
                ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark_deal_solid))
                it.context.toast(it.getString(R.string.added_to_bookmarks))
            }
        }
        ivRateArrowUp.setOnClickListener {
            ivRateArrowUp.setColorFilter(it.getColorValue(R.color.green))
            ivRateArrowDown.setColorFilter(it.getColorValue(R.color.grey))
            tvRate.text = coupon.rating.inc().toString()
        }
        ivRateArrowDown.setOnClickListener {
            ivRateArrowDown.setColorFilter(it.getColorValue(R.color.red))
            ivRateArrowUp.setColorFilter(it.getColorValue(R.color.grey))
            tvRate.text = coupon.rating.dec().toString()
        }
        btnCopy.setOnClickListener {
            copyTextToClipboard(it.context, coupon.title)
            it.context.toast(it.getString(R.string.copied))
        }
    }
}