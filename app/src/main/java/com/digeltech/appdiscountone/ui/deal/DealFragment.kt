package com.digeltech.appdiscountone.ui.deal

import android.os.Bundle
import android.view.View
import androidx.core.text.parseAsHtml
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentDealBinding
import com.digeltech.appdiscountone.ui.common.adapter.DealAdapter
import com.digeltech.appdiscountone.ui.common.addToBookmark
import com.digeltech.appdiscountone.ui.common.isAddedToBookmark
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.removeFromBookmark
import com.digeltech.appdiscountone.util.capitalizeFirstLetter
import com.digeltech.appdiscountone.util.copyTextToClipboard
import com.digeltech.appdiscountone.util.isNotNullAndNotEmpty
import com.digeltech.appdiscountone.util.view.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DealFragment : BaseFragment(R.layout.fragment_deal) {

    private val binding by viewBinding(FragmentDealBinding::bind)
    override val viewModel: DealViewModel by viewModels()

    private val args: DealFragmentArgs by navArgs()

    private lateinit var dealAdapter: DealAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCoupon(args.deal)
        initAdapter()
        observeData()
    }

    private fun initAdapter() {
        dealAdapter = DealAdapter {
            initCoupon(it)
            binding.scrollView.scrollTo(0, 0)
        }
        binding.rvDeals.adapter = dealAdapter
    }

    private fun initCoupon(deal: DealParcelable) {
        with(binding) {
            initListeners(deal)

            // categoryId=0 только в случае когда был переход с HomeFragment по нажатию на баннер
            if (deal.categoryId != 0) viewModel.getSimilarDeals(deal.categoryId, deal.id)

            deal.imageUrl?.let(ivDealImage::loadImage)

            deal.isAddedToBookmark = isAddedToBookmark(deal.id)

            if (deal.isAddedToBookmark) {
                ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_bookmark_deal_solid))
            } else {
                ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_bookmark_deal))
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
            if (deal.shopName.isNotEmpty()) {
                tvCouponCompany.text = deal.shopName.capitalizeFirstLetter()
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
        tvMoreDeals.setOnClickListener {
            navigate(R.id.categoriesFragment)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.similarDeals.collect {
                if (it.isNotEmpty()) {
                    dealAdapter.submitList(it)
                    binding.grSimilarProducts.visible()
                }
            }
        }
    }
}