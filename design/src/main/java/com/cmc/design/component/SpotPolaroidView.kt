package com.cmc.design.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.cmc.design.R
import com.cmc.design.databinding.ViewSpotPolaroidBinding

class SpotPolaroidView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val binding: ViewSpotPolaroidBinding =
        ViewSpotPolaroidBinding.inflate(LayoutInflater.from(context), this, true)

    private var onArchiveClick: (() -> Unit)? = null
    private var onImageClick: (() -> Unit)? = null

    init {
        initListeners()
    }

    private fun initListeners() {
        binding.ivSpotArchive.setOnClickListener {
            onArchiveClick?.invoke()
        }
        binding.ivSpotImage.setOnClickListener {
            onImageClick?.invoke()
        }
    }

    fun setSpotData(
        title: String,
        location: String,
        imageResId: Int,
        tags: List<String>? = null,
        isArchived: Boolean = false,
        isBadgeVisible: Boolean = false
    ) {
        with(binding) {
            tvSpotTitle.text = title
            tvSpotLocation.text = location
            ivSpotImage.setImageResource(imageResId)
            viewSpotBadge.root.visibility = if (isBadgeVisible) View.VISIBLE else View.GONE
            ivSpotArchive.isSelected = isArchived
            updateTags(tags)
        }
    }

    private fun updateTags(tags: List<String>?) {
        binding.layoutTag.removeAllViews()

        tags?.forEach { tag ->
            val tagView = LayoutInflater.from(context).inflate(R.layout.view_tag, binding.layoutTag, false)
            val tagTextView = tagView.findViewById<TextView>(R.id.tv_tag)
            tagTextView.text = tag

            binding.layoutTag.addView(tagView)
        }
    }

    fun setOnArchiveClickListener(listener: () -> Unit) {
        onArchiveClick = listener
    }

    fun setOnImageClickListener(listener: () -> Unit) {
        onImageClick = listener
    }

    fun setSpotTitle(title: String) {
        binding.tvSpotTitle.text = title
    }

    fun setSpotLocation(location: String) {
        binding.tvSpotLocation.text = location
    }

    fun setSpotImage(imageResId: Int) {
        binding.ivSpotImage.setImageResource(imageResId)
    }

    data class SpotPolaroid(
        val title: String,
        val location: String,
        val imageResId: Int,
        val tags: List<String>?,
        val isArchived: Boolean,
        val isBadgeVisible: Boolean = false
    )
}
