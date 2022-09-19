package com.javaindoku.yotaniniaga.view.delivery.dialog

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.DialogAddCertificateBinding
import com.javaindoku.yotaniniaga.databinding.DialogAddDocumentBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.CertificateGarden
import com.javaindoku.yotaniniaga.model.gardenPetani.CertificateType
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentGarden
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentType
import com.javaindoku.yotaniniaga.utilities.CustomProfile
import com.javaindoku.yotaniniaga.utilities.CustomSpinnerAdapter
import com.javaindoku.yotaniniaga.utilities.StringFormat
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import java.text.SimpleDateFormat
import java.util.*

class AddCertificateDialog(
    var activity: Activity,
    var cancelable: Boolean,
    var title: String,
    var message: String,
    var certificateTypeList: MutableList<CertificateType>,
    var certificateTypeAdapter : CustomSpinnerAdapter<CertificateType>,
    var labelPositiveButton: String? = null,
    var labelNegativeButton: String? = null,
    var positiveAction: ((certificate: CertificateGarden) -> Unit)? = null,
    var negativeAction: ((activity: Activity) -> Unit)? = null,
    var certificate: CertificateGarden = CertificateGarden(),
    var permissionRequestCode: Int,
    var showPictureDialog: (() -> Unit)? = null
) {
    private lateinit var dialog:Dialog
    private lateinit var binding:DialogAddCertificateBinding

    init {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogAddCertificateBinding.inflate(LayoutInflater.from(activity))
        dialog.setContentView(binding.root)
        binding.spinnerCertificateType.setSelection(certificateTypeAdapter.getPosition(certificate.sertifikasi_id))
        binding.edtCertificateNumber.setText(certificate.sertifikasi_no.toString())
        binding.lblDateFrom.setText(certificate.sertifikasi_dari.toString())
        binding.lblDateTo.setText(certificate.sertifikasi_sampai.toString())
        CustomProfile.showRemoteImageUsingGlide(activity, binding.imgCertificate, certificate.sertifikasi_image, R.drawable.bg_rectangle_white_black)
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
                certificate.sertifikasi_no = binding.edtCertificateNumber.text.toString()
                certificate.sertifikasi_dari = StringFormat.formatStringDate(
                    binding.lblDateFrom.text.toString(),
                    "dd/MM/yyyy",
                    "yyyy-MM-dd"
                )
                certificate.sertifikasi_sampai = StringFormat.formatStringDate(
                    binding.lblDateTo.text.toString(),
                    "dd/MM/yyyy",
                    "yyyy-MM-dd"
                )
                positiveAction?.let { it1 -> it1(certificate) }
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
        binding.spinnerCertificateType.adapter = certificateTypeAdapter
        binding.spinnerCertificateType.setTitle("Pilih Jenis Sertifikat")
        binding.spinnerCertificateType.setOnSearchTextChangedListener {
            if (binding.spinnerCertificateType != null) {
                for (i in 0..certificateTypeList.size - 1) {
                    if(certificateTypeList.get(i).name.equals(binding.spinnerCertificateType.selectedItem.toString())){
                        break
                    }
                    if(i == (certificateTypeList.size-1)){
                        if(!certificateTypeList.get(i).name.equals(binding.spinnerCertificateType.selectedItem.toString())){
                        }
                    }
                }
            }
        }
        binding.spinnerCertificateType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                certificate.sertifikasi_id = certificateTypeAdapter.items[position].id.toString()
                certificate.sertifikasi_name = certificateTypeAdapter.items[position].name.toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.spinnerCertificateType.setSelection(certificateTypeAdapter.getPosition(certificate.sertifikasi_id))

        binding.edtDateFrom.setOnClickListener { showDateFromPickerDialog() }
        binding.lblDateFrom.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty()) {
                    binding.imgDateFrom.setImageResource(R.drawable.ic_close_black_24dp)
                    binding.imgDateFrom.setOnClickListener {
                        binding.lblDateFrom.setText("")
                    }
                } else {
                    binding.imgDateFrom.setImageResource(R.drawable.kalender)
                    binding.imgDateFrom.setOnClickListener {
                        showDateFromPickerDialog()
                    }
                }
            }
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edtDateTo.setOnClickListener { showDateToPickerDialog() }
        binding.lblDateTo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty()) {
                    binding.imgDateTo.setImageResource(R.drawable.ic_close_black_24dp)
                    binding.imgDateTo.setOnClickListener {
                        binding.lblDateTo.setText("")
                    }
                } else {
                    binding.imgDateTo.setImageResource(R.drawable.kalender)
                    binding.imgDateTo.setOnClickListener {
                        showDateToPickerDialog()
                    }
                }
            }
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnImgCertificate.setOnClickListener {
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
            binding.imgCertificate,
            uri,
            R.drawable.bg_rectangle_white_black
        )
        certificate.sertifikasi_image = "data:image/jpeg;base64," + base64
    }

    private fun showDateFromPickerDialog() {
        val c: Calendar = Calendar.getInstance()
        val mYear: Int = c.get(Calendar.YEAR)
        val mMonth: Int = c.get(Calendar.MONTH)
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = activity.let {
            DatePickerDialog(it,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "dd/MM/yyyy" //In which you need put here
                    val sdf = SimpleDateFormat(myFormat)
                    binding.lblDateFrom.text = sdf.format(c.time)
                    /*parent.delegateGarden().sertifikasiDari = StringFormat.formatStringDate(
                        binding.lblDateFrom.text.toString(),
                        "dd/MM/yyyy",
                        "yyyy-MM-dd"
                    )*/
                }, mYear, mMonth, mDay
            )
        }
        datePickerDialog.show()
    }

    private fun showDateToPickerDialog() {
        val c: Calendar = Calendar.getInstance()
        val mYear: Int = c.get(Calendar.YEAR)
        val mMonth: Int = c.get(Calendar.MONTH)
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = activity.let {
            DatePickerDialog(it,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "dd/MM/yyyy" //In which you need put here
                    val sdf = SimpleDateFormat(myFormat)
                    binding.lblDateTo.text = sdf.format(c.time)
                    /*parent.delegateGarden().sertifikasiSampai = StringFormat.formatStringDate(
                        binding.lblDateTo.text.toString(),
                        "dd/MM/yyyy",
                        "yyyy-MM-dd"
                    )*/
                }, mYear, mMonth, mDay
            )
        }
        datePickerDialog.show()
    }
}