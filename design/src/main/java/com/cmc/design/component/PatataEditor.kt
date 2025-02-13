package com.cmc.design.component

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.cmc.design.R
import com.cmc.design.databinding.ViewPatataEditorBinding
import com.cmc.design.databinding.ViewPatataSearchbarBinding

class PatataEditor @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewPatataEditorBinding =
        ViewPatataEditorBinding.inflate(LayoutInflater.from(context), this, true)

    private var onSubmitListener: ((String) -> Unit)? = null
    private var onTextChangeListener: ((String) -> Unit)? = null

    init {
        applyStyle(false)
        initAttributes(context, attrs)
        initState()
        initListeners()
    }

    private fun initState() {
        binding.ivUploadIcon.isEnabled = false
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PatataEditor)

        when (typedArray.getInt(R.styleable.PatataEditor_editorStyle, 0)) {
            0 -> applyStyle(false)
            1 -> applyStyle(true)
        }

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
        // 검색 아이콘 클릭 시 검색 실행 (활성화 상태에서만)
        binding.ivUploadIcon.setOnClickListener {
            hideKeyboard()
            performSubmit()
        }

        // 키보드 Enter 키 입력 시 검색 실행 (활성화 상태에서만)
        binding.etEditorInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                hideKeyboard()
                performSubmit()
                true
            } else {
                false
            }
        }

        // 입력값 변경 리스너 (활성화 상태에서만)
        binding.etEditorInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ivUploadIcon.isEnabled = !s.isNullOrEmpty()
                onTextChangeListener?.invoke(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun performSubmit() {
        val query = binding.etEditorInput.text.toString().trim()
        onSubmitListener?.invoke(query)
    }

    /**
     * Submit 리스너 설정
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
     * 검색어 초기화
     */
    fun clearEditor() {
        binding.etEditorInput.text.clear()
    }

    /**
     * 검색어 설정
     */
    fun setEditorText(text: String) {
        binding.etEditorInput.setText(text)
    }
    /**
     * 검색바 포커스 및 키보드 노출
     */
    fun focusEditorInput() {
        binding.etEditorInput.requestFocus()
        showKeyboard()
    }

    private fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etEditorInput.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etEditorInput, InputMethodManager.SHOW_IMPLICIT)
    }
}
