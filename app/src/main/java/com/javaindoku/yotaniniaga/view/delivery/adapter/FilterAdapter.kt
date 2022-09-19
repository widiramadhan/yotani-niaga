package com.javaindoku.yotaniniaga.view.delivery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.ItemStatusBinding
import com.javaindoku.yotaniniaga.model.delivery.FilterStatus

class FilterAdapter (private val filterStatuses: List<FilterStatus>, private val onClickStatus: OnClickStatus) : RecyclerView.Adapter<FilterAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, onClickStatus)
    }

    class ItemViewHolder(private val binding: ItemStatusBinding, private val onClickStatus: OnClickStatus) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(
            item: FilterStatus
        ) {
            binding.lblStatus.text = item.name
            binding.lytStatus.setOnClickListener {
                if (item.isSelected) {
                    item.isSelected = false
                    binding.lblStatus.setTextColor(itemView.resources.getColor(R.color.color_title_home))
                }
                else {
                    item.isSelected = true
                    binding.lblStatus.setTextColor(itemView.resources.getColor(R.color.colorPrimary))
                }
                onClickStatus.toHandleStatus(item)
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: FilterStatus = filterStatuses[position]
        holder.bindItem(item)
    }

    override fun getItemCount(): Int {
        return filterStatuses.size
    }

    interface OnClickStatus {
        fun toHandleStatus(filterStatus: FilterStatus)
    }
}