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
import com.digeltech.appdiscountone.data.source.remote.KEY_SHOPS
import com.digeltech.appdiscountone.databinding.FragmentDealBinding
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.ui.common.*
import com.digeltech.appdiscountone.ui.common.adapter.LinearDealAdapter
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.util.capitalizeFirstLetter
import com.digeltech.appdiscountone.util.copyTextToClipboard
import com.digeltech.appdiscountone.util.isNotNullAndNotEmpty
import com.digeltech.appdiscountone.util.view.*
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DealFragment : BaseFragment(R.layout.fragment_deal) {

    private val binding by viewBinding(FragmentDealBinding::bind)
    override val viewModel: DealViewModel by viewModels()

    private val args: DealFragmentArgs by navArgs()

    private lateinit var dealAdapter: LinearDealAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCoupon(args.deal)
        initAdapter()
        observeData()
    }

    private fun initAdapter() {
        dealAdapter = LinearDealAdapter {
            initCoupon(it)
            binding.scrollView.scrollTo(0, 0)
        }
        binding.rvDeals.adapter = dealAdapter
    }

    private fun initCoupon(deal: DealParcelable) {
        with(binding) {
            logOpenDeal(deal.title)
            initListeners(deal)
            viewModel.getSimilarDeals(deal.categoryId, deal.id)

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


            deal.shopImageUrl?.let { ivCouponCompanyLogo.setImageWithRadius(it, R.dimen.radius_10) }
            if (deal.shopName.isNotEmpty()) {
                tvCouponCompany.text = deal.shopName.capitalizeFirstLetter()
            }

            if (deal.validDate.isNotEmpty()) {
                tvTimeValid.text = deal.validDate
                ivTimeValid.visible()
                tvTimeValid.visible()
            }

            tvRate.text = deal.rating.toString()

            if (deal.promocode.isNotEmpty()) {
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
        ivBookmark.setOnClickListener {
            if (deal.isAddedToBookmark) {
                deal.isAddedToBookmark = false
                removeFromBookmark(deal.id)
                ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark_deal))
                it.context.toast(it.getString(R.string.removed_from_bookmarks))
            } else {
                deal.isAddedToBookmark = true
                addToBookmark(deal)
                ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark_deal_solid))
                it.context.toast(it.getString(R.string.added_to_bookmarks))
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
            it.openLink(deal.link)
            logShopNow(name = deal.title, url = deal.link)
        }
        btnCopy.setOnClickListener {
            copyTextToClipboard(it.context, deal.title)
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

    private fun getShopIdByName(name: String): Int {
        val listOfShops: List<Shop> = Hawk.get(KEY_SHOPS)
        return listOfShops.find {
            it.name.equals(name, true)
        }?.id ?: 0
    }
}