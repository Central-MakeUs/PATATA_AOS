package com.cmc.design.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.cmc.design.R
import com.cmc.design.databinding.ViewPatataEditTextHelperBinding

class PatataEditTextHelperView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewPatataEditTextHelperBinding =
        ViewPatataEditTextHelperBinding.inflate(LayoutInflater.from(context), this)

    init {
        initAttributes(context, attrs)
        initText()
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PatataEditTextHelperView)

        typedArray.getString(R.styleable.PatataEditTextHelperView_android_text)?.let { setText(it) }

        typedArray.recycle()
    }

    private fun initText() {
        with(binding.tvHelper) {
            setTextColor(ContextCompat.getColor(context, R.color.red_100))
            setTextAppearance(R.style.caption_medium)
        }
    }

    fun setVisible(isVisible: Boolean) {
        this.isVisible = isVisible
    }

    // 텍스트 설정 메서드
    fun setText(text: String) {
        binding.tvHelper.text = text
    }

    fun setCustomTextColor(color: Int) {
        binding.tvHelper.setTextColor(color)
    }

    fun setCustomTextSize(size: Float) {
        binding.tvHelper.textSize = size
    }
}