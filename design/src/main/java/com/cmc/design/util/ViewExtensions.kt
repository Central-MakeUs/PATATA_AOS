package com.cmc.design.util

import android.view.View

fun View.animateClickEffect() {
    this.animate()
        .scaleX(0.8f) // 0.8배 축소
        .scaleY(0.8f)
        .setDuration(100)
        .withEndAction {
            this.animate()
                .scaleX(1.1f) // 1.1배 확대
                .scaleY(1.1f)
                .setDuration(100)
                .withEndAction {
                    this.animate()
                        .scaleX(1.0f) // 원래 크기
                        .scaleY(1.0f)
                        .setDuration(100)
                        .start()
                }
                .start()
        }
        .start()
}