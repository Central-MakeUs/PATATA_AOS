package com.cmc.design.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.cmc.design.R
import com.cmc.design.databinding.ViewPatataAppbarBinding

class PatataAppBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewPatataAppbarBinding =
        ViewPatataAppbarBinding.inflate(LayoutInflater.from(context), this, true)

    private var onBackClickListener: (() -> Unit)? = null
    private var onHeadButtonClickListener: (() -> Unit)? = null
    private var onFootButtonClickListener: (() -> Unit)? = null

    init {
        initAttributes(context, attrs)
        initListeners()
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PatataAppBar)

        // 1. Body 타입 설정 (main, searchbar, title)
        when (typedArray.getInt(R.styleable.PatataAppBar_bodyType, -1)) {
            0 -> { // main
                binding.ivMainTextLogo.visibility = View.VISIBLE
                binding.etSearchbar.visibility = View.GONE
                binding.tvAppbarTitle.visibility = View.GONE
            }
            1 -> { // searchbar
                binding.ivMainTextLogo.visibility = View.GONE
                binding.etSearchbar.visibility = View.VISIBLE
                binding.tvAppbarTitle.visibility = View.GONE
            }
            else -> { // title
                binding.ivMainTextLogo.visibility = View.GONE
                binding.etSearchbar.visibility = View.GONE
                binding.tvAppbarTitle.visibility = View.VISIBLE
            }
        }

        // 2. Head 버튼 설정 (back, close, custom)
        when (typedArray.getInt(R.styleable.PatataAppBar_headButtonType, -1)) {
            0 -> { // back
                binding.ivBackArrow.visibility = View.VISIBLE
            }
            1 -> { // close
                binding.ivBackArrow.visibility = View.GONE
            }
            else -> { // none or custom
                binding.ivBackArrow.visibility = View.GONE
            }
        }

        // 3. Foot 버튼 설정 (complaint, more)
        when (typedArray.getInt(R.styleable.PatataAppBar_footButtonType, -1)) {
            0 -> { // complaint
                binding.ivComplaint.visibility = View.VISIBLE
                binding.ivMore.visibility = View.GONE
            }
            1 -> { // more
                binding.ivComplaint.visibility = View.GONE
                binding.ivMore.visibility = View.VISIBLE
            }
            else -> { // 아무것도 표시 안 함
                binding.ivComplaint.visibility = View.GONE
                binding.ivMore.visibility = View.GONE
            }
        }

        typedArray.recycle()
    }

    private fun initListeners() {
        binding.ivBackArrow.setOnClickListener {
            onBackClickListener?.invoke()
        }
        binding.ivComplaint.setOnClickListener {
            onFootButtonClickListener?.invoke()
        }
        binding.ivMore.setOnClickListener {
            onFootButtonClickListener?.invoke()
        }
    }

    /**
     * 앱바의 타이틀, 아이콘, 버튼 리스너를 한 번에 설정하는 함수
     * @param title 앱바 타이틀 텍스트
     * @param icon 타이틀 아이콘 Drawable (nullable)
     * @param iconPosition 타이틀 아이콘 위치 (start, end, top, bottom)
     * @param onBackClick 뒤로가기 버튼 클릭 리스너
     * @param onHeadButtonClick 헤드 버튼 클릭 리스너 (ex. 닫기 버튼)
     * @param onFootButtonClick 푸터 버튼 클릭 리스너 (ex. 신고, 더보기 버튼)
     */
    fun setupAppBar(
        title: String,
        icon: Int? = null,
        iconPosition: IconPosition = IconPosition.START,
        onBackClick: (() -> Unit)? = null,
        onHeadButtonClick: (() -> Unit)? = null,
        onFootButtonClick: (() -> Unit)? = null,
    ) {
        setTitle(title, icon, iconPosition)
        onBackClickListener = onBackClick
        onHeadButtonClickListener = onHeadButtonClick
        onFootButtonClickListener = onFootButtonClick
    }

    /**
     * 타이틀 텍스트와 아이콘을 설정하는 함수
     * @param title 타이틀 텍스트
     * @param icon 아이콘 Drawable (nullable)
     * @param iconPosition 아이콘 위치 (start, end, top, bottom)
     */
    fun setTitle(
        title: String,
        icon: Int? = null,
        iconPosition: IconPosition = IconPosition.START,
    ) {
        binding.tvAppbarTitle.text = title
        icon?.let {
            when (iconPosition) {
                IconPosition.START -> binding.tvAppbarTitle.setCompoundDrawablesWithIntrinsicBounds(it, 0, 0, 0)
                IconPosition.END -> binding.tvAppbarTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, it, 0)
                IconPosition.TOP -> binding.tvAppbarTitle.setCompoundDrawablesWithIntrinsicBounds(0, it, 0, 0)
                IconPosition.BOTTOM -> binding.tvAppbarTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, it)
            }
        }
    }

    enum class IconPosition {
        START, END, TOP, BOTTOM
    }
}
