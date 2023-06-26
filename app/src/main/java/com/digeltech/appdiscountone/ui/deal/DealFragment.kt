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
import com.digeltech.appdiscountone.ui.common.*
import com.digeltech.appdiscountone.ui.common.adapter.LinearDealAdapter
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.util.capitalizeFirstLetter
import com.digeltech.appdiscountone.util.copyTextToClipboard
import com.digeltech.appdiscountone.util.isNotNullAndNotEmpty
import com.digeltech.appdiscountone.util.view.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DealFragment : BaseFragment(R.layout.fragment_deal) {

    private val binding by viewBinding(FragmentDealBinding::bind)
    override val viewModel: DealViewModel by viewModels()

    private val args: DealFragmentArgs by navArgs()

    private lateinit var categoryDealsAdapter: LinearDealAdapter
    private lateinit var shopDealsAdapter: LinearDealAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCoupon(args.deal)
        initAdapters()
        observeData()

//        binding.scrollView.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
//            override fun onSwipeLeft() {
//                navigateBack()
//            }
//
//            override fun onSwipeRight() {
//                navigateBack()
//            }
//        })
    }

    private fun initAdapters() {
        categoryDealsAdapter = LinearDealAdapter {
            initCoupon(it)
            binding.scrollView.scrollTo(0, 0)
        }
        binding.rvSimilarCategoryDeals.adapter = categoryDealsAdapter

        shopDealsAdapter = LinearDealAdapter {
            initCoupon(it)
            binding.scrollView.scrollTo(0, 0)
        }
        binding.rvSimilarShopDeals.adapter = shopDealsAdapter
    }

    private fun initCoupon(deal: DealParcelable) {
        with(binding) {
            logOpenDeal(deal.title)
            initListeners(deal)
            viewModel.getSimilarDealsByCategory(deal.categoryId, deal.id)
            viewModel.getSimilarDealsByShop(deal.shopName, deal.id)

            deal.imageUrl.let(ivDealImage::loadImage)

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
                tvPriceWithDiscount.text = deal.priceCurrency + deal.price
            }
            tvPublishedDate.text = getString(R.string.fr_deal_published, deal.publishedDate)
            tvDealName.text = deal.title


            deal.shopImageUrl.let { ivCouponCompanyLogo.setImageWithRadius(it, R.dimen.radius_10) }
            if (deal.shopName.isNotEmpty()) {
                tvCouponCompany.text = deal.shopName.capitalizeFirstLetter()
            }

            if (deal.expirationDate.isNotNullAndNotEmpty()) {
                tvTimeValid.text = deal.expirationDate
                ivTimeValid.visible()
                tvTimeValid.visible()
            }

            tvRate.text = deal.rating.toString()

            if (deal.promocode.isNotNullAndNotEmpty()) {
                tvCouponText.text = deal.promocode
                btnCopy.visible()
            } else {
                btnCopy.gone()
            }

            if (deal.description.isNotEmpty()) {
                tvMoreAboutDiscountText.text = deal.description.parseAsHtml()
            } else {
                tvMoreAboutDiscount.gone()
                tvMoreAboutDiscountText.gone()
            }
        }
    }

    private fun FragmentDealBinding.initListeners(deal: DealParcelable) {
        ivBack.setOnClickListener {
            navigateBack()
        }
        ivShare.setOnClickListener {
            val shareText = getString(R.string.share_text, deal.webLink)
            it.shareText(shareText)
        }
        ivBookmark.setOnClickListener {
            if (deal.isAddedToBookmark) {
                deal.isAddedToBookmark = false
                removeFromBookmark(deal.id)
                ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark_deal))
                it.context.toast(it.getString(R.string.removed_from_bookmarks))
            } else {
                if (!prefs.isLogin()) {
                    it.context.toast(R.string.toast_bookmark)
                } else {
                    deal.isAddedToBookmark = true
                    addToBookmark(deal)
                    ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark_deal_solid))
                    it.context.toast(it.getString(R.string.added_to_bookmarks))
                }
            }
        }
        ivCouponCompanyLogo.setOnClickListener {
            navigate(DealFragmentDirections.toShopFragment(getShopIdByName(deal.shopName), deal.shopName))
        }
        tvCouponCompany.setOnClickListener {
            navigate(DealFragmentDirections.toShopFragment(getShopIdByName(deal.shopName), deal.shopName))
        }
        ivRateArrowUp.setOnClickListener {
            ivRateArrowUp.setColorFilter(it.getColorValue(R.color.green))
            ivRateArrowDown.setColorFilter(it.getColorValue(R.color.grey))
            tvRate.text = deal.rating.inc().toString()
        }
        ivRateArrowDown.setOnClickListener {
            ivRateArrowDown.setColorFilter(it.getColorValue(R.color.red))
            ivRateArrowUp.setColorFilter(it.getColorValue(R.color.grey))
            tvRate.text = deal.rating.dec().toString()
        }
        btnGetDeal.setOnClickListener {
            it.openLink(deal.shopLink)
            logShopNow(name = deal.title, url = deal.shopLink)
        }
        btnCopy.setOnClickListener {
            copyTextToClipboard(it.context, deal.title)
            it.context.toast(it.getString(R.string.copied))
        }

    }

    private fun observeData() {
        viewModel.similarCategoryDeals.observe(viewLifecycleOwner) {
            val categoryName = getCategoryNameById(args.deal.categoryId)
            binding.tvSimilarCategoryDeals.text = getString(R.string.fr_deal_similar_deals, categoryName)
            categoryDealsAdapter.submitList(it)
            binding.grSimilarCategoryDeals.visible()
            binding.tvMoreCategoryDeals.setOnClickListener {
                navigate(
                    DealFragmentDirections.toCategoryFragment(
                        id = args.deal.categoryId, title = categoryName, isFromCategory = true
                    )
                )
            }
        }
        viewModel.similarShopDeals.observe(viewLifecycleOwner) {
            binding.tvSimilarShopDeals.text = getString(R.string.fr_deal_similar_deals, args.deal.shopName)
            shopDealsAdapter.submitList(it)
            binding.grSimilarShopDeals.visible()
            binding.tvMoreShopDeals.setOnClickListener {
                navigate(
                    DealFragmentDirections.toShopFragment(
                        id = getShopIdByName(args.deal.shopName), title = args.deal.shopName, isFromCategory = false
                    )
                )
            }
        }
    }
}