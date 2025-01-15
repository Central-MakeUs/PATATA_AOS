package com.cmc.design

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.cmc.common.model.SpotPolaroid
import com.cmc.design.databinding.ViewSpotPolaroidBinding
import org.w3c.dom.Text

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

    fun setSpotData(data: SpotPolaroid) {
        with(binding) {
            tvSpotTitle.text = data.title
            tvSpotLocation.text = data.location
            ivSpotImage.setImageResource(data.imageResId)
            viewSpotBadge.root.visibility = if (data.isBadgeVisible) View.VISIBLE else View.GONE
            ivSpotArchive.isSelected = data.isArchived
            updateTags(data.tags)
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

}
