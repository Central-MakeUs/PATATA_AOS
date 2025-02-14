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

    private var onArchiveClickListener: (() -> Unit)? = null
    private var onImageClickListener: (() -> Unit)? = null

    init {
        initListeners()
    }

    private fun initListeners() {
        binding.ivSpotArchive.setOnClickListener {
            onArchiveClickListener?.invoke()
        }
        binding.ivSpotImage.setOnClickListener {
            onImageClickListener?.invoke()
        }
    }

    fun setSpotPolaroidView(
        title: String,
        location: String,
        imageResId: Int,
        tags: List<String>? = null,
        isArchived: Boolean = false,
        isBadgeVisible: Boolean = false,
        archiveClick: () -> Unit,
        imageClick: () -> Unit,
    ) {
        with(binding) {
            tvSpotTitle.text = title
            tvSpotLocation.text = location
            ivSpotImage.setImageResource(imageResId)
            viewSpotBadge.root.visibility = if (isBadgeVisible) View.VISIBLE else View.GONE
            ivSpotArchive.isSelected = isArchived
            updateTags(tags)
            onArchiveClickListener = archiveClick
            onImageClickListener = imageClick
        }

    }

    private fun updateTags(tags: List<String>?) {
        binding.layoutTag.removeAllViews()

        tags?.forEach { tag ->
            val tagView = LayoutInflater.from(context).inflate(R.layout.view_tag_gray, binding.layoutTag, false)
            val tagTextView = tagView.findViewById<TextView>(R.id.tv_tag)
            tagTextView.text = tag

            binding.layoutTag.addView(tagView)
        }
    }

    fun setSpotTitle(title: String) {
        binding.tvSpotTitle.text = title
    }

    data class SpotPolaroidItem(
        val title: String,
        val location: String,
        val imageResId: Int,
        val tags: List<String>?,
        val isScraped: Boolean,
        val isRecommended: Boolean = false
    )
}
