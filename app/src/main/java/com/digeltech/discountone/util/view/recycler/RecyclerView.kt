package com.digeltech.discountone.util.view.recycler

import android.graphics.Rect
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
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
    private var currentPosition: Int = 0


    fun startAutoScroll() {
        stopAutoScroll() // Stop any existing autoscroll

        currentPosition = 0
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(3000L)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.let {
                    val itemCount = layoutManager.itemCount

                    currentPosition++
                    if (currentPosition >= itemCount) {
                        currentPosition = 0
                    }

                    recyclerView.smoothScrollToPosition(currentPosition)
                }
            }
        }
    }

    fun stopAutoScroll() {
        job?.cancel()
        job = null
    }
}

class CyclicScrollHelper {
    fun enableCyclicScroll(recyclerView: RecyclerView) {
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
                    recyclerView.scrollToPosition(0)
                }
            }
        }
    }
}
