package com.javaindoku.yotaniniaga.view.sendstocksupplyfarmer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.ItemConfirmReservasiBinding
import com.javaindoku.yotaniniaga.databinding.ItemDocumentGardenBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentGarden
import com.javaindoku.yotaniniaga.model.sendstocksupplyfarmer.DataConfirmReservasi

class ConfirmReservasiListAdapter(
    var context: Context,
    var documentList: List<DataConfirmReservasi>
) : RecyclerView.Adapter<ConfirmReservasiListAdapter.ViewHolder>() {

    fun refreshList(newListDocumentGarden: List<DataConfirmReservasi>) {
        documentList = newListDocumentGarden
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemConfirmReservasiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(data: DataConfirmReservasi) {
            binding.noGRN.text = data.noGrn
            binding.namaPetani.text = data.namaPetani
            binding.tglTerima.text = data.tanggalTerima
            binding.totalTonasi.text = "Tonasi "+ data.totalTonasi + " Kg"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemConfirmReservasiBinding>(LayoutInflater.from(parent.context), R.layout.item_confirm_reservasi, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return documentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(documentList[position])
    }
}