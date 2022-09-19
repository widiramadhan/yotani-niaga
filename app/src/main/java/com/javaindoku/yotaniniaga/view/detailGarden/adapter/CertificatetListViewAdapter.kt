package com.javaindoku.yotaniniaga.view.detailGarden.adapter

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
import com.javaindoku.yotaniniaga.databinding.ItemDocumentGardenViewBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.CertificateGarden
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentGarden
import com.javaindoku.yotaniniaga.utilities.CustomProfile

class CertificatetListViewAdapter(
    var context: Context,
    var certificateList: List<CertificateGarden>,
    var actionListener: ActionListener
) : RecyclerView.Adapter<CertificatetListViewAdapter.ViewHolder>() {

    fun refreshList(newListCertificateGarden: List<CertificateGarden>) {
        certificateList = newListCertificateGarden
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemDocumentGardenViewBinding, var actionListener: ActionListener, var context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(
            certificateGarden: CertificateGarden,
            position: Int
        ) {
            binding.documentTypeName.text = certificateGarden.sertifikasi_name
            binding.documentNo.text = certificateGarden.sertifikasi_no
            CustomProfile.showRemoteImageUsingGlide(context, binding.imgDocument, certificateGarden.sertifikasi_image.toString(), R.drawable.bg_rectangle_white_black)
            /*binding.editButton.setOnClickListener(View.OnClickListener {
                actionListener.onEditClick(documentGarden)
            })
            binding.deleteButton.setOnClickListener(View.OnClickListener {
                actionListener.onDeleteClick(position)
            })
            binding.mainLayout.setOnClickListener(View.OnClickListener {
                actionListener.onClick(documentGarden)
            })*/
        }
    }

    interface ActionListener {
        fun onEditClickCertificate(certificateGarden: CertificateGarden);
        fun onDeleteClickCertificate(certificateGarden: Int);
        fun onClickCertificate(certificateGarden: CertificateGarden);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDocumentGardenViewBinding>(LayoutInflater.from(parent.context), R.layout.item_document_garden_view, parent, false)
        return ViewHolder(binding, actionListener, context)
    }

    override fun getItemCount(): Int {
        return certificateList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(certificateList[position], position)
    }
}