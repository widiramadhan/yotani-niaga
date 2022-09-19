package com.javaindoku.yotaniniaga.view.transaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.ItemTransactionDemandSupplyBinding
import com.javaindoku.yotaniniaga.model.transaction.SupplyDemand
import java.text.SimpleDateFormat

class SupplyDemandAdapter (private val supplyDemands: List<SupplyDemand>) : RecyclerView.Adapter<SupplyDemandAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemTransactionDemandSupplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    class ItemViewHolder(private val binding: ItemTransactionDemandSupplyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(
            item: SupplyDemand
        ) {
            Glide.with(itemView).load(item.image).placeholder(R.color.colorLoading).into(binding.imgSupplyDemands)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z")
            val sdfFormated = SimpleDateFormat("dd MMMM yyyy HH:mm")
            val date = sdf.parse(item.date)
            val dateFormated = sdfFormated.format(date)
            binding.lblDate.text = dateFormated

            binding.lblInvoiceNo.text = item.invoiceNo
            binding.lblName.text = item.name
            binding.lblSupply.text = item.supply
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: SupplyDemand = supplyDemands[position]
        holder.bindItem(item)
    }

    override fun getItemCount(): Int {
        return supplyDemands.size
    }

}