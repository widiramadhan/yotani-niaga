package com.javaindoku.yotaniniaga.view.addGarden.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.ItemDocumentGardenBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.CertificateGarden
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentGarden

class CertificatetListAdapter(
    var context: Context,
    var certificateList: List<CertificateGarden>,
    var actionListener: ActionListener,
    var editMode: String
) : RecyclerView.Adapter<CertificatetListAdapter.ViewHolder>() {

    fun refreshList(newListCertificateGarden: List<CertificateGarden>) {
        certificateList = newListCertificateGarden
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemDocumentGardenBinding, var actionListener: ActionListener, var editMode: String, var context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(
            certificateGarden: CertificateGarden,
            position: Int
        ) {
            binding.documentTypeName.text = certificateGarden.sertifikasi_name
            binding.documentNo.text = certificateGarden.sertifikasi_no
            binding.editButton.setOnClickListener(View.OnClickListener {
                actionListener.onEditClick(certificateGarden)
            })
            binding.deleteButton.setOnClickListener(View.OnClickListener {
                actionListener.onDeleteClick(position)
            })
            binding.mainLayout.setOnClickListener(View.OnClickListener {
                actionListener.onClick(certificateGarden)
            })

            if (editMode == "false"){
                binding.editButton.isEnabled = false
                binding.editButton.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_disabled_edittext))
                binding.deleteButton.isEnabled = false
                binding.deleteButton.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_disabled_edittext))
            }
        }
    }

    interface ActionListener {
        fun onEditClick(certificateGarden: CertificateGarden);
        fun onDeleteClick(certificateGarden: Int);
        fun onClick(certificateGarden: CertificateGarden);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDocumentGardenBinding>(LayoutInflater.from(parent.context), R.layout.item_document_garden, parent, false)
        return ViewHolder(binding, actionListener, editMode, context)
    }

    override fun getItemCount(): Int {
        return certificateList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(certificateList[position], position)
    }
}