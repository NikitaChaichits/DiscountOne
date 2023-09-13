package com.digeltech.discountone.util.view.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridOffsetDecoration(
    private val edgesOffset: Int,
    private val horizontalOffset: Int,
    private val verticalOffset: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> {
                makeGridSpacing(
                    outRect,
                    parent.getChildAdapterPosition(view),
                    state.itemCount,
                    layoutManager.spanCount,
                )
            }
        }
    }

    private fun makeGridSpacing(
        outRect: Rect,
        position: Int,
        itemCount: Int,
        spanCount: Int,
    ) {
        // Opposite of spanCount (find the list depth)
        val subsideCount = if (itemCount % spanCount == 0) {
            itemCount / spanCount
        } else {
            (itemCount / spanCount) + 1
        }

        // Grid position. Imagine all items ordered in x/y axis
        val xAxis = position % spanCount
        val yAxis = position / spanCount

        // Conditions in row and column
        val isFirstColumn = xAxis == 0
        val isFirstRow = yAxis == 0
        val isLastColumn = xAxis == spanCount - 1
        val isLastRow = yAxis == subsideCount - 1

        // Saved size
        val sizeBasedOnFirstColumn = if (isFirstColumn) edgesOffset else horizontalOffset / 2
        val sizeBasedOnLastColumn = if (isLastColumn) edgesOffset else horizontalOffset / 2
        val sizeBasedOnFirstRow = if (isFirstRow) edgesOffset / 2 else verticalOffset / 2
        val sizeBasedOnLastRow = if (isLastRow) edgesOffset else verticalOffset / 2

        with(outRect) {
            left = sizeBasedOnFirstColumn
            top = sizeBasedOnFirstRow
            right = sizeBasedOnLastColumn
            bottom = sizeBasedOnLastRow
        }
    }
}
