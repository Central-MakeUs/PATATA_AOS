package com.cmc.common.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        if (position != RecyclerView.NO_POSITION) {
            outRect.right = space // 기본 간격 추가

            // 마지막 아이템이면 오른쪽 간격 제거 (기본적으로 추가되므로 다시 제거)
            if (position == itemCount - 1) {
                outRect.right = 0
            }
        }
    }
}
