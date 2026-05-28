package com.kharchamate.resumaster.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kharchamate.resumaster.R
import com.kharchamate.resumaster.databinding.ItemTemplateBinding

class TemplateAdapter(
    private val templatesList: List<String>,
    private val onTemplateClick: (String) -> Unit
) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val binding = ItemTemplateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TemplateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val templateName = templatesList[position]
        holder.bind(templateName)
    }

    override fun getItemCount(): Int = templatesList.size

    inner class TemplateViewHolder(private val binding: ItemTemplateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String) {
            binding.tvTemplateName.text = name
            binding.ivTemplatePreview.setImageResource(R.drawable.img_template_placeholder)
            binding.cardTemplate.setOnClickListener {
                onTemplateClick(name)
            }
        }
    }
}
