package com.kharchamate.resumaster.ui.template

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kharchamate.resumaster.R
import com.kharchamate.resumaster.databinding.ItemTemplateCardBinding

class TemplateSelectionAdapter(
    private val templates: List<TemplateSelectionModel>,
    private val onTemplateSelected: (TemplateSelectionModel) -> Unit
) : RecyclerView.Adapter<TemplateSelectionAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(val binding: ItemTemplateCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(template: TemplateSelectionModel, position: Int) {
            binding.tvTemplateName.text = template.name
            binding.tvTemplateDesc.text = template.description
            
            binding.flTemplatePreviewContainer.removeAllViews()
            LayoutInflater.from(binding.root.context).inflate(template.layoutResId, binding.flTemplatePreviewContainer, true)

            val isSelected = position == selectedPosition

            if (isSelected) {
                binding.cardTemplate.strokeColor = ContextCompat.getColor(binding.root.context, R.color.primary)
                binding.ivSelectedIndicator.visibility = View.VISIBLE
            } else {
                binding.cardTemplate.strokeColor = ContextCompat.getColor(binding.root.context, android.R.color.transparent)
                binding.ivSelectedIndicator.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                if (selectedPosition != position) {
                    val previousSelected = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousSelected)
                    notifyItemChanged(selectedPosition)
                    onTemplateSelected(template)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTemplateCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(templates[position], position)
    }

    override fun getItemCount(): Int = templates.size
}
