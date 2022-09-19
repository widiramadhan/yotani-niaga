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
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentGarden

/*
*
*
*@Author
*@Version
*/
class DocumentListAdapter(
    var context: Context,
    var documentList: List<DocumentGarden>,
    var actionListener: ActionListener,
    var editMode: String
) : RecyclerView.Adapter<DocumentListAdapter.ViewHolder>() {

    fun refreshList(newListDocumentGarden: List<DocumentGarden>) {
        documentList = newListDocumentGarden
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemDocumentGardenBinding, var actionListener: ActionListener, var editMode: String, var context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(
            documentGarden: DocumentGarden,
            position: Int
        ) {
            binding.documentTypeName.text = documentGarden.dokumen_name
            binding.documentNo.text = documentGarden.nomor
            binding.editButton.setOnClickListener(View.OnClickListener {
                actionListener.onEditClick(documentGarden)
            })
            binding.deleteButton.setOnClickListener(View.OnClickListener {
                actionListener.onDeleteClick(position)
            })
            binding.mainLayout.setOnClickListener(View.OnClickListener {
                actionListener.onClick(documentGarden)
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
        fun onEditClick(documentGarden: DocumentGarden);
        fun onDeleteClick(documentGarden: Int);
        fun onClick(documentGarden: DocumentGarden);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDocumentGardenBinding>(LayoutInflater.from(parent.context), R.layout.item_document_garden, parent, false)
        return ViewHolder(binding, actionListener, editMode, context)
    }

    override fun getItemCount(): Int {
        return documentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(documentList[position], position)
    }
}