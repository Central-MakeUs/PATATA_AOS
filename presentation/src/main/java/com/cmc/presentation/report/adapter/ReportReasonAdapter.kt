package com.cmc.presentation.report.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cmc.presentation.databinding.ViewReportReasonBinding
import com.cmc.presentation.report.model.ReportReason

class ReportReasonAdapter(
    private val reasons: List<ReportReason>,
    private val selectedItemStateChange: (Pair<Int, String?>) -> Unit
) : RecyclerView.Adapter<ReportReasonAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(val binding: ViewReportReasonBinding) : RecyclerView.ViewHolder(binding.root) {
        private fun updateSelectedPosition(position: Int) {
            val prevPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(prevPosition)
            notifyItemChanged(selectedPosition)
            selectedItemStateChange.invoke(Pair(selectedPosition, null))
        }
        fun bind(position: Int) {
            val reason = reasons[position]
            with(binding) {
                tvReasonTitle.text = reason.title
                ivReasonCheck.isSelected = position == selectedPosition

                divider.isVisible = reason.requiresTextInput.not()

                tvReasonEtcInfo.isVisible = reason.requiresTextInput
                etInput.isVisible = reason.requiresTextInput
                tvEtCount.isVisible = reason.requiresTextInput
                etInput.setFocusState(reason.requiresTextInput && position == selectedPosition)


                layoutRoot.setOnClickListener {
                    if (selectedPosition != position) {
                        updateSelectedPosition(position)
                    }
                }
                etInput.setOnTextChangeListener { str ->
                    "${str.length}/300".also { tvEtCount.text = it }
                    selectedItemStateChange.invoke(Pair(selectedPosition, str))
                }

                etInput.setOnFocusChangeListener { hasFocus ->
                    if (hasFocus && selectedPosition != position) {
                        updateSelectedPosition(position)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewReportReasonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = reasons.size
}
