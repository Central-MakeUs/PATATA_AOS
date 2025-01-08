package com.cmc.patata

import android.view.View
import kotlin.math.abs


private var lastClickTime: Long = 0L
fun View.onDelayedClick(defaultTime: Int = 400, block: () -> Unit) {
    this.setOnClickListener {
        val timeStamp = System.currentTimeMillis()
        if (abs(timeStamp - lastClickTime) > defaultTime) {
            lastClickTime = timeStamp
            block()
        }
    }
}