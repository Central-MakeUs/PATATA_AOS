package com.cmc.design.component

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.cmc.design.R
import com.cmc.design.databinding.ViewPatataEditTextBinding
import com.cmc.design.util.Util.dpToFloat
import java.util.regex.Pattern

class PatataEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewPatataEditTextBinding =
        ViewPatataEditTextBinding.inflate(LayoutInflater.from(context), this, true)

    private var onSubmitListener: ((String) -> Unit)? = null
    private var onTextChangeListener: ((String) -> Unit)? = null
    private var afterTextChangeListener: ((String) -> Unit)? = null
    private var onFocusChangeListener: ((Boolean) -> Unit)? = null

    private var helperView: PatataEditTextHelperView? = null

    private var isEnabled: Boolean = true
    private var isFocusableState: Boolean = false

    init {
        applyStyle(false)
        initAttributes(context, attrs)
        initListeners()
        setFocusState(false)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PatataEditText)

        when (typedArray.getInt(R.styleable.PatataEditText_customEditTextStyle, 0)) {
            0 -> applyStyle(false)
            1 -> applyStyle(true)
        }

        setLinesOption(typedArray.getBoolean(R.styleable.PatataEditText_isSingLine, true))
        isEnabled = typedArray.getBoolean(R.styleable.PatataEditText_isEnabled, true)

        setTextMaxLength(typedArray.getInteger(R.styleable.PatataEditText_editTextMaxLength, -1))
        setTextAppearance(typedArray.getResourceId(R.styleable.PatataEditText_editTextTextAppearance, R.style.subtitle_small))
        setCancelButtonVisibility(
            typedArray.getBoolean(R.styleable.PatataEditText_showCancelButton, false)
        )

        typedArray.getText(R.styleable.PatataEditText_editTextHint)?.let { setHint(it.toString()) }

        typedArray.recycle()
    }

    private fun setLinesOption(isSingleLine: Boolean) {
        with(binding) {
            val inputType = if (isSingleLine) InputType.TYPE_CLASS_TEXT
                else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            etEditTextInput.inputType = inputType

            layoutEditTextRoot.layoutParams = binding.layoutEditTextRoot.layoutParams.apply {
                this.height = (if (isSingleLine) 48 else 120).dpToFloat.toInt()
            }
            etEditTextInput.gravity = if (isSingleLine) Gravity.CENTER_VERTICAL else Gravity.TOP

            val imeOptions = if (isSingleLine) EditorInfo.IME_ACTION_DONE else  EditorInfo.IME_FLAG_NO_ENTER_ACTION
            etEditTextInput.imeOptions = imeOptions
        }
    }

    private fun applyStyle(isDarkMode: Boolean) {
        val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.bg_rounded_8) as GradientDrawable

        if (isDarkMode) {
            backgroundDrawable.setColor(ContextCompat.getColor(context, R.color.gray_20))  // 다크 모드
        } else {
            backgroundDrawable.setColor(ContextCompat.getColor(context, R.color.white))  // 라이트 모드 배경
        }

        binding.root.background = backgroundDrawable
    }

    private fun setTextMaxLength(length: Int) {
        if (length > 0) {
            binding.etEditTextInput.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
        }
    }

    private fun setTextAppearance(resourceId: Int) {
        binding.etEditTextInput.setTextAppearance(resourceId)
    }

    private fun setCancelButtonVisibility(isVisible: Boolean) {
        binding.ivCancel.isVisible = isVisible
    }

    fun setBorderColor(colorResId: Int) {
        val drawable = binding.layoutEditTextRoot.background as? GradientDrawable
        drawable?.let {
            it.setStroke(2.dpToFloat.toInt(), ContextCompat.getColor(context, colorResId)) // 🔥 테두리 색 변경
            binding.layoutEditTextRoot.background = it
        }
    }

    private fun initListeners() {
        binding.ivCancel.setOnClickListener {
            clearEditText()
        }

        // 키보드 Enter 키 입력 시 검색 실행 (활성화 상태에서만)
        binding.etEditTextInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                performSubmit()
                true
            } else {
                false
            }
        }

        // 입력값 변경 리스너 (활성화 상태에서만)
        binding.etEditTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setErrorState(false)
                onTextChangeListener?.invoke(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {
                afterTextChangeListener?.invoke(s.toString())
            }
        })

        binding.etEditTextInput.setOnFocusChangeListener { v, hasFocus ->
            onFocusChangeListener?.invoke(hasFocus)
        }
    }

    private fun performSubmit() {
        val query = binding.etEditTextInput.text.toString().trim()
        onSubmitListener?.invoke(query)
    }

    private fun clearEditText() {
        binding.etEditTextInput.setText("")
    }

    fun setHint(hint: String?) {
        binding.etEditTextInput.hint = hint
    }

    /**
     * 검색 실행 리스너 설정
     */
    fun setOnSubmitListener(listener: ((String) -> Unit)? = null) {
        onSubmitListener = listener
    }

    /**
     * 입력값 변경 리스너 설정
     */
    fun setOnTextChangeListener(listener: ((String) -> Unit)? = null) {
        onTextChangeListener = listener
    }

    /**
     * 입력값 변경 완료 리스너 설정
     */
    fun setAfterTextChangeListener(listener: ((String) -> Unit)? = null) {
        afterTextChangeListener = listener
    }

    fun setOnFocusChangeListener(listener: ((Boolean) -> Unit)? = null) {
        onFocusChangeListener = listener
    }

    /**
     * 검색어 초기화
     */
    fun clearEditor() {
        binding.etEditTextInput.text.clear()
    }

    /**
     * 검색어 설정
     */
    fun setEditorText(text: String) {
        binding.etEditTextInput.setText(text)
    }
    /**
     * 검색바 포커스 및 키보드 노출
     */
    fun focusEditorInput() {
        binding.etEditTextInput.requestFocus()
        showKeyboard()
    }
    /**
     * 태그 입력 타입으로 변경 
     */
    fun setTagInput() {
        binding.etEditTextInput.filters = arrayOf(filterAlphaNumKorean)
    }
    
    fun setErrorState(isError: Boolean) {
        helperView?.setVisible(isError)
        setBorderColor(if (isError) R.color.red_100 else R.color.white)
    }

    fun setFocusState(isCaution: Boolean) {
        isFocusableState = isCaution
        setBorderColor(if (isCaution) R.color.blue_100 else R.color.white)
    }

    fun connectHelperView(patataEditTextHelperView: PatataEditTextHelperView) {
        helperView = patataEditTextHelperView
    }


    // SearchBar 속성 isEnabled 가 false 경우 View Only 로 판단
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isEnabled.not()
    }

    private fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etEditTextInput.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etEditTextInput, InputMethodManager.SHOW_IMPLICIT)
    }
}

private val filterAlphaNumKorean = InputFilter { source, start, end, dest, dstart, dend ->
    val ps = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9]+$") // 공백 제거 (\\s 삭제)
    if (!ps.matcher(source).matches()) "" else source
}