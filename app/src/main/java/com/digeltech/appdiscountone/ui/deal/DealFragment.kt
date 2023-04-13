package com.digeltech.appdiscountone.ui.deal

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentDealBinding
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.util.copyTextToClipboard
import com.digeltech.appdiscountone.util.getDiscountText
import com.digeltech.appdiscountone.util.view.*

class DealFragment : BaseFragment(R.layout.fragment_deal) {

    private val binding by viewBinding(FragmentDealBinding::bind)
    override val viewModel: DealViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        initCoupon(viewModel.getDealData())
    }

    private fun observeData() = Unit

    private fun initCoupon(deal: Deal) {
        with(binding) {
            initListeners(deal)

            tvPrice.setStrikethrough(deal.priceCurrency + deal.oldPrice.toString())
            tvPriceWithDiscount.text = deal.priceCurrency + deal.discountPrice.toString()
            tvPublishedDate.text = deal.publishedDate
            tvDealName.text = deal.title

//            ivCouponCompanyLogo.setImageDrawable(ivCouponCompanyLogo.getImageDrawable(deal.companyLogo))
            tvCouponCompany.text = deal.companyName

            tvTimeValid.text = "Valid for 1 month"

            tvRate.text = deal.rating.toString()

            btnGetCoupon.text = getDiscountText(deal.oldPrice, deal.discountPrice)
            tvCouponText.text = deal.promocode

            tvMoreAboutDiscountText.text = deal.description
        }
    }

    private fun FragmentDealBinding.initListeners(coupon: Deal) {
        ivBack.setOnClickListener {
            navigateBack()
        }
        ivBookmark.setOnClickListener {
            if (coupon.isAddedToBookmark) {
                coupon.isAddedToBookmark = false
                ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark_deal))
                it.context.toast(it.getString(R.string.removed_from_bookmarks))
            } else {
                coupon.isAddedToBookmark = true
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