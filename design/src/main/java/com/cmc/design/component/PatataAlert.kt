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
import androidx.appcompat.widget.AppCompatButton
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
        binding.dialogTitle.text = title
        return this
    }

    fun title(titleRes: Int): PatataAlert {
        return this.title(context.getString(titleRes))
    }

    fun setCloseButton(isVisible: Boolean = true): PatataAlert {
        with(binding.dialogCloseBtn) {
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
            setLineSpacing(0f, 1.2f)
        }.let {
            binding.dialogContentLay.removeAllViewsInLayout()
            binding.dialogContentLay.isVisible = true
            binding.dialogContentLay.addView(it)
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
            if (binding.dialogTitle.text.isNullOrEmpty()) {
                binding.dialogTitle.isVisible = false
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
        var leftButton: AppCompatButton? = null
        var rightButton: AppCompatButton? = null

        fun leftButton(
            text: String,
            autoDismiss: Boolean = true,
            onClick: (View) -> Unit = {}
        ) {
            leftButton = AppCompatButton(context).apply {
                this.text = text
                this.stateListAnimator = null
                this.layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                        height = 48.dp
                    }
                this.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_dialog_button_main
                    )

                this.setTextColor(ContextCompat.getColor(context, R.color.white))
                this.setOnClickListener {
                    onClick(it)
                    if (autoDismiss) dismiss()
                }
            }
        }

        fun rightButton(
            text: String,
            autoDismiss: Boolean = true,
            onClick: (View) -> Unit = {}
        ) {
            rightButton = AppCompatButton(context).apply {
                this.text = text
                this.layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                        leftMargin = 12.dp
                        height = 48.dp
                    }
                this.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_dialog_button_main)
                this.setTextColor(ContextCompat.getColor(context, R.color.text_default))
                this.setOnClickListener {
                    onClick(it)
                    if (autoDismiss) dismiss()
                }
            }
        }
    }

    fun multiButton(addButton: MultiButtonBuilder.() -> Unit): PatataAlert {
        var buttons = MultiButtonBuilder().apply(addButton)
        LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, context.resources.getDimensionPixelSize(R.dimen.default_spacing), 0, 0)
            weightSum = 2f
            if (buttons.leftButton != null) {
                this.addView(buttons.leftButton)
            } else {
                this.addView(Space(context).apply {
                    layoutParams =
                        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                })
            }
            buttons.rightButton?.let { rightButton ->
                this.addView(rightButton)
            }
        }.let {
            binding.dialogButtonLay.addView(it)
        }
        return this
    }
}
