package com.cmc.design.component

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.cmc.design.R
import com.cmc.design.databinding.ViewPatataSearchbarBinding

class PatataSearchBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewPatataSearchbarBinding =
        ViewPatataSearchbarBinding.inflate(LayoutInflater.from(context), this, true)

    private var onSearchListener: ((String) -> Unit)? = null
    private var onTextChangeListener: ((String) -> Unit)? = null
    private var onSearchBarClickListener: (() -> Unit)? = null

    private var isEnabled: Boolean = true

    init {
        applyStyle(false)
        initAttributes(context, attrs)
        initListeners()
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PatataSearchBar)

        when (typedArray.getInt(R.styleable.PatataSearchBar_searchBarStyle, 0)) {
            0 -> applyStyle(false)
            1 -> applyStyle(true)
        }

        isEnabled = typedArray.getBoolean(R.styleable.PatataSearchBar_isEnabled, true)

        typedArray.recycle()
    }

    private fun applyStyle(isDarkMode: Boolean) {
        val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.bg_searchbar) as GradientDrawable

        if (isDarkMode) {
            backgroundDrawable.setColor(ContextCompat.getColor(context, R.color.gray_10))  // 다크 모드 배경
            backgroundDrawable.setStroke(2, ContextCompat.getColor(context, R.color.gray_20))
        } else {
            backgroundDrawable.setColor(ContextCompat.getColor(context, R.color.white))  // 라이트 모드 배경
            backgroundDrawable.setStroke(2, ContextCompat.getColor(context, R.color.gray_30))
        }

        binding.root.background = backgroundDrawable
    }

    private fun initListeners() {
        // 검색바 전체 클릭 (비활성화 상태에서만)
        this.setOnClickListener {
            if (!isEnabled) {
                onSearchBarClickListener?.invoke()
            }
        }

        // 검색 아이콘 클릭 시 검색 실행 (활성화 상태에서만)
        binding.ivSearchIcon.setOnClickListener {
            performSearch()
        }

        // 키보드 Enter 키 입력 시 검색 실행 (활성화 상태에서만)
        binding.etSearchInput.setOnEditorActionListener { _, actionId, event ->
            if ((actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN))) {
                performSearch()
                true
            } else {
                false
            }
        }

        // 입력값 변경 리스너 (활성화 상태에서만)
        binding.etSearchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChangeListener?.invoke(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun performSearch() {
        val query = binding.etSearchInput.text.toString().trim()
        onSearchListener?.invoke(query)
    }

    /**
     * 검색 실행 리스너 설정
     */
    fun setOnSearchListener(listener: (String) -> Unit) {
        onSearchListener = listener
    }

    /**
     * 입력값 변경 리스너 설정
     */
    fun setOnTextChangeListener(listener: (String) -> Unit) {
        onTextChangeListener = listener
    }

    /**
     * 검색바 클릭 리스너 설정
     */
    fun setOnSearchBarClickListener(listener: () -> Unit) {
        onSearchBarClickListener = listener
    }

    /**
     * 검색어 초기화
     */
    fun clearSearch() {
        binding.etSearchInput.text.clear()
    }

    /**
     * 검색어 설정
     */
    fun setSearchText(text: String) {
        binding.etSearchInput.setText(text)
    }

    // SearchBar 속성 isEnabled 가 false 경우 클릭용 SearchBar 로 판단
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isEnabled.not()
    }
    
}
