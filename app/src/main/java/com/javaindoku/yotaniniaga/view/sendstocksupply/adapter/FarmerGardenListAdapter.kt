package com.javaindoku.yotaniniaga.view.sendstocksupply.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.KebunPetaniListItemBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.CropToSend

/*
*
*
*@Author
*@Version
*/
class FarmerGardenListAdapter(
    val context: Context,
    var gardenList: List<CropToSend>,
    val itemMenuInterface: ItemMenuInterface
) : RecyclerView.Adapter<FarmerGardenListAdapter.ViewHolder>() {
    interface ItemMenuInterface {
        fun onEdit(
            garden: CropToSend,
            position: Int
        )
        fun onRemove(position: Int)
    }

    fun refreshItem(newCropsList: List<CropToSend>) {
        gardenList = newCropsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = KebunPetaniListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view, itemMenuInterface)
    }

    override fun getItemCount(): Int {
        return gardenList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(gardenList[position], position)
    }

    class ViewHolder(val binding: KebunPetaniListItemBinding, val itemMenuInterface: ItemMenuInterface)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            cropToSend: CropToSend,
            position: Int
        ) {
            binding.lblNamaPetani.setText(cropToSend.petaniName)
            binding.lblNameKebun.setText("${cropToSend.jenisBibit}, ${cropToSend.kelurahan}")
            binding.lblTonasi.setText("${cropToSend.tonasi}%")
            binding.lytMoreGarden.setOnClickListener{
                val popup = PopupMenu(binding.root.context, it)
                popup.setOnMenuItemClickListener { itemMenu ->
                    when (itemMenu.itemId) {
                        R.id.actEdit -> {
                            itemMenuInterface.onEdit(cropToSend, position)
                            true
                        }
                        R.id.actDelete -> {
                            itemMenuInterface.onRemove(position)
                            true
                        }
                        else -> false
                    }
                }
                popup.inflate(R.menu.menu_garden)
                popup.gravity = Gravity.RIGHT
                popup.show()
            }
        }
    }
}