package com.javaindoku.yotaniniaga.view.addGarden.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import com.javaindoku.yotaniniaga.databinding.FragmentGardenAddBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.CropType
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenDataSchema
import com.javaindoku.yotaniniaga.model.gardenPetani.LandStatus
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.CustomProfile
import com.javaindoku.yotaniniaga.utilities.CustomSpinnerAdapter
import com.javaindoku.yotaniniaga.utilities.removeBase64DataString
import com.javaindoku.yotaniniaga.view.addGarden.AddGardenActivity
import com.javaindoku.yotaniniaga.view.addGarden.listener.DataChangedListener
import com.javaindoku.yotaniniaga.view.addGarden.listener.dataErrorListener
import com.javaindoku.yotaniniaga.viewmodel.AddGardenViewModel
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class GardenAddFragment : BaseFragment(), AdapterView.OnItemSelectedListener,
    DataChangedListener, dataErrorListener {
    private lateinit var binding: FragmentGardenAddBinding
    private lateinit var parent: AddGardenActivity
    @Inject
    lateinit var viewModel: AddGardenViewModel
    private lateinit var cropTypeList: MutableList<CropType>
    private lateinit var landStatusList: MutableList<LandStatus>
    private lateinit var cropAdapter: CustomSpinnerAdapter<CropType>
    private lateinit var landAdapter: CustomSpinnerAdapter<LandStatus>
    private lateinit var onNext: View.OnClickListener
    private var PERMISSION_REQUEST_CODE = 131
    private var photoOrGallery: Int = 0
    private lateinit var imageUriData: Uri
    private lateinit var valuesData: ContentValues
    private var pic_garden = 126
    private var fotoGardenZoom: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_garden_add, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parent = activity as AddGardenActivity
        parent.setGardenDataChangeNotifier(this)
        binding.spinnerLandCondition.onItemSelectedListener = this
        binding.spinnerLandCondition.setTitle(getString(R.string.selectLandCondition))
        binding.spinnerLandCondition.setOnSearchTextChangedListener {
            if (binding.spinnerLandCondition != null) {
                for (i in 0..landStatusList.size - 1) {
                    if(landStatusList.get(i).name.equals(binding.spinnerLandCondition.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }

                    if(i == (landStatusList.size-1)){
                        if(!landStatusList.get(i).name.equals(binding.spinnerLandCondition.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }
        binding.spinnerSeed.onItemSelectedListener = this
        binding.spinnerSeed.setTitle(getString(R.string.selectCropType))
        binding.spinnerSeed.setOnSearchTextChangedListener {
            if (binding.spinnerSeed != null) {
                for (i in 0..cropTypeList.size - 1) {
                    if(cropTypeList.get(i).name.equals(binding.spinnerSeed.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }

                    if(i == (cropTypeList.size-1)){
                        if(!cropTypeList.get(i).name.equals(binding.spinnerSeed.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }

        binding.btnImgGarden.setOnClickListener {
            if (checkPersmission()) {
                valuesData = ContentValues()
                valuesData.put(MediaStore.Images.Media.TITLE, "New Picture")
                valuesData.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
                imageUriData = requireContext().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valuesData
                )!!
                showPictureDialog(pic_garden)
            } else {
                requestPermission()
            }
        }
        binding.btnConfirmationPositive.setOnClickListener {
            onNext.onClick(it)
        }
        binding.btnConfirmationNegative.setOnClickListener{
            onNext.onClick(it)
        }
        binding.edtGardenArea.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateGarden().luasKebun = p0.toString()
            }
        })
        binding.edtTotalTree.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateGarden().jumlahPohon = p0.toString()
            }
        })
        binding.edtPlantingYear.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateGarden().tahunTanamId = p0.toString()
            }
        })
        binding.edtPotentialProduction.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateGarden().potensiProduksi = p0.toString()
            }
        })

        editMode()
    }

    private fun editMode() {
        if (parent.editMode == "false"){
            binding.edtGardenArea.isEnabled = false
            binding.edtGardenArea.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.edtTotalTree.isEnabled = false
            binding.edtTotalTree.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.edtPlantingYear.isEnabled = false
            binding.edtPlantingYear.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.edtPotentialProduction.isEnabled = false
            binding.edtPotentialProduction.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.spinnerSeed.isEnabled = false
            binding.spinnerSeed.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.spinnerLandCondition.isEnabled = false
            binding.spinnerLandCondition.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.btnImgGarden.isEnabled = false
            binding.btnImgGarden.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onNext = context as View.OnClickListener
    }

    private fun getCropType() {
        viewModel.getCropType()
        viewModel.cropTypeResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                parent.delegateLoader().show()
            } else if (it.status == ApiResult.Status.ERROR) {
                parent.delegateLoader().dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                parent.delegateLoader().dismiss()
                val cropTypes = it.data!!.data!!
                cropTypeList = cropTypes.toMutableList()
                cropAdapter = CustomSpinnerAdapter<CropType>(
                    requireActivity(),
                    cropTypes
                )
                binding.spinnerSeed.adapter = cropAdapter
                if (!parent.delegateGarden().jenisBibitId.isNullOrEmpty()) {
                    binding.spinnerSeed.setSelection(cropAdapter.getItemPositionById(parent.delegateGarden().jenisBibitId.toString()))
                }
            }
        })
    }

    private fun getLandStatus() {
        viewModel.getLandStatus()
        viewModel.landStatusResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                parent.delegateLoader().show()
            } else if (it.status == ApiResult.Status.ERROR) {
                parent.delegateLoader().dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                parent.delegateLoader().dismiss()
                val landStatuses = it.data!!.data!!
                landStatusList = landStatuses.toMutableList()
                landAdapter = CustomSpinnerAdapter<LandStatus>(
                    requireActivity(),
                    landStatuses
                )
                binding.spinnerLandCondition.adapter = landAdapter
                if (!parent.delegateGarden().statusLahanId.isNullOrEmpty()) {
                    binding.spinnerLandCondition.setSelection(landAdapter.getItemPositionById(parent.delegateGarden().statusLahanId.toString()))
                }
            }
        })
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var iid = p0!!.getId()
        when (iid) {
            R.id.spinnerSeed -> {
                parent.delegateGarden().jenisBibitId = cropAdapter.items[p2].id.toString()
            }

            //Kabupaten Kota
            R.id.spinnerLandCondition -> {
                parent.delegateGarden().statusLahanId = landAdapter.items[p2].id.toString()
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
                pic_garden -> {
                    when (photoOrGallery) {
                        1 -> {
                            var resultSuratPicture: String =
                                CustomProfile.setImageTakeAPicture(
                                    requireActivity(),
                                    requireContext(),
                                    imageUriData,
                                    binding.imgGarden
                                )
                            var imageBitmap: Bitmap =
                                SiliCompressor.with(requireContext()).getCompressBitmap(resultSuratPicture)
                            parent.delegateGarden().fotoBase64 = "data:image/jpeg;base64," + encodeImage(imageBitmap)
                            fotoGardenZoom = resultSuratPicture
                        }
                        2 -> {
                            var dataUri = data!!.data!!
                            var resultSuratPicture: String =
                                CustomProfile.setImageFromGallery(
                                    requireActivity(),
                                    requireContext(),
                                    dataUri,
                                    binding.imgGarden
                                )
                            var imageBitmap: Bitmap =
                                SiliCompressor.with(requireContext()).getCompressBitmap(resultSuratPicture)
                            parent.delegateGarden().fotoBase64 = "data:image/jpeg;base64," + encodeImage(imageBitmap)
                            fotoGardenZoom = resultSuratPicture
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

    fun bindView(gardenData: GardenDataSchema) {
        binding.edtGardenArea.setText(gardenData.luasKebun)
        binding.edtPlantingYear.setText(gardenData.tahunTanamId)
        binding.edtPotentialProduction.setText(gardenData.potensiProduksi)
        binding.edtTotalTree.setText(gardenData.jumlahPohon)
        getLandStatus()
        getCropType()
        if (parent.delegateGarden().fotoBase64.isNullOrEmpty()) {
            CustomProfile.showRemoteImageUsingGlide(
                requireContext(),
                binding.imgGarden,
                gardenData.foto,
                R.drawable.bg_rectangle_white_black
            )
        } else {
            val string64 = parent.delegateGarden().fotoBase64.removeBase64DataString()
            Glide.with(requireContext())
                .asBitmap()
                .load(Base64.decode(string64, Base64.DEFAULT))
                .placeholder(R.drawable.bg_rectangle_white_black)
                .into(binding.imgGarden);
        }
    }

    override fun onDataChanged(gardenData: GardenDataSchema) {
        bindView(gardenData)
    }

    override fun onResume() {
        super.onResume()
        bindView(parent.delegateGarden())
    }

    override fun onError(garden: GardenDataSchema) {
        if (garden.luasKebun.isNullOrBlank()) {
            binding.edtGardenArea.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorGardenArea.visibility = View.VISIBLE
        } else {
            binding.edtGardenArea.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorGardenArea.visibility = View.GONE
        }

        if (garden.jumlahPohon.isNullOrBlank()) {
            binding.edtTotalTree.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorTotalTree.visibility = View.VISIBLE
        } else {
            binding.edtTotalTree.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorTotalTree.visibility = View.GONE
        }

        if (garden.tahunTanamId.isNullOrBlank()) {
            binding.edtPlantingYear.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorPlantingYear.visibility = View.VISIBLE
        } else {
            binding.edtPlantingYear.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorPlantingYear.visibility = View.GONE
        }

        if (garden.potensiProduksi.isNullOrBlank()) {
            binding.edtPotentialProduction.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorPotentialProduction.visibility = View.VISIBLE
        } else {
            binding.edtPotentialProduction.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorPotentialProduction.visibility = View.GONE
        }
    }
}