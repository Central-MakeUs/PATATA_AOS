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

    private var bodyType: BodyType = BodyType.TITLE

    init {
        initAttributes(context, attrs)
        initListeners()
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PatataAppBar)

        // 1. Body 타입 설정 (main, searchbar, title)
        bodyType = when (typedArray.getInt(R.styleable.PatataAppBar_bodyType, -1)) {
            0 -> BodyType.MAIN
            1 -> BodyType.SEARCH
            else -> BodyType.TITLE
        }

        setBody()

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

    private fun setBody() {
        when (bodyType) {
            BodyType.MAIN -> {
                binding.ivMainTextLogo.visibility = View.VISIBLE
                binding.searchbar.visibility = View.GONE
                binding.tvAppbarTitle.visibility = View.GONE
            }
            BodyType.SEARCH -> {
                binding.ivMainTextLogo.visibility = View.GONE
                binding.searchbar.visibility = View.VISIBLE
                binding.tvAppbarTitle.visibility = View.GONE
            }
            BodyType.TITLE -> {
                binding.ivMainTextLogo.visibility = View.GONE
                binding.searchbar.visibility = View.GONE
                binding.tvAppbarTitle.visibility = View.VISIBLE
            }
        }
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
     * 앱바의 타이틀, 아이콘, 버튼 리스너를 설정하는 함수 (일괄 입력)
     *
     * @param title 앱바 타이틀 텍스트 (TITLE 모드에서는 필수)
     * @param icon 타이틀 아이콘 리소스 (nullable)
     * @param iconPosition 아이콘 위치 (start, end, top, bottom), 기본값 START
     * @param onBackClick 뒤로가기 버튼 클릭 리스너 (nullable)
     * @param onHeadButtonClick 헤드 버튼 클릭 리스너 (nullable)
     * @param onFootButtonClick 푸터 버튼 클릭 리스너 (nullable)
     * @param onSearch 검색 실행 리스너 (nullable)
     * @param onTextChange 검색 텍스트 변경 리스너 (nullable)
     */
    fun setupAppBar(
        title: String,
        icon: Int? = null,
        iconPosition: IconPosition = IconPosition.START,
        onBackClick: (() -> Unit)? = null,
        onHeadButtonClick: (() -> Unit)? = null,
        onFootButtonClick: (() -> Unit)? = null,
        onSearch: ((String) -> Unit)? = null,
        onTextChange: ((String) -> Unit)? = null,
    ) {
        setTitle(title, icon, iconPosition)
        onBackClickListener = onBackClick
        onHeadButtonClickListener = onHeadButtonClick
        onFootButtonClickListener = onFootButtonClick

        binding.searchbar.apply {
            setOnSearchListener(onSearch)
            setOnTextChangeListener(onTextChange)
        }
    }

    /**
     * 앱바의 타이틀, 아이콘, 버튼 리스너를 설정하는 함수 (BodyType이 TITLE인 경우)
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
     * 앱바의 아이콘, 버튼 리스너를 설정하는 함수 (BodyType이 TITLE이 아닌 경우)
     */
    fun setupAppBar(
        icon: Int? = null,
        iconPosition: IconPosition = IconPosition.START,
        onBackClick: (() -> Unit)? = null,
        onHeadButtonClick: (() -> Unit)? = null,
        onFootButtonClick: (() -> Unit)? = null,
        onSearch: ((String) -> Unit)? = null,
        onTextChange: ((String) -> Unit)? = null,
    ) {
        if (bodyType == BodyType.TITLE) {
            throw IllegalArgumentException("BodyType이 TITLE일 때는 setupAppBar(title: String)를 사용해야 합니다.")
        }

        onBackClickListener = onBackClick
        onHeadButtonClickListener = onHeadButtonClick
        onFootButtonClickListener = onFootButtonClick

        binding.searchbar.apply {
            setOnSearchListener(onSearch)
            setOnTextChangeListener(onTextChange)
        }
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

    /**
     * Body Type을 변경하는 함수
     * @param type BodyType
     */
    fun setBodyType(type: BodyType) {
        bodyType = type
        setBody()
    }

    fun getBodyType(): BodyType = bodyType

    /**
     * 검색어 초기화
     */
    fun clearSearch() {
        binding.searchbar.clearSearch()
    }

    /**
     * 검색어 설정
     */
    fun setSearchText(text: String) {
        binding.searchbar.setSearchText(text)
    }

    /**
     * 검색바 포커스 및 키보드 노출
     */
    fun focusSearchInput() {
        binding.searchbar.focusSearchInput()
    }

    enum class IconPosition {
        START, END, TOP, BOTTOM
    }

    enum class BodyType {
        MAIN, SEARCH, TITLE
    }
}
