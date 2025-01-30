package com.cmc.common.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpaceItemDecoration(private val spanCount: Int, private val space: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        val column = position % spanCount

        // 첫 번째 행에는 위쪽 여백을 주지 않음
        if (position >= spanCount) {
            outRect.top = space
        }

        // 좌우 여백 조정 (양쪽 끝을 맞추기 위해)
        val halfSpace = space / 2
        outRect.left = if (column == 0) 0 else halfSpace
        outRect.right = if (column == spanCount - 1) 0 else halfSpace

    }

}