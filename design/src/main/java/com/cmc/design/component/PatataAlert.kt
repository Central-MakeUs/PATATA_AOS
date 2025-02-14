package com.cmc.design.component

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.cmc.design.R
import com.cmc.design.databinding.ComponentAlertBaseBinding
import com.cmc.design.util.Util.dp
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PatataAlert(private val context: Context) {

    companion object {
        private var dialog: AlertDialog? = null
    }

    private val builder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
    private val binding: ComponentAlertBaseBinding = ComponentAlertBaseBinding.inflate(
        LayoutInflater.from(context)
    )

    private var inputView: PatataEditText? = null
    private var contentView: View? = null

    private var dismissWithCancel: Boolean = false

    fun title(title: String): PatataAlert {
        binding.tvDialogTitle.text = title
        return this
    }

    fun title(titleRes: Int): PatataAlert {
        return this.title(context.getString(titleRes))
    }

    fun setCloseButton(isVisible: Boolean = true): PatataAlert {
        with(binding.ivDialogClose) {
            visibility = if (isVisible) View.VISIBLE else View.GONE
            setOnClickListener { dismiss() }
        }
        return this
    }

    fun isCancelable(cancelable: Boolean): PatataAlert {
        builder.setCancelable(
            if (dismissWithCancel) false else cancelable
        )
        return this
    }

    fun dismissWithCancel(dismissWithCancel: Boolean): PatataAlert {
        this.dismissWithCancel = dismissWithCancel
        if (dismissWithCancel) {
            builder.setCancelable(false)
            if (context is Activity) builder.setOnCancelListener { context.finish() }
        }
        return this
    }

    fun content(content: String): PatataAlert {
        TextView(context).apply {
            gravity = Gravity.CENTER
            text = content
            setTextAppearance(R.style.caption_medium)
            setTextColor(ContextCompat.getColor(context, R.color.text_info))
            setLineSpacing(0f, 1.2f)
        }.let {
            binding.layoutDialogContent.apply {
                removeAllViewsInLayout()
                isVisible = true
                addView(it)
            }
        }
        return this
    }

    fun show(): AlertDialog? {
        if (dialog?.isShowing != true) {
            builder.setView(binding.root)
            dialog = builder.create()
            dialog?.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(
                    context.resources.getDimensionPixelSize(R.dimen.alert_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            if (binding.tvDialogTitle.text.isNullOrEmpty()) {
                binding.tvDialogTitle.isVisible = false
            }
            dialog?.show()
        }

        return dialog
    }

    fun show(onDialogViewBinding: PatataAlert.(ComponentAlertBaseBinding) -> Unit): AlertDialog? {
        onDialogViewBinding(binding)
        return show()
    }

    fun dismiss() {
        if (context is Activity) {
            context
        }
        if (dialog?.isShowing == true)
            dialog?.dismiss()
    }


    inner class MultiButtonBuilder {
        var leftButtonText: String? = null
        var rightButtonText: String? = null
        var leftButtonClick: (View) -> Unit = {}
        var rightButtonClick: (View) -> Unit = {}

        fun leftButton(
            text: String,
            autoDismiss: Boolean = true,
            onClick: (View) -> Unit = {}
        ) {
            leftButtonText = text
            leftButtonClick = {
                onClick(it)
                if (autoDismiss) dismiss()
            }
        }

        fun rightButton(
            text: String,
            autoDismiss: Boolean = true,
            onClick: (View) -> Unit = {}
        ) {
            rightButtonText = text
            rightButtonClick = {
                onClick(it)
                if (autoDismiss) dismiss()
            }
        }
    }

    fun multiButton(addButton: MultiButtonBuilder.() -> Unit): PatataAlert {
        val buttons = MultiButtonBuilder().apply(addButton)

        LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            clipToOutline = true
            setPadding(0, 0, 0, 0)
            weightSum = 2f

            if (buttons.leftButtonText != null) {
                addView(createDialogButton(buttons.leftButtonText!!, buttons.leftButtonClick, R.color.text_info))
            } else {
                addView(Space(context).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                })
            }

            // 🔹 버튼 사이 Divider 추가
            addView(View(context).apply {
                layoutParams = LinearLayout.LayoutParams(1.dp, LinearLayout.LayoutParams.MATCH_PARENT)
                setBackgroundColor(ContextCompat.getColor(context, R.color.gray_30))
            })

            buttons.rightButtonText?.let { rightButtonText ->
                addView(createDialogButton(rightButtonText, buttons.rightButtonClick, R.color.red_100))
            }
        }.let {
            binding.layoutDialogButton.apply {
                removeAllViews()
                addView(View(context).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1.dp)
                    setBackgroundColor(ContextCompat.getColor(context, R.color.gray_30))
                })
                addView(it)
            }

        }
        return this
    }

    private fun createDialogButton(str: String, onClick: (View) -> Unit, textColor: Int): View {
        return LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, 48.dp, 1f).apply {
                setMargins(0, 14, 0, 14)
            }

            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true
            setBackgroundResource(android.R.color.transparent) // 투명 배경
            setOnClickListener { onClick(it) }

            addView(TextView(context).apply {
                textSize = 16f
                text = str
                setTextColor(ContextCompat.getColor(context, textColor))
                gravity = Gravity.CENTER
            })
        }
    }
}
