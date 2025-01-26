package com.cmc.design.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.cmc.design.R

class PatataEditTextHelperView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val textView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_patata_edit_text_helper, this, true)
        textView = findViewById(R.id.tv_helper)

        initText()
    }

    private fun initText() {
        with(textView) {
            setTextColor(ContextCompat.getColor(context, R.color.red_100))
            setTextAppearance(R.style.caption_medium)
        }
    }

    fun setVisible(isVisible: Boolean) {
        this.isVisible = isVisible
    }

    // 텍스트 설정 메서드
    fun setText(text: String) {
        textView.text = text
    }

    fun setCustomTextColor(color: Int) {
        textView.setTextColor(color)
    }

    fun setCustomTextSize(size: Float) {
        textView.textSize = size
    }
}