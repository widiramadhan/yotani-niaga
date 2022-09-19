package com.javaindoku.yotaniniaga.view.addGarden.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.iceteck.silicompressorr.SiliCompressor
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentCertificateGardenBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.CertificateType
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenDataSchema
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.CustomProfile
import com.javaindoku.yotaniniaga.utilities.CustomSpinnerAdapter
import com.javaindoku.yotaniniaga.utilities.StringFormat
import com.javaindoku.yotaniniaga.utilities.removeBase64DataString
import com.javaindoku.yotaniniaga.view.addGarden.AddGardenActivity
import com.javaindoku.yotaniniaga.view.addGarden.listener.DataChangedListener
import com.javaindoku.yotaniniaga.view.addGarden.listener.dataErrorListener
import com.javaindoku.yotaniniaga.viewmodel.AddGardenViewModel
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CertificateGardenFragment : BaseFragment(), AdapterView.OnItemSelectedListener,
    DataChangedListener, dataErrorListener {
    private lateinit var binding: FragmentCertificateGardenBinding

    @Inject
    lateinit var viewModel: AddGardenViewModel
    lateinit var parent: AddGardenActivity
    private lateinit var certificateTypeList: MutableList<CertificateType>
    private lateinit var certificateTypeAdapter: CustomSpinnerAdapter<CertificateType>
    private var PERMISSION_REQUEST_CODE = 131
    private var photoOrGallery: Int = 0
    private lateinit var imageUriData: Uri
    private lateinit var valuesData: ContentValues
    private lateinit var onNext: View.OnClickListener
    private var pic_certificate = 126
    private var fotoCertificateBase64: String = ""
    private var fotoCertificateZoom: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_certificate_garden, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parent = activity as AddGardenActivity
        parent.setCertificateDataChangeNotifier(this)
        bindView(parent.delegateGarden())
        binding.spinnerCertificateType.onItemSelectedListener = this
        binding.spinnerCertificateType.setTitle(getString(R.string.selectCertificateType))
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

        binding.btnImgCertificate.setOnClickListener {
            if (checkPersmission()) {
                valuesData = ContentValues()
                valuesData.put(MediaStore.Images.Media.TITLE, "New Picture")
                valuesData.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
                imageUriData = requireContext().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valuesData
                )!!
                showPictureDialog(pic_certificate)
            } else {
                requestPermission()
            }
        }

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

        binding.btnConfirmationPositive.setOnClickListener {
            parent.submit()
        }

        binding.btnConfirmationNegative.setOnClickListener {
            onNext.onClick(it)
        }

        binding.edtCertificateNumber.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateGarden().sertifikasiNumber = p0.toString()
            }
        })

        editMode()
    }

    private fun editMode() {
        if (parent.editMode == "false"){
            binding.spinnerCertificateType.isEnabled = false
            binding.spinnerCertificateType.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.edtCertificateNumber.isEnabled = false
            binding.edtCertificateNumber.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.edtDateFrom.isEnabled = false
            binding.edtDateFrom.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.edtDateTo.isEnabled = false
            binding.edtDateTo.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.btnImgCertificate.isEnabled = false
            binding.btnImgCertificate.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.btnConfirmationPositive.isEnabled = false
            binding.btnConfirmationPositive.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
        }
    }

    private fun getCertificateType() {
        viewModel.getCertificationType()
        viewModel.certificateTypeResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                parent.delegateLoader().show()
            } else if (it.status == ApiResult.Status.ERROR) {
                parent.delegateLoader().dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                val certificateTypes = it.data!!.data!!
                certificateTypeList = certificateTypes.toMutableList()
                certificateTypeAdapter = CustomSpinnerAdapter<CertificateType>(
                    requireActivity(),
                    certificateTypes
                )
                binding.spinnerCertificateType.adapter = certificateTypeAdapter
                if (!parent.delegateGarden().sertifikasiId.isNullOrEmpty()) {
                    binding.spinnerCertificateType.setSelection(certificateTypeAdapter.getItemPositionById(parent.delegateGarden().sertifikasiId.toString()))
                }
                parent.delegateLoader().dismiss()
            }
        })
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var iid = p0!!.getId()
        when (iid) {
            R.id.spinnerCertificateType -> {
                parent.delegateGarden().sertifikasiId = certificateTypeAdapter.items[p2].id.toString()
            }
        }
    }

    //Camera
    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
