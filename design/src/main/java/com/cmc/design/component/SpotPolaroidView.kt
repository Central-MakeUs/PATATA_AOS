package com.cmc.design.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cmc.design.R
import com.cmc.design.databinding.ViewSpotPolaroidBinding
import com.cmc.design.util.animateClickEffect

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
        binding.ivSpotArchive.apply {
            setOnClickListener {
                isSelected = isSelected.not()
                animateClickEffect()
                onArchiveClickListener?.invoke()
            }
        }
        binding.ivSpotImage.setOnClickListener {
            onImageClickListener?.invoke()
        }
    }

    fun setSpotPolaroidView(
        title: String,
        location: String,
        imageUrl: String,
        tags: List<String>? = null,
        isArchived: Boolean = false,
        isBadgeVisible: Boolean = false,
        archiveClick: () -> Unit,
        imageClick: () -> Unit,
    ) {
        with(binding) {
            tvSpotTitle.text = title
            tvSpotLocation.text = location
            viewSpotBadge.root.visibility = if (isBadgeVisible) View.VISIBLE else View.GONE
            ivSpotArchive.isSelected = isArchived
            updateTags(tags)
            onArchiveClickListener = archiveClick
            onImageClickListener = imageClick
            Glide.with(this.root)
                .load(imageUrl)
                .into(ivSpotImage)
        }

    }

    private fun updateTags(tags: List<String>?) {
        binding.layoutTag.removeAllViews()

        tags?.forEach { tag ->
            val tagView = LayoutInflater.from(context).inflate(R.layout.view_tag_gray, binding.layoutTag, false)
            "#$tag".also { tagView.findViewById<TextView>(R.id.tv_tag).text = it }

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
