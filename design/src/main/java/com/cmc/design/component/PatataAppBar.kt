package com.cmc.design.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.cmc.design.R
import com.cmc.design.databinding.ViewPatataAppbarBinding
import com.cmc.design.util.Util.dp

class PatataAppBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewPatataAppbarBinding =
        ViewPatataAppbarBinding.inflate(LayoutInflater.from(context), this, true)

    private var onHeadButtonClickListener: ((HeaderType) -> Unit)? = null
    private var onBodyClickListener: ((BodyType) -> Unit)? = null
    private var onFootButtonClickListener: ((FooterType) -> Unit)? = null

    private var headerType: HeaderType = HeaderType.NONE
    private var bodyType: BodyType = BodyType.TITLE
    private var footerType: FooterType = FooterType.NONE

    init {
        initAttributes(context, attrs)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.PatataAppBar).apply {
            try {
                val headType = HeaderType.fromId(getInt(R.styleable.PatataAppBar_headButtonType, -1))
                val bodyType = BodyType.fromId(getInt(R.styleable.PatataAppBar_bodyType, 0))
                val footerType = FooterType.fromId(getInt(R.styleable.PatataAppBar_footButtonType, -1))

                val bgStyle = getInt(R.styleable.PatataAppBar_appBarBackgroundStyle, 0)
                val searchBarStyle = getInt(R.styleable.PatataAppBar_appbarSearchBarStyle, 0)

                setHeader(headType)
                setBody(bodyType)
                setFooter(footerType)

                applySearchBarStyle(searchBarStyle)
                applyBackgroundStyle(bgStyle)

            } finally {
                recycle()
            }
        }
    }

    private fun setHeader(type: HeaderType) {
        mapOf(
            HeaderType.BACK to binding.ivBackArrow,
            HeaderType.LIST to binding.ivList,
            HeaderType.MAP to binding.ivMap,
        ).forEach { (t, v) ->
            v.isVisible = t == type
            v.setOnClickListener {
                onHeadButtonClickListener?.invoke(type)
            }
        }
    }
    private fun setBody(bodyType: BodyType) {
        this.bodyType = bodyType
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

        binding.searchbar.setOnSearchBarClickListener {
            onBodyClickListener?.invoke(bodyType)
        }
    }
    private fun setFooter(type: FooterType) {
        mapOf(
            FooterType.COMPLAINT to binding.ivFooterComplaint,
            FooterType.MORE to binding.ivFooterMore,
            FooterType.CANCEL to binding.ivFooterCancel,
            FooterType.SELECT to binding.tvFooterSelect,
            FooterType.DELETE to binding.tvFooterDelete,
            FooterType.SETTING to binding.ivFooterSetting,
        ).forEach { (t, v) ->
            v.isVisible = t == type
            v.setOnClickListener {
                onFootButtonClickListener?.invoke(type)
            }
        }
    }

    private fun applySearchBarStyle(style: Int) {
        when (style) {
            0 -> { binding.searchbar.setMode(false) }
            1 -> { binding.searchbar.setMode(true) }
        }
    }
    private fun applyBackgroundStyle(style: Int) {
        when (style) {
            0 -> { binding.layoutRoot.background = AppCompatResources.getDrawable(context, R.color.transparent) }
            1 -> { binding.layoutRoot.background = AppCompatResources.getDrawable(context, R.color.white) }
        }
    }

    /**
     * 앱바의 타이틀, 아이콘, 버튼 리스너를 설정하는 함수 (일괄 입력)
     *
     * @param title 앱바 타이틀 텍스트 (TITLE 모드에서는 필수)
     * @param icon 타이틀 아이콘 리소스 (nullable)
     * @param iconPosition 아이콘 위치 (start, end, top, bottom), 기본값 START
     * @param onHeadButtonClick 뒤로가기 버튼 클릭 리스너 (nullable)
     * @param onHeadButtonClick 헤드 버튼 클릭 리스너 (nullable)
     * @param onFootButtonClick 푸터 버튼 클릭 리스너 (nullable)
     * @param onSearch 검색 실행 리스너 (nullable)
     * @param onTextChange 검색 텍스트 변경 리스너 (nullable)
     */
    fun setupAppBar(
        title: String,
        icon: Int? = null,
        iconPosition: IconPosition = IconPosition.START,
        onHeadButtonClick: ((HeaderType) -> Unit)? = null,
        onFootButtonClick: ((FooterType) -> Unit)? = null,
        onSearch: ((String) -> Unit)? = null,
        onTextChange: ((String) -> Unit)? = null,
    ) {
        setTitle(title, icon, iconPosition)

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
        onHeadButtonClick: ((HeaderType) -> Unit)? = null,
        onFootButtonClick: ((FooterType) -> Unit)? = null,
    ) {
        setTitle(title, icon, iconPosition)
        onHeadButtonClickListener = onHeadButtonClick
        onFootButtonClickListener = onFootButtonClick
    }

    /**
     * 앱바의 아이콘, 버튼 리스너를 설정하는 함수 (BodyType이 TITLE이 아닌 경우)
     */
    fun setupAppBar(
        icon: Int? = null,
        iconPosition: IconPosition = IconPosition.START,
        onHeadButtonClick: ((HeaderType) -> Unit)? = null,
        onBodyClick: ((BodyType) -> Unit)? = null,
        onFootButtonClick: ((FooterType) -> Unit)? = null,
        onSearch: ((String) -> Unit)? = null,
        onTextChange: ((String) -> Unit)? = null,
        searchBarDisable: Boolean = false,
    ) {
        if (bodyType == BodyType.TITLE) {
            throw IllegalArgumentException("BodyType이 TITLE일 때는 setupAppBar(title: String)를 사용해야 합니다.")
        }

        onHeadButtonClickListener = onHeadButtonClick
        onBodyClickListener = onBodyClick
        onFootButtonClickListener = onFootButtonClick

        binding.searchbar.apply {
            setOnSearchListener(onSearch)
            setOnTextChangeListener(onTextChange)
        }

        if (searchBarDisable) {
            binding.searchbar.setDisable()
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
        icon?.let { i ->
            val drawable = ContextCompat.getDrawable(context, i) ?: return

            drawable.setBounds(0, 0, 18.dp, 18.dp)
            binding.tvAppbarTitle.compoundDrawablePadding = 5.dp
            when (iconPosition) {
                IconPosition.START -> binding.tvAppbarTitle.setCompoundDrawables(drawable, null, null, null)
                IconPosition.END -> binding.tvAppbarTitle.setCompoundDrawables(null, null, drawable, null)
                IconPosition.TOP -> binding.tvAppbarTitle.setCompoundDrawables(null, drawable, null, null)
                IconPosition.BOTTOM -> binding.tvAppbarTitle.setCompoundDrawables(null, null, null, drawable)
            }
        }
    }

    fun changeBackgroundStyle(style: Int) {
        applyBackgroundStyle(style)
    }

    /**
     * Body Type을 변경하는 함수
     * @param type BodyType
     */
    fun setBodyType(type: BodyType) {
        bodyType = type
        setBody(bodyType)
    }
    fun getBodyType(): BodyType = bodyType

    /**
     * Footer Type을 변경하는 함수
     * @param type FooterType
     */
    fun setFooterType(type: FooterType) {
        setFooter(type)
    }
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

    enum class HeaderType(val id: Int) {
        BACK(0), LIST(1), MAP(2), NONE(-1);
        companion object {
            fun fromId(id: Int) = entries.first { it.id == id } ?: NONE
        }
    }
    enum class BodyType(val id: Int) {
        MAIN(0), SEARCH(1), TITLE(2);
        companion object {
            fun fromId(id: Int) = entries.first { it.id == id } ?: MAIN
        }
    }
    enum class FooterType(val id: Int) {
        COMPLAINT(0), MORE(1), CANCEL(2), SELECT(3), DELETE(4), SETTING(5),NONE(-1);
        companion object {
            fun fromId(id: Int) = entries.first { it.id == id } ?: NONE
        }
    }
}
