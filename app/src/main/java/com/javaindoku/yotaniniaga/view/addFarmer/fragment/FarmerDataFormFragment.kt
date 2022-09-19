package com.javaindoku.yotaniniaga.view.addFarmer.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentFarmerEditBinding
import com.javaindoku.yotaniniaga.databinding.FragmentFarmerEditKoperasiBinding
import com.javaindoku.yotaniniaga.di.factory.ViewModelFactory
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.utilities.CustomProfile
import com.javaindoku.yotaniniaga.utilities.CustomSpinnerAdapter
import com.javaindoku.yotaniniaga.view.addFarmer.AddFarmerActivity
import com.javaindoku.yotaniniaga.view.addFarmer.listener.DataChangedListener
import com.javaindoku.yotaniniaga.view.addFarmer.listener.DataErrorListener
import java.util.ArrayList
import javax.inject.Inject

class FarmerDataFormFragment : BaseFragment(), AdapterView.OnItemSelectedListener,
    DataChangedListener, DataErrorListener {
    private lateinit var binding: FragmentFarmerEditKoperasiBinding
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private var PERMISSION_REQUEST_CODE = 131
    private var pic_profile_picture = 126
    private var photoOrGallery: Int = 0
    private lateinit var imageUriData: Uri
    private lateinit var parent: AddFarmerActivity
    private lateinit var valuesData: ContentValues
    private lateinit var onNextListener: View.OnClickListener
    private lateinit var listBank: MutableList<com.javaindoku.yotaniniaga.model.profile.bank.Data>
    private lateinit var arrayAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.bank.Data>

    //For Zooming
    private var fotoProfileZoom: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_farmer_edit_koperasi, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parent = activity as AddFarmerActivity
        parent.setFarmerDataChangeNotifier(this)
        binding.btnConfirmationPositive.setOnClickListener {
            onNextListener.onClick(it)
        }
        binding.btnConfirmationNegative.setOnClickListener{
                view -> parent.finish()
        }
        binding.spinnerBank.onItemSelectedListener = this
        binding.spinnerBank.setTitle(getString(R.string.srSelectBank))
        binding.spinnerBank.setOnSearchTextChangedListener {
            if (binding.spinnerBank != null) {
                for (i in 0..listBank.size - 1) {
                    if(listBank.get(i).bankName.equals(binding.spinnerBank.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }
                    if(i == (listBank.size-1)){
                        if(!listBank.get(i).bankName.equals(binding.spinnerBank.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }
        binding.btnImage.setOnClickListener {
            if (checkPersmission()) {
                valuesData = ContentValues()
                valuesData.put(MediaStore.Images.Media.TITLE, "New Picture")
                valuesData.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
                imageUriData = requireContext().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valuesData)!!
                showPictureDialog(pic_profile_picture)
            } else {
                requestPermission()
            }
        }
        binding.edtFarmerName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateFarmer().nama = p0.toString()
            }
        })
        binding.edtPhoneNumber.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateFarmer().mobile = p0.toString()
            }
        })
        binding.edtNameOfBank.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateFarmer().akunBank = p0.toString()
            }
        })
        binding.edtNoOfBank.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateFarmer().noRekening = p0.toString()
            }
        })
        binding.edtPhoneNumber.setText(parent.delegateFarmer().mobile)
    }

    //Camera
    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
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
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                pic_profile_picture -> {
                    when (photoOrGallery) {
                        1 -> {
                            var resultProfilePicture: String =
                                CustomProfile.setImageTakeAPicture(
                                    requireActivity(),
                                    requireContext(),
                                    imageUriData,
                                    binding.imgProfile
                                )
                            parent.imageUri = resultProfilePicture
                            fotoProfileZoom = resultProfilePicture
                        }
                        2 -> {
                            var dataUri = data!!.data!!
                            var resultProfilePicture: String =
                                CustomProfile.setImageFromGallery(
                                    requireActivity(),
                                    requireContext(),
                                    dataUri,
                                    binding.imgProfile
                                )
                            parent.imageUri = resultProfilePicture
                            fotoProfileZoom = resultProfilePicture
                        }
                    }
                }
                else ->
                    Toast.makeText(activity, R.string.srProfileFailedPicture, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun bindProfile(data: Data?) {
        binding.edtFarmerName.setText(data?.namaPetani)
        binding.edtPhoneNumber.setText(data?.mobile)
        binding.edtNameOfBank.setText(data?.akunBank)
        binding.edtNoOfBank.setText(data?.noRekening)
        CustomProfile.showRemoteImageUsingGlide(
            requireContext(),
            binding.imgProfile,
            parent.delegateFarmer().foto,
            R.drawable.ic_businessman
        )
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var iid = p0!!.getId()
        when (iid) {
            R.id.spinnerBank -> {
                var bank = arrayAdapter.getItemData(p2)
                parent.delegateFarmer().bankId = bank.bankId
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onNextListener = context as View.OnClickListener
    }

    override fun onDataChanged(petaniData: Data) {
        bindProfile(petaniData)
    }

    override fun onBankListChanged(bankList: ArrayList<com.javaindoku.yotaniniaga.model.profile.bank.Data>) {
        arrayAdapter = CustomSpinnerAdapter(
            requireContext(),
            bankList
        )
        binding.spinnerBank.adapter = arrayAdapter
        if (!parent.delegateFarmer().bankId.isNullOrEmpty()) {
            binding.spinnerBank.setSelection(arrayAdapter.getItemPositionById(parent.delegateFarmer().bankId.toString()))
        }
    }

    override fun onError(farmer: Data) {
        if (farmer.nama.isNullOrBlank()) {
            binding.edtFarmerName.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorFarmerName.visibility = View.VISIBLE
        } else {
            binding.edtFarmerName.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorFarmerName.visibility = View.GONE
        }

        if (farmer.mobile.isNullOrBlank()) {
            binding.edtPhoneNumber.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorPhoneNumber.visibility = View.VISIBLE
        } else {
            binding.edtPhoneNumber.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorPhoneNumber.visibility = View.GONE
        }

        if (farmer.akunBank.isNullOrBlank()) {
            binding.edtNameOfBank.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorNameOfBank.visibility = View.VISIBLE
        } else {
            binding.edtNameOfBank.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorNameOfBank.visibility = View.GONE
        }

        if (farmer.noRekening.isNullOrBlank()) {
            binding.edtNoOfBank.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorNoBank.visibility = View.VISIBLE
        } else {
            binding.edtNoOfBank.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorNoBank.visibility = View.GONE
        }
    }
}