//                    showPictureDialog(requestCode)
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {

            }
        }
    }

    private fun showPictureDialog(setImage: Int) {
        val pictureDialog = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_title_bar, null)
        val txvLableTitle = view.findViewById<TextView>(R.id.txvLableTitle)
        txvLableTitle.setText(getString(R.string.srTitleDialogCamera))
        pictureDialog.setCustomTitle(view)
        val pictureDialogItems = arrayOf(
            getString(R.string.choose_Pict_gallery),
            getString(R.string.choose_Pict_photo)
        )
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> pickImageFromGallery(setImage)
                1 -> takePicture(setImage)
            }
        }
        pictureDialog.show()
    }

    private fun pickImageFromGallery(setImage: Int) {
        photoOrGallery = 2
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, setImage)
        }
    }

    private fun takePicture(setTakePicture: Int) {
        photoOrGallery = 1
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriData)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, setTakePicture)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                pic_certificate -> {
                    when (photoOrGallery) {
                        1 -> {
                            var resultSuratPicture: String =
                                CustomProfile.setImageTakeAPicture(
                                    requireActivity(),
                                    requireContext(),
                                    imageUriData,
                                    binding.imgCertificate
                                )
                            var imageBitmap: Bitmap =
                                SiliCompressor.with(requireContext()).getCompressBitmap(resultSuratPicture)
                            parent.delegateGarden().fotoSertifikatBase64 = "data:image/jpeg;base64," + encodeImage(imageBitmap)
                            fotoCertificateZoom = resultSuratPicture
                        }
                        2 -> {
                            var dataUri = data!!.data!!
                            var resultSuratPicture: String =
                                CustomProfile.setImageFromGallery(
                                    requireActivity(),
                                    requireContext(),
                                    dataUri,
                                    binding.imgCertificate
                                )
                            var imageBitmap: Bitmap =
                                SiliCompressor.with(requireContext()).getCompressBitmap(resultSuratPicture)
                            parent.delegateGarden().fotoSertifikatBase64 = "data:image/jpeg;base64," + encodeImage(imageBitmap)
                            fotoCertificateZoom = resultSuratPicture
                        }
                    }
                }
                else ->
                    Toast.makeText(requireContext(), R.string.srProfileFailedPicture, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun encodeImage(bm: Bitmap): String {
        var baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.NO_WRAP)
    }

    private fun showDateFromPickerDialog() {
        val c: Calendar = Calendar.getInstance()
        val mYear: Int = c.get(Calendar.YEAR)
        val mMonth: Int = c.get(Calendar.MONTH)
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = requireActivity().let {
            DatePickerDialog(it,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "dd/MM/yyyy" //In which you need put here
                    val sdf = SimpleDateFormat(myFormat)
                    binding.lblDateFrom.text = sdf.format(c.time)
                    parent.delegateGarden().sertifikasiDari = StringFormat.formatStringDate(
                        binding.lblDateFrom.text.toString(),
                        "dd/MM/yyyy",
                        "yyyy-MM-dd"
                    )
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
        val datePickerDialog = requireActivity().let {
            DatePickerDialog(it,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "dd/MM/yyyy" //In which you need put here
                    val sdf = SimpleDateFormat(myFormat)
                    binding.lblDateTo.text = sdf.format(c.time)
                    parent.delegateGarden().sertifikasiSampai = StringFormat.formatStringDate(
                        binding.lblDateTo.text.toString(),
                        "dd/MM/yyyy",
                        "yyyy-MM-dd"
                    )
                }, mYear, mMonth, mDay
            )
        }
        datePickerDialog.show()
    }

    override fun onDataChanged(gardenData: GardenDataSchema) {
        bindView(gardenData)
    }

    private fun bindView(gardenData: GardenDataSchema) {
        getCertificateType()
        if (!gardenData.sertifikasiDari.isNullOrBlank()) {
            binding.lblDateFrom.setText(StringFormat.formatStringDate(
                gardenData.sertifikasiDari.toString(),
                "yyyy-MM-dd",
                "dd/MM/yyyy")
            )
        }
        if (!gardenData.sertifikasiSampai.isNullOrBlank()) {
            binding.lblDateTo.setText(
                StringFormat.formatStringDate(
                    gardenData.sertifikasiSampai.toString(),
                    "yyyy-MM-dd",
                    "dd/MM/yyyy"
                )
            )
        }
        binding.edtCertificateNumber.setText(gardenData.sertifikasiNumber)
        if (parent.delegateGarden().fotoSertifikatBase64.isNullOrEmpty())
            CustomProfile.showRemoteImageUsingGlide(
                requireContext(),
                binding.imgCertificate,
                gardenData.fotoSertifikasi,
                R.drawable.bg_rectangle_white_black
            )
        else {
            val string64 = parent.delegateGarden().fotoSertifikatBase64.removeBase64DataString()
            Glide.with(requireContext())
                .asBitmap()
                .load(Base64.decode(string64, Base64.DEFAULT))
                .placeholder(R.drawable.bg_rectangle_white_black)
                .into(binding.imgCertificate);
        }
    }

    override fun onResume() {
        super.onResume()
        getCertificateType()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onNext = context as View.OnClickListener
    }

    override fun onError(garden: GardenDataSchema) {
        if (garden.sertifikasiNumber.isNullOrBlank()) {
            binding.edtCertificateNumber.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorCertificateNumber.visibility = View.VISIBLE
        } else {
            binding.edtCertificateNumber.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorCertificateNumber.visibility = View.GONE
        }

        if (garden.sertifikasiDari.isNullOrBlank()) {
            binding.edtDateFrom.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorFromDate.visibility = View.VISIBLE
        } else {
            binding.edtDateFrom.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorFromDate.visibility = View.GONE
        }

        if (garden.sertifikasiSampai.isNullOrBlank()) {
            binding.edtDateTo.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorToDate.visibility = View.VISIBLE
        } else {
            binding.edtDateTo.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorToDate.visibility = View.GONE
        }

        if (garden.fotoSertifikatBase64.isNullOrBlank() && garden.fotoSertifikasi.isNullOrBlank()) {
            binding.lblErrorPicture.visibility = View.VISIBLE
        } else {
            binding.lblErrorPicture.visibility = View.GONE
        }
    }

}