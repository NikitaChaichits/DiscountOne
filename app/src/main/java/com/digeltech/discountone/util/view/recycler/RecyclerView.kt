package com.digeltech.discountone.util.view.recycler

import android.content.Context
import android.graphics.Rect
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

@Px
fun LinearLayoutManager.getChildBottom(childIndex: Int, defaultValue: Int): Int {
    val lastChild = getChildAt(childIndex)
    return lastChild?.let {
        val lastChildBounds = Rect()
        getDecoratedBoundsWithMargins(it, lastChildBounds)
        lastChildBounds.bottom
    } ?: defaultValue
}

class AutoScrollHelper(private val recyclerView: RecyclerView) {
    private var job: Job? = null
    private var shopsJob: Job? = null
    private var currentPosition: Int = 0
    private var currentShopPosition: Int = 0

    fun startAutoScroll() {
        currentPosition = 0
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(3000L)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.let {
                    val itemCount = it.itemCount

                    currentPosition++
                    if (currentPosition >= itemCount) {
                        currentPosition = 0
                    }

                    recyclerView.smoothScrollToPosition(currentPosition)
                }
            }
        }
    }

    fun startShopsAutoScroll() {
        shopsJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(3000L)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager

                layoutManager?.let {
                    val itemCount = it.itemCount

                    val smoothScroller = SmoothScroller(recyclerView.context, itemCount * 1500)
                    smoothScroller.targetPosition = itemCount
                    recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
                }
            }
        }
    }


    fun stopAutoScroll() {
        job?.cancel()
        job = null
        shopsJob?.cancel()
        shopsJob = null
    }
}

class CyclicScrollHelper {
    fun enableBannerCyclicScroll(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
        layoutManager?.let {
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(recyclerView)

            recyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
                val snapPosition = snapHelper.findSnapView(layoutManager)?.let {
                    layoutManager.getPosition(it)
                } ?: RecyclerView.NO_POSITION
                val itemCount = layoutManager.itemCount

                if (snapPosition == itemCount) {
                    recyclerView.smoothScrollToPosition(0)
                }
            }
        }
    }

    fun enableShopCyclicScroll(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
        layoutManager?.let {
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(recyclerView)

            recyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
                val snapPosition = snapHelper.findSnapView(layoutManager)?.let {
                    layoutManager.getPosition(it)
                } ?: RecyclerView.NO_POSITION
                val itemCount = layoutManager.itemCount

                if (snapPosition + 3 == itemCount) {
                    recyclerView.smoothScrollToPosition(0)
                }
            }
        }
    }
}

class SmoothScroller(context: Context, private val durationMillis: Int) : LinearSmoothScroller(context) {
    override fun calculateTimeForScrolling(dx: Int): Int {
        return super.calculateTimeForScrolling(dx).coerceAtLeast(durationMillis)
    }

    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_START
    }
}