package com.digeltech.discountone.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentHomeBinding
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.ui.common.KEY_USER
import com.digeltech.discountone.ui.common.adapter.GridDealAdapter
import com.digeltech.discountone.ui.common.adapter.LinearDealAdapter
import com.digeltech.discountone.ui.common.getShopIdByName
import com.digeltech.discountone.ui.home.adapter.BannerAdapter
import com.digeltech.discountone.ui.home.adapter.SubcategoriesAdapter
import com.digeltech.discountone.util.logSearch
import com.digeltech.discountone.util.view.*
import com.digeltech.discountone.util.view.recycler.AutoScrollHelper
import com.digeltech.discountone.util.view.recycler.CyclicScrollHelper
import com.digeltech.discountone.util.view.recycler.GridOffsetDecoration
import com.facebook.appevents.AppEventsLogger
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    override val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var logger: AppEventsLogger

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var bestDealsAdapter: LinearDealAdapter
    private lateinit var categoriesAdapter: SubcategoriesAdapter
    private lateinit var searchDealAdapter: GridDealAdapter
    private lateinit var autoScrollHelper: AutoScrollHelper
    private lateinit var cyclicScrollHelper: CyclicScrollHelper

//    private lateinit var categoryPaginator: CategoryPaginator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getHomepageData()
        loadProfileImage()

        initAdapters()
        initListeners()
        observeData()

        binding.ivLoading.loadGif()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            binding.homeGroup.visible()
            binding.tvSearchResultEmpty.invisible()
            binding.rvSearchDeals.invisible()
        } else {
            logSearch(newText.toString(), logger)
            viewModel.searchDeals(newText.toString())
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        autoScrollHelper.stopAutoScroll()
    }

    private fun initListeners() {
        binding.ivProfile.setOnClickListener {
            if (prefs.isLogin()) {
                navigate(R.id.profileFragment)
            } else {
                navigate(R.id.startFragment)
            }
        }
        binding.searchView.apply {
            setOnClickListener { onActionViewExpanded() }
            setOnQueryTextListener(this@HomeFragment)
            queryHint = getString(R.string.search_by_deals)
        }
        binding.tvMoreBestDeals.setOnClickListener {
            navigate(R.id.dealsFragment)
        }
    }

    private fun initAdapters() {
        // scrolling horizontal RV for banners
        bannerAdapter = BannerAdapter {
            navigate(
                HomeFragmentDirections.toCategoryFragment(
                    id = getShopIdByName(it.shopName),
                    title = it.shopName,
                    slug = it.shopSlug,
                    isFromCategory = false
                )
            )
        }
        binding.rvBanners.adapter = bannerAdapter

        autoScrollHelper = AutoScrollHelper(binding.rvBanners)
        cyclicScrollHelper = CyclicScrollHelper()
        cyclicScrollHelper.enableCyclicScroll(binding.rvBanners)
        autoScrollHelper.startAutoScroll()

        // Linear horizontal RV for best deals
        bestDealsAdapter = LinearDealAdapter(
            onClickListener = {
//                viewModel.updateDealViewsClick(it.id.toString())
                navigate(HomeFragmentDirections.toDealFragment(it))
            },
            onBookmarkClickListener = {
                viewModel.updateBookmark(it.toString())
            },
            fragmentManager = requireActivity().supportFragmentManager,
            logger = logger,
        )
        binding.rvBestDeals.adapter = bestDealsAdapter

        // Linear vertical RV for Categories with subcategories with Linear horizontal RV for deals
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        categoriesAdapter = SubcategoriesAdapter(
            onMoreDealsClick = {
                navigate(
                    HomeFragmentDirections.toCategoryFragment(
                        id = it.id,
                        title = it.name,
                        slug = it.slug,
                    )
                )
            },
            onDealClick = {
                navigate(HomeFragmentDirections.toDealFragment(it))
            },
            onBookmarkClickListener = {
                viewModel.updateBookmark(it.toString())
            },
            fragmentManager = requireActivity().supportFragmentManager,
            logger = logger,
        )
        binding.rvCategories.apply {
            adapter = categoriesAdapter
            this.layoutManager = layoutManager
//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    super.onScrollStateChanged(recyclerView, newState)
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
//                        val totalItemCount = layoutManager.itemCount
//
//                        if (lastVisibleItemPosition == totalItemCount - 1) {
//                            if (categoryPaginator.hasNextPage()) {
//                                val nextPage = categoryPaginator.getNextPage()
//                                categoriesAdapter.submitList(categoriesAdapter.currentList + nextPage)
//                            } else if (categoryPaginator.hasLastPage()) {
//                                val lastPage = categoryPaginator.getLastPage()
//                                categoriesAdapter.submitList(categoriesAdapter.currentList + lastPage)
//                            }
//                        }
//                    }
//                }
//            })
        }

        //Grid RV for searching results
        searchDealAdapter = GridDealAdapter(
            onClickListener = {
                navigate(HomeFragmentDirections.toDealFragment(it))
            },
            onBookmarkClickListener = {
                viewModel.updateBookmark(it.toString())
            },
            fragmentManager = requireActivity().supportFragmentManager,
            logger = logger,
        )
        binding.rvSearchDeals.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )
        binding.rvSearchDeals.adapter = searchDealAdapter
    }

    private fun loadProfileImage() {
        Hawk.get<User>(KEY_USER)?.let {
            it.avatarUrl?.let { url ->
                binding.ivProfile.setProfileImage(url)
            }
        }
    }

    private fun observeData() {
        viewModel.banners.observe(viewLifecycleOwner, bannerAdapter::submitList)
        viewModel.loadingGifVisibility.observe(viewLifecycleOwner) {
            if (it)
                binding.ivLoading.visible()
            else
                binding.ivLoading.invisible()
        }
        viewModel.bestDeals.observe(viewLifecycleOwner) {
            binding.bestDealsGroup.visible()
            bestDealsAdapter.submitList(it)
        }
        viewModel.categories.observe(viewLifecycleOwner) {
//            categoryPaginator = CategoryPaginator(it)
//            categoriesAdapter.submitList(categoryPaginator.getNextPage())
            categoriesAdapter.submitList(it)
        }
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (binding.searchView.query.isNullOrEmpty()) {
                binding.homeGroup.visible()
                binding.bestDealsGroup.visible()
                binding.tvSearchResultEmpty.invisible()
                binding.rvSearchDeals.invisible()
            } else if (it.isEmpty() && !binding.searchView.query.isNullOrEmpty()) {
                binding.tvSearchResultEmpty.visible()
                binding.homeGroup.invisible()
                binding.bestDealsGroup.invisible()
                binding.rvSearchDeals.invisible()
            } else {
                binding.tvSearchResultEmpty.invisible()
                binding.homeGroup.gone()
                binding.bestDealsGroup.gone()
                binding.rvSearchDeals.visible()
            }
            searchDealAdapter.submitList(it)
            binding.ivLoading.invisible()
        }
    }
}