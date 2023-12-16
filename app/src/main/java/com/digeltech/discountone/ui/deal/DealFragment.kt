package com.digeltech.discountone.ui.deal

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentDealBinding
import com.digeltech.discountone.ui.common.*
import com.digeltech.discountone.ui.common.adapter.LinearDealAdapter
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.util.*
import com.digeltech.discountone.util.time.formatDate
import com.digeltech.discountone.util.view.*
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DealFragment : BaseFragment(R.layout.fragment_deal) {

    private val binding by viewBinding(FragmentDealBinding::bind)
    override val viewModel: DealViewModel by viewModels()

    private val args: DealFragmentArgs by navArgs()

    private lateinit var categoryDealsAdapter: LinearDealAdapter
    private lateinit var shopDealsAdapter: LinearDealAdapter

    @Inject
    lateinit var logger: AppEventsLogger

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        observeData()

        if (args.deal != null) {
            initCoupon(args.deal!!)
        } else {
            viewModel.getDeal(args.dealId)
        }

        onBackPressed()
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.isVisible) {
                    binding.grContent.visible()
                    binding.webView.invisible()
                } else {
                    navigateBack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initAdapters() {
        categoryDealsAdapter = LinearDealAdapter(
            onClickListener = {
                initCoupon(it)
                binding.scrollView.scrollTo(0, 0)
            },
            onBookmarkClickListener = {
                viewModel.updateBookmark(it.toString())
            },
            fragmentManager = requireActivity().supportFragmentManager,
            logger = logger,
        )
        binding.rvSimilarCategoryDeals.adapter = categoryDealsAdapter

        shopDealsAdapter = LinearDealAdapter(
            onClickListener = {
                initCoupon(it)
                binding.scrollView.scrollTo(0, 0)
            },
            onBookmarkClickListener = {
                viewModel.updateBookmark(it.toString())
            },
            fragmentManager = requireActivity().supportFragmentManager,
            logger = logger,
        )
        binding.rvSimilarShopDeals.adapter = shopDealsAdapter
    }

    private fun initCoupon(deal: DealParcelable) {
        with(binding) {
            logOpenDeal(
                name = deal.title,
                shopName = deal.shopName,
                categoryName = getCategoryNameById(deal.categoryId),
                price = deal.price.toString(),
                className = "DealFragment",
                logger
            )

            initListeners(deal)
            viewModel.getSimilarDealsByCategory(deal.categoryId, deal.id)
            viewModel.getSimilarDealsByShop(deal.shopName, deal.id)

            scrollView.visible()
            if (!deal.imagesUrl.isNullOrEmpty()) {
                val listOfDealImages = mutableListOf<String>()
                listOfDealImages.addAll(deal.imagesUrl!!)

                val adapter = ImageSliderAdapter(requireContext(), listOfDealImages)
                vpImages.adapter = adapter
                if (listOfDealImages.size > 1)
                    dots.setupWithViewPager(vpImages)
                else dots.invisible()
            } else {
                deal.imageUrl.let(ivDealImage::loadImage)
                ivDealImage.visible()
                vpImages.invisible()
                dots.invisible()
            }

            deal.isAddedToBookmark = isAddedToBookmark(deal.id)

            if (deal.isAddedToBookmark) {
                ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_wishlist_on))
                tvWishlist.text = getText(R.string.fr_deal_remove_from_bookmarks)
            } else {
                ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_wishlist_colored))
                tvWishlist.text = getText(R.string.fr_deal_add_to_bookmarks)
            }

            if (deal.sale.isNotNullAndNotEmpty() && deal.sale != "0") {
                tvPrice.text = deal.sale
                tvPriceWithDiscount.gone()
            } else {
                tvPrice.setStrikethrough(deal.priceCurrency + deal.oldPrice)
                tvPriceWithDiscount.text = getDiscountText(
                    price = deal.oldPrice?.toDouble() ?: 0.0,
                    discountPrice = deal.price?.toDouble() ?: 0.0,
                    saleSize = deal.saleSize,
                )
            }

            if (deal.expirationDate.isNotNullAndNotEmpty()) {
                tvExpirationDate.text = deal.expirationDate
                tvExpirationDate.visible()
            } else {
                tvPublishedDate.text = getString(R.string.fr_deal_published, formatDate(deal.lastUpdateDate))
                tvPublishedDate.visible()
            }

            tvDealName.text = deal.title

            deal.shopImageUrl.let { ivCouponCompanyLogo.setImageWithRadius(it, R.dimen.radius_10) }
            if (deal.shopName.isNotEmpty()) {
                tvCouponCompany.text = deal.shopName.capitalizeFirstLetter()
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
            addToBookmark(deal, it)
        }
        tvWishlist.setOnClickListener {
            addToBookmark(deal, it)
        }
        ivCouponCompanyLogo.setOnClickListener {
            navigate(
                DealFragmentDirections.toShopFragment(
                    id = getShopIdByName(deal.shopName),
                    title = deal.shopName,
                    slug = deal.shopSlug,
                )
            )
        }
        tvCouponCompany.setOnClickListener {
            navigate(
                DealFragmentDirections.toShopFragment(
                    id = getShopIdByName(deal.shopName),
                    title = deal.shopName,
                    slug = deal.shopSlug,
                )
            )
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
            viewModel.updateDealViewsClick(deal.id.toString())
            logShopNow(
                name = deal.title,
                url = deal.shopLink,
                shopName = deal.shopName,
                categoryName = getCategoryNameById(deal.categoryId),
                price = deal.price.toString(),
                className = "DealFragment",
                logger
            )
            it.openLink(deal.shopLink + "?app=1")
        }
        btnCopy.setOnClickListener {
            copyTextToClipboard(it.context, deal.title)
            it.context.toast(it.getString(R.string.copied))
        }

    }

    private fun FragmentDealBinding.addToBookmark(
        deal: DealParcelable,
        it: View
    ) {
        if (deal.isAddedToBookmark) {
            deal.isAddedToBookmark = false
            removeFromBookmarkCache(deal.id)
            viewModel.updateBookmark(deal.id.toString())
            ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_wishlist_colored))
            tvWishlist.text = getText(R.string.fr_deal_add_to_bookmarks)
            it.context.toast(it.getString(R.string.removed_from_bookmarks))
        } else {
            if (!prefs.isLogin()) {
                val dialogFragment = WishlistDialogFragment()
                dialogFragment.show(requireActivity().supportFragmentManager, dialogFragment.tag)
            } else {
                deal.isAddedToBookmark = true
                viewModel.updateBookmark(deal.id.toString())
                addToBookmarkCache(deal)
                ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_wishlist_on))
                tvWishlist.text = getText(R.string.fr_deal_remove_from_bookmarks)
                it.context.toast(it.getString(R.string.added_to_bookmarks))
            }
        }
    }

    private fun observeData() {
        viewModel.similarCategoryDeals.observe(viewLifecycleOwner) {
            categoryDealsAdapter.submitList(it)
            binding.grSimilarCategoryDeals.visible()
//            binding.tvMoreCategoryDeals.setOnClickListener {
//                navigate(
//                    DealFragmentDirections.toCategoryFragment(
//                        id = args.deal.categoryId, title = categoryName, isFromCategory = true
//                    )
//                )
//            }
        }
        viewModel.similarShopDeals.observe(viewLifecycleOwner) {
            shopDealsAdapter.submitList(it)
            val firstDeal = it.first()
            binding.grSimilarShopDeals.visible()
            binding.tvSimilarShopName.text = firstDeal.shopName
            binding.tvSimilarShopName.setOnClickListener {
                navigate(
                    DealFragmentDirections.toShopFragment(
                        id = getShopIdByName(firstDeal.shopName),
                        title = firstDeal.shopName,
                        slug = firstDeal.shopSlug,
                        isFromCategory = false
                    )
                )
            }
        }
        viewModel.deal.observe(viewLifecycleOwner, ::initCoupon)
        viewModel.loadingDealError.observe(viewLifecycleOwner) {
            MaterialDialog(requireContext())
                .lifecycleOwner(viewLifecycleOwner)
                .message(text = "Deal loading error. Please try later.")
                .cornerRadius(res = R.dimen.radius_12)
                .cancelOnTouchOutside(cancelable = true)
                .onDismiss { navigate(R.id.homeFragment) }
                .show()
        }
    }
}