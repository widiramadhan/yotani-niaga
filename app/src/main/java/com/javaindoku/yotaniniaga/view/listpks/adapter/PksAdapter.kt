package com.javaindoku.yotaniniaga.view.listpks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.ItemPksBinding
import com.javaindoku.yotaniniaga.model.pks.PKS
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener

class PksAdapter (private val pksList: List<PKS>, private val onClickPks: OnClickPks) : RecyclerView.Adapter<PksAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemPksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, onClickPks)
    }

    class ItemViewHolder(private val binding: ItemPksBinding, private val onClickPks: OnClickPks) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(
            item: PKS
        ) {
            binding.lblNamaPks.text = item.pksName
            binding.lblGardenArea.text = item.gardenArea
            binding.lblLocation.text = item.location
            binding.lblTotalTree.text = item.totalTree
            binding.lblYearsOfPlant.text = item.yearsOfPlant
            binding.lytEditPks.setOnSafeClickListener {
                onClickPks.toEditPks(item)
            }
            Glide.with(itemView).load("https://maps.googleapis.com/maps/api/staticmap?center=Brooklyn+Bridge,New+York,NY&zoom=13&size=600x300&maptype=roadmap&markers=color:blue%7Clabel:S%7C40.702147,-74.015794&markers=color:green%7Clabel:G%7C40.711614,-74.012318&markers=color:red%7Clabel:C%7C40.718217,-73.998284&key=AIzaSyD2yJ0zDOhEJl34M-e1lnUwI6YvA-Cu4eI").placeholder(R.color.colorLoading).into(binding.map)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: PKS = pksList[position]
        holder.bindItem(item)
    }

    override fun getItemCount(): Int {
        return pksList.size
    }

    interface OnClickPks {
        fun toEditPks(pks: PKS)
    }
}