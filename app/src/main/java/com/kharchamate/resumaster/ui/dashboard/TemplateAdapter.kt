package com.kharchamate.resumaster.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kharchamate.resumaster.R
import com.kharchamate.resumaster.data.model.TemplateModel
import com.kharchamate.resumaster.databinding.ItemTemplateBinding

class TemplateAdapter(
    private val onTemplateClick: (TemplateModel) -> Unit
) : ListAdapter<TemplateModel, TemplateAdapter.TemplateViewHolder>(DIFF_CALLBACK) {

    private var selectedTemplateId: String? = null

    fun setSelectedTemplate(templateId: String?) {
        val previousId = selectedTemplateId
        selectedTemplateId = templateId
        currentList.forEachIndexed { index, template ->
            if (template.id == previousId || template.id == templateId) {
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val binding = ItemTemplateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TemplateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.bind(getItem(position), getItem(position).id == selectedTemplateId)
    }

    inner class TemplateViewHolder(private val binding: ItemTemplateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(template: TemplateModel, isSelected: Boolean) {
            val context = binding.root.context

            // ── Template name & category ──────────────────────────────────────
            binding.tvTemplateName.text = template.name
            binding.tvTemplateCategory.text = template.category

            // ── Wireframe preview ─────────────────────────────────────────────
            // Clear previous inflation to avoid duplicates on RecyclerView recycle
            binding.flTemplatePreviewContainer.removeAllViews()
            LayoutInflater.from(context).inflate(
                template.previewLayoutRes,
                binding.flTemplatePreviewContainer,
                true
            )

            // ── PRO badge ─────────────────────────────────────────────────────
            binding.tvProBadge.visibility =
                if (template.isPremium) View.VISIBLE else View.GONE

            // ── Selection overlay ─────────────────────────────────────────────
            binding.overlaySelected.visibility =
                if (isSelected) View.VISIBLE else View.GONE

            // ── Card stroke — teal when selected, subtle gray otherwise ───────
            binding.cardTemplate.strokeColor = if (isSelected) {
                ContextCompat.getColor(context, R.color.strokeSelected)
            } else {
                ContextCompat.getColor(context, R.color.strokeDefault)
            }
            binding.cardTemplate.strokeWidth = if (isSelected) 3 else 1

            // ── Click ─────────────────────────────────────────────────────────
            binding.cardTemplate.setOnClickListener {
                onTemplateClick(template)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TemplateModel>() {
            override fun areItemsTheSame(oldItem: TemplateModel, newItem: TemplateModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: TemplateModel, newItem: TemplateModel) =
                oldItem == newItem
        }
    }
}
