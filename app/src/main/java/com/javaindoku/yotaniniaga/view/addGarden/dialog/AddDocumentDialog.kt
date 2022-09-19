package com.javaindoku.yotaniniaga.view.delivery.dialog

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.DialogAddDocumentBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.CertificateType
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentGarden
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentType
import com.javaindoku.yotaniniaga.utilities.CustomProfile
import com.javaindoku.yotaniniaga.utilities.CustomSpinnerAdapter
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import java.util.*

class AddDocumentDialog(
    var activity: Activity,
    var cancelable: Boolean,
    var title: String,
    var message: String,
    var documentTypeList: MutableList<DocumentType>,
    var documentTypeAdapter : CustomSpinnerAdapter<DocumentType>,
    var labelPositiveButton: String? = null,
    var labelNegativeButton: String? = null,
    var positiveAction: ((document: DocumentGarden) -> Unit)? = null,
    var negativeAction: ((activity: Activity) -> Unit)? = null,
    var document: DocumentGarden = DocumentGarden(),
    var permissionRequestCode: Int,
    var showPictureDialog: (() -> Unit)? = null
) {
    private lateinit var dialog:Dialog
    private lateinit var binding:DialogAddDocumentBinding

    init {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogAddDocumentBinding.inflate(LayoutInflater.from(activity))
        dialog.setContentView(binding.root)
        binding.spinnerDocumentType.setSelection(documentTypeAdapter.getPosition(document.dokumen_id))
        binding.edtNomorDocument.setText(document.nomor.toString())
        CustomProfile.showRemoteImageUsingGlide(activity, binding.documentImage, document.foto, R.drawable.bg_rectangle_white_black)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(cancelable)
        binding.imgClose.setOnSafeClickListener {
            dialog.dismiss()
        }

        if (labelPositiveButton != null) {
            binding.btnConfirmationPositive.text = labelPositiveButton
            binding.btnConfirmationPositive.setOnClickListener {
                document.nomor = binding.edtNomorDocument.text.toString()
                positiveAction?.let { it1 -> it1(document) }
                dialog.dismiss()
            }
        } else {
            binding.btnConfirmationPositive.visibility = View.GONE
        }
        if (labelNegativeButton != null) {
            binding.btnConfirmationNegative.text = labelNegativeButton
            binding.btnConfirmationNegative.setOnClickListener {
                negativeAction?.let { it1 -> it1(activity) }
                dialog.dismiss()
            }
        } else {
            binding.btnConfirmationNegative.visibility = View.GONE
        }
        binding.spinnerDocumentType.adapter = documentTypeAdapter
        binding.spinnerDocumentType.setTitle("Pilih Jenis Sertifikat")
        binding.spinnerDocumentType.setOnSearchTextChangedListener {
            if (binding.spinnerDocumentType != null) {
                for (i in 0..documentTypeList.size - 1) {
                    if(documentTypeList.get(i).dokumen_name.equals(binding.spinnerDocumentType.selectedItem.toString())){
                        break
                    }
                    if(i == (documentTypeList.size-1)){
                        if(!documentTypeList.get(i).dokumen_name.equals(binding.spinnerDocumentType.selectedItem.toString())){
                        }
                    }
                }
            }
        }
        binding.spinnerDocumentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                document.dokumen_id = documentTypeAdapter.items[position].dokumen_id.toString()
                document.dokumen_name = documentTypeAdapter.items[position].dokumen_name.toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.uploadImage.setOnClickListener {
            if (checkPersmission(activity)) {
                showPictureDialog?.let { it1 -> it1() }
            } else {
                requestPermission(activity, permissionRequestCode)
            }
        }
        this.dialog = dialog
    }

    fun show() {
        dialog.show()
    }

    private fun requestPermission(activity: Activity, code: Int) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            code
        )
    }

    private fun checkPersmission(activity: Activity): Boolean {
        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    fun setImageDocument(uri: String, base64:String) {
        CustomProfile.showRemoteImageUsingGlide(
            activity,
            binding.documentImage,
            uri,
            R.drawable.bg_rectangle_white_black
        )
        document.foto = "data:image/jpeg;base64," + base64
    }
}