package com.example.playlistmaker.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,     // 8dp
    private val edgeSpacing: Int  // 16dp
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        // ЛЕВЫЙ ОТСТУП
        outRect.left = if (column == 0) {
            edgeSpacing
        } else {
            spacing / 2
        }

        // ПРАВЫЙ ОТСТУП
        outRect.right = if (column == spanCount - 1) {
            edgeSpacing
        } else {
            spacing / 2
        }

        // ВЕРХ (между строками)
        if (position >= spanCount) {
            outRect.top = spacing
        }

        outRect.bottom = 0
    }
}

