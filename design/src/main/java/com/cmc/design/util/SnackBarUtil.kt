package com.cmc.design.util

import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.cmc.design.R
import com.google.android.material.snackbar.Snackbar

object SnackBarUtil {
    fun show(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        val snackbar = Snackbar.make(view, message, duration).apply {
            setBackgroundTint(ContextCompat.getColor(view.context, R.color.gray_100))
            setTextColor(ContextCompat.getColor(view.context, R.color.blue_20))
        }

        val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textView.gravity = Gravity.CENTER

        snackbar.show()
    }
}