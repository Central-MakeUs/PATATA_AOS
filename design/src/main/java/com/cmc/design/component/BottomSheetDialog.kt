package com.cmc.design.component


import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.cmc.design.R
import com.cmc.design.databinding.ContentSheetHandlebarBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.internal.managers.FragmentComponentManager

class BottomSheetDialog(
    private val context: Context,
    hasDim: Boolean = true,
) {

    private var dismissAfterClick = true
    private var btmDlg: BottomSheetDialog
    private var customView: View? = null

    var style: Int = if (hasDim) R.style.PatataBottomSheetDimStyle else R.style.PatataBottomSheetStyle

    init {
        btmDlg = BottomSheetDialog(context, style).apply {
            init()
        }
    }

    private fun BottomSheetDialog.init() {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        behavior.setPeekHeight(0, false)
    }

    val isShowing: Boolean
        get() {
            return this.btmDlg.isShowing
        }

    fun show() {
        if (isShowing) return
        this.btmDlg.show()
    }

    fun dismiss() {
        if (!isShowing) return
        this.btmDlg.dismiss()
    }

    fun setOutSideTouchable(activity: Activity): BottomSheetDialog {
        return this.btmDlg.apply {
            val outside = findViewById<View>(com.google.android.material.R.id.touch_outside)
            outside?.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_DOWN -> {
                        activity.dispatchTouchEvent(event)
                    }
                }
                false
            }
        }
    }

    fun <T : ViewBinding> bindBuilder(
        binding: T,
        hasHandle: Boolean = true,
        callback: T.(BottomSheetDialog) -> Unit,
    ): com.cmc.design.component.BottomSheetDialog {
        this.customView = binding.root
        if (hasHandle) {
            val root = ContentSheetHandlebarBinding.inflate(LayoutInflater.from(context)).root
            val view = root.findViewById<View>(R.id.handle_view)
            root.removeView(view)
            (binding.root.rootView as ViewGroup).addView(view, 0)
        }
        btmDlg.setContentView(this.customView!!)
        binding.callback(btmDlg)
        return this
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getScreenRealHeight() * 30 / 100
    }

    fun setCustomViewHeightMax() {
        val height = getScreenRealHeight()

        btmDlg.behavior.maxHeight = height
        this.customView?.layoutParams?.height = height
    }

    private fun getScreenRealHeight(): Int {
        val activity = FragmentComponentManager.findActivity(context) as Activity
        return activity
            .window.decorView.findViewById<ViewGroup>(android.R.id.content)
            .getChildAt(0).height
    }
}