package com.javaindoku.yotaniniaga.view.editprofilepetani.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentFarmerEdit2Binding
import com.javaindoku.yotaniniaga.databinding.FragmentFarmerEditBinding
import com.javaindoku.yotaniniaga.di.factory.ViewModelFactory
import com.javaindoku.yotaniniaga.model.ocrKtp.OcrKtpData
import com.javaindoku.yotaniniaga.model.profile.agama.DataAgama
import com.javaindoku.yotaniniaga.model.profile.bank.Bank
import com.javaindoku.yotaniniaga.model.profile.bidangpekerjaan.DataBidangPekerjaan
import com.javaindoku.yotaniniaga.model.profile.pendidikan.DataPendidikan
import com.javaindoku.yotaniniaga.model.profile.pengalamankerja.DataPengalamanKerja
import com.javaindoku.yotaniniaga.model.profile.profesi.DataProfesi
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.CustomProfile
import com.javaindoku.yotaniniaga.utilities.CustomSpinnerAdapter
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.view.editprofilepetani.EditProfilePetaniActivity
import com.javaindoku.yotaniniaga.view.editprofilepetani.listener.DataChangedListenerProfile
import com.javaindoku.yotaniniaga.view.editprofilepetani.listener.FarmerDataErrorListener
import com.javaindoku.yotaniniaga.viewmodel.EditProfilePetaniViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class FarmerEditFragment : BaseFragment(), AdapterView.OnItemSelectedListener, DataChangedListenerProfile,
    FarmerDataErrorListener {
    private lateinit var binding: FragmentFarmerEditBinding
    @Inject
    lateinit var viewModelEditProfile: EditProfilePetaniViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var PERMISSION_REQUEST_CODE = 131
    private var pic_profile_picture = 126
    private var pic_ktp_picture = 127
    private var pic_bpjstk_picture = 128
    private var pic_bpjskes_picture = 129
    private var pic_kk_picture = 130
    private var photoOrGallery: Int = 0
    private lateinit var imageUriData: Uri
    private lateinit var parent: EditProfilePetaniActivity
    private lateinit var valuesData: ContentValues
    private lateinit var onNextListener: View.OnClickListener
    private lateinit var listBank: MutableList<com.javaindoku.yotaniniaga.model.profile.bank.Data>
    private lateinit var listAgama: MutableList<DataAgama>
    private lateinit var listPendidikan: MutableList<DataPendidikan>
    private lateinit var listProfesi: MutableList<DataProfesi>
    private lateinit var listBidangPekerjaan: MutableList<DataBidangPekerjaan>
    private lateinit var listPengalamanKerja: MutableList<DataPengalamanKerja>
    private lateinit var arrayAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.bank.Data>
    private lateinit var arrayAdapterAgama: CustomSpinnerAdapter<DataAgama>
    private lateinit var arrayAdapterPendidikan: CustomSpinnerAdapter<DataPendidikan>
    private lateinit var arrayAdapterProfesi: CustomSpinnerAdapter<DataProfesi>
    private lateinit var arrayAdapterBidangPekerjaan: CustomSpinnerAdapter<DataBidangPekerjaan>
    private lateinit var arrayAdapterPengalamanKerja: CustomSpinnerAdapter<DataPengalamanKerja>

    //For Zooming
    private var fotoProfileZoom: String = ""
    private var fotoKTPZoom: String = ""
    private var fotoBpjsTkZoom: String = ""
    private var fotoBpjsKesZoom: String = ""
    private var fotoKKZoom: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_farmer_edit, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parent = activity as EditProfilePetaniActivity
        parent.setDataChangeNotifier(this)
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

        binding.spinnerReligion.onItemSelectedListener = this
        binding.spinnerReligion.setTitle(getString(R.string.srSelectReligion))
        binding.spinnerReligion.setOnSearchTextChangedListener {
            if (binding.spinnerReligion != null) {
                for (i in 0..listAgama.size - 1) {
                    if(listAgama.get(i).agama_deskripsi.equals(binding.spinnerReligion.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }
                    if(i == (listAgama.size-1)){
                        if(!listAgama.get(i).agama_deskripsi.equals(binding.spinnerReligion.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }

        binding.spinnerEducation.onItemSelectedListener = this
        binding.spinnerEducation.setTitle(getString(R.string.srSelectEducation))
        binding.spinnerEducation.setOnSearchTextChangedListener {
            if (binding.spinnerEducation != null) {
                for (i in 0..listPendidikan.size - 1) {
                    if(listPendidikan.get(i).deskripsi.equals(binding.spinnerEducation.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }
                    if(i == (listPendidikan.size-1)){
                        if(!listPendidikan.get(i).deskripsi.equals(binding.spinnerEducation.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }

        binding.spinnerProfession.onItemSelectedListener = this
        binding.spinnerProfession.setTitle(getString(R.string.srSelectProfesion))
        binding.spinnerProfession.setOnSearchTextChangedListener {
            if (binding.spinnerProfession != null) {
                for (i in 0..listProfesi.size - 1) {
                    if(listProfesi.get(i).pekerjaan_deskripsi.equals(binding.spinnerProfession.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }
                    if(i == (listProfesi.size-1)){
                        if(!listProfesi.get(i).pekerjaan_deskripsi.equals(binding.spinnerProfession.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }

        binding.spinnerFieldWork.onItemSelectedListener = this
        binding.spinnerFieldWork.setTitle(getString(R.string.srSelectFieldOfWrok))
        binding.spinnerFieldWork.setOnSearchTextChangedListener {
            if (binding.spinnerFieldWork != null) {
                for (i in 0..listBidangPekerjaan.size - 1) {
                    if(listBidangPekerjaan.get(i).nama.equals(binding.spinnerFieldWork.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }
                    if(i == (listBidangPekerjaan.size-1)){
                        if(!listBidangPekerjaan.get(i).nama.equals(binding.spinnerFieldWork.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }

        binding.spinnerWorkExperience.onItemSelectedListener = this
        binding.spinnerWorkExperience.setTitle(getString(R.string.srSelectWorkEcperience))
        binding.spinnerWorkExperience.setOnSearchTextChangedListener {
            if (binding.spinnerWorkExperience != null) {
                for (i in 0..listPengalamanKerja.size - 1) {
                    if(listPengalamanKerja.get(i).deskripsi.equals(binding.spinnerWorkExperience.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }
                    if(i == (listPengalamanKerja.size-1)){
                        if(!listPengalamanKerja.get(i).deskripsi.equals(binding.spinnerWorkExperience.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }

        // GET BIRTH DATE
        binding.edtBirthDate.setOnClickListener {
            showDatePickerDialogBirthDate()
        }
        binding.lblBirthDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty()) {
                    binding.imgBirthDate.setImageResource(R.drawable.ic_close_black_24dp)
                    binding.imgBirthDate.setOnClickListener {
                        binding.lblBirthDate.setText("")
                    }
                } else {
                    binding.imgBirthDate.setImageResource(R.drawable.kalender)
                    binding.imgBirthDate.setOnClickListener {
                        showDatePickerDialogBirthDate()
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

        // CHECK GENDER
        binding.lytCheckMale.setOnClickListener {
            binding.lytCheckMale.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckMale.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckMale.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            binding.lytCheckFemale.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckFemale.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckFemale.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            parent.delegateProfile().jenisKelamin = "M"
        }
        binding.lytCheckFemale.setOnClickListener {
            binding.lytCheckMale.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckMale.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckMale.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            binding.lytCheckFemale.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckFemale.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckFemale.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            parent.delegateProfile().jenisKelamin = "F"
        }

        // CHECK STATUS NIKAH
        binding.lytCheckSudahNikah.setOnClickListener {
            binding.lytCheckSudahNikah.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckSudahNikah.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckSudahNikah.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            binding.lytCheckBelumNikah.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckBelumNikah.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckBelumNikah.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            parent.delegateProfile().statusNikahId = "1"
        }
        binding.lytCheckBelumNikah.setOnClickListener {
            binding.lytCheckSudahNikah.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckSudahNikah.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckSudahNikah.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            binding.lytCheckBelumNikah.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckBelumNikah.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckBelumNikah.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            parent.delegateProfile().statusNikahId = "2"
        }

        // CHECK WORK ONLINE OR OFFLINE
        binding.lytCheckOnline.setOnClickListener {
            binding.lytCheckOnline.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckOnline.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckOnline.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            binding.lytCheckOffline.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckOffline.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckOffline.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            parent.delegateProfile().onlineWorkId = "1"
        }
        binding.lytCheckOffline.setOnClickListener {
            binding.lytCheckOnline.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckOnline.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckOnline.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            binding.lytCheckOffline.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckOffline.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckOffline.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            parent.delegateProfile().onlineWorkId = "2"
        }

        // CHECK STATUS RUMAH
        binding.lytCheckRumahSendiri.setOnClickListener {
            binding.lytCheckRumahSendiri.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckRumahSendiri.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckRumahSendiri.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            binding.lytCheckRumahTidakSendiri.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckRumahTidakSendiri.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckRUmahTidakSendiri.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            parent.delegateProfile().homeOwnershipId = "1"
        }
        binding.lytCheckRumahTidakSendiri.setOnClickListener {
            binding.lytCheckRumahSendiri.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckRumahSendiri.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckRumahSendiri.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            binding.lytCheckRumahTidakSendiri.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckRumahTidakSendiri.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckRUmahTidakSendiri.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            parent.delegateProfile().homeOwnershipId = "2"
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

        binding.btnUnggahKTP.setOnClickListener {
            if (checkPersmission()) {
                valuesData = ContentValues()
                valuesData.put(MediaStore.Images.Media.TITLE, "New Picture")
                valuesData.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
                imageUriData = requireContext().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valuesData)!!
                showPictureDialog(pic_ktp_picture)
            } else {
                requestPermission()
            }
        }

        binding.btnUnggahBPJSTK.setOnClickListener {
            if (checkPersmission()) {
                valuesData = ContentValues()
                valuesData.put(MediaStore.Images.Media.TITLE, "New Picture")
                valuesData.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
                imageUriData = requireContext().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valuesData)!!
                showPictureDialog(pic_bpjstk_picture)
            } else {
                requestPermission()
            }
        }

        binding.btnUnggahBPJSKes.setOnClickListener {
            if (checkPersmission()) {
                valuesData = ContentValues()
                valuesData.put(MediaStore.Images.Media.TITLE, "New Picture")
                valuesData.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
                imageUriData = requireContext().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valuesData)!!
                showPictureDialog(pic_bpjskes_picture)
            } else {
                requestPermission()
            }
        }

        binding.btnUnggahKK.setOnClickListener {
            if (checkPersmission()) {
                valuesData = ContentValues()
                valuesData.put(MediaStore.Images.Media.TITLE, "New Picture")
                valuesData.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
                imageUriData = requireContext().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valuesData)!!
                showPictureDialog(pic_kk_picture)
            } else {
                requestPermission()
            }
        }

        binding.edtFarmerName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().nama = p0.toString()
            }
        })
        binding.edtPhoneNumber.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().mobile = p0.toString()
            }
        })
        binding.edtNameOfBank.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().akunBank = p0.toString()
            }
        })
        binding.edtNoOfBank.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().noRekening = p0.toString()
            }
        })
        binding.edtEmail.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().email = p0.toString()
            }
        })
        binding.edtIDCard.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().noKtp = p0.toString()
            }
        })
        binding.edtNPWP.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().npwp = p0.toString()
            }
        })
        binding.edtBirthPlace.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().tempatLahir = p0.toString()
            }
        })
        binding.edtIncome.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().gaji= p0.toString()
            }
        })
        binding.edtTotalAset.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().totalAset = p0.toString()
            }
        })
        binding.edtBPJSTK.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().bpjsTk = p0.toString()
            }
        })
        binding.edtBPJSKes.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().bpjsKes = p0.toString()
            }
        })
        binding.edtKK.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().noKk = p0.toString()
            }
        })
    }

    //Camera
    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
                pic_ktp_picture -> {
                    when (photoOrGallery) {
                        1 -> {
                            var resultKtpPicture: String =
                                CustomProfile.setImageTakeAPicture(
                                    requireActivity(),
                                    requireContext(),
                                    imageUriData,
                                    binding.imgKTP
                                )
                            parent.imageUriKTP = resultKtpPicture
                            fotoKTPZoom = resultKtpPicture
                            parent.sendOcrKtp()
                        }
                        2 -> {
                            var dataUri = data!!.data!!
                            var resultKtpPicture: String =
                                CustomProfile.setImageFromGallery(
                                    requireActivity(),
                                    requireContext(),
                                    dataUri,
                                    binding.imgKTP
                                )
                            parent.imageUriKTP = resultKtpPicture
                            fotoKTPZoom = resultKtpPicture
                            parent.sendOcrKtp()
                        }
                    }
                }
                pic_bpjstk_picture -> {
                    when (photoOrGallery) {
                        1 -> {
                            var resultBpjsTkPicture: String =
                                CustomProfile.setImageTakeAPicture(
                                    requireActivity(),
                                    requireContext(),
                                    imageUriData,
                                    binding.imgBPJSTK
                                )
                            parent.imageUriBpjsTk = resultBpjsTkPicture
                            fotoBpjsTkZoom = resultBpjsTkPicture
                        }
                        2 -> {
                            var dataUri = data!!.data!!
                            var resultBpjsTkPicture: String =
                                CustomProfile.setImageFromGallery(
                                    requireActivity(),
                                    requireContext(),
                                    dataUri,
                                    binding.imgBPJSTK
                                )
                            parent.imageUriBpjsTk = resultBpjsTkPicture
                            fotoBpjsTkZoom = resultBpjsTkPicture
                        }
                    }
                }
                pic_bpjskes_picture -> {
                    when (photoOrGallery) {
                        1 -> {
                            var resultBpjsKesPicture: String =
                                CustomProfile.setImageTakeAPicture(
                                    requireActivity(),
                                    requireContext(),
                                    imageUriData,
                                    binding.imgBPJSKes
                                )
                            parent.imageUriBpjsKes = resultBpjsKesPicture
                            fotoBpjsKesZoom = resultBpjsKesPicture
                        }
                        2 -> {
                            var dataUri = data!!.data!!
                            var resultBpjsKesPicture: String =
                                CustomProfile.setImageFromGallery(
                                    requireActivity(),
                                    requireContext(),
                                    dataUri,
                                    binding.imgBPJSKes
                                )
                            parent.imageUriBpjsKes = resultBpjsKesPicture
                            fotoBpjsKesZoom = resultBpjsKesPicture
                        }
                    }
                }
                pic_kk_picture -> {
                    when (photoOrGallery) {
                        1 -> {
                            var resultKKPicture: String =
                                CustomProfile.setImageTakeAPicture(
                                    requireActivity(),
                                    requireContext(),
                                    imageUriData,
                                    binding.imgKK
                                )
                            parent.imageUriKK = resultKKPicture
                            fotoKKZoom = resultKKPicture
                        }
                        2 -> {
                            var dataUri = data!!.data!!
                            var resultKKPicture: String =
                                CustomProfile.setImageFromGallery(
                                    requireActivity(),
                                    requireContext(),
                                    dataUri,
                                    binding.imgKK
                                )
                            parent.imageUriKK = resultKKPicture
                            fotoKKZoom = resultKKPicture
                        }
                    }
                }
                else ->
                    Toast.makeText(activity, R.string.srProfileFailedPicture, Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun bindProfile(data: Data) {
        binding.edtFarmerName.setText(data.nama)
        binding.edtPhoneNumber.setText(data.mobile)
        binding.edtEmail.setText(data.email)
        binding.edtNameOfBank.setText(data.akunBank)
        binding.edtNoOfBank.setText(data.noRekening)
        binding.edtEmail.setText(data.email)
        binding.edtIDCard.setText(data.noKtp)
        binding.edtNPWP.setText(data.npwp)
        binding.edtBirthPlace.setText(data.tempatLahir)
        binding.lblBirthDate.setText(data.tanggalLahir)
        binding.edtIncome.setText(data.gaji)
        binding.edtTotalAset.setText(data.totalAset)
        binding.edtBPJSTK.setText(data.bpjsTk)
        binding.edtBPJSKes.setText(data.bpjsKes)
        binding.edtKK.setText(data.noKk)
        prefHelper.setStringToShared(SharedPrefKeys.IMAGE_PROFILE, data.foto.toString())
        CustomProfile.showRemoteImageUsingGlide(requireContext(), binding.imgProfile, data.foto.toString(), R.drawable.ic_businessman)
        CustomProfile.showRemoteImageUsingGlide(requireContext(), binding.imgKTP, data.fotoKtp.toString(), R.drawable.bg_rectangle_white_black)
        CustomProfile.showRemoteImageUsingGlide(requireContext(), binding.imgBPJSTK, data.fotoBpjsTk.toString(), R.drawable.bg_rectangle_white_black)
        CustomProfile.showRemoteImageUsingGlide(requireContext(), binding.imgBPJSKes, data.fotoBpjsKes.toString(), R.drawable.bg_rectangle_white_black)
        CustomProfile.showRemoteImageUsingGlide(requireContext(), binding.imgKK, data.fotoKk.toString(), R.drawable.bg_rectangle_white_black)

        // CHECK GENDER
        if (data.jenisKelamin == "M" || data.jenisKelamin.isNullOrEmpty()){
            binding.lytCheckMale.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckMale.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckMale.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            binding.lytCheckFemale.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckFemale.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckFemale.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            parent.delegateProfile().jenisKelamin = "M"
        }else{
            binding.lytCheckMale.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckMale.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckMale.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            binding.lytCheckFemale.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckFemale.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckFemale.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            parent.delegateProfile().jenisKelamin = "F"
        }

        // CHECK STATUS NIKAH
        if (data.statusNikahId == "1" || data.statusNikahId.isNullOrEmpty()){
            binding.lytCheckSudahNikah.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckSudahNikah.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckSudahNikah.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            binding.lytCheckBelumNikah.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckBelumNikah.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckBelumNikah.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            parent.delegateProfile().statusNikahId = "1"
        }else{
            binding.lytCheckSudahNikah.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckSudahNikah.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckSudahNikah.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            binding.lytCheckBelumNikah.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckBelumNikah.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckBelumNikah.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            parent.delegateProfile().statusNikahId = "2"
        }

        // CHECK WORK ONLINE OF OFFLINE
        if (data.onlineWorkId == "1" || data.onlineWorkId.isNullOrEmpty()){
            binding.lytCheckOnline.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckOnline.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckOnline.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            binding.lytCheckOffline.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckOffline.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckOffline.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            parent.delegateProfile().onlineWorkId = "1"
        }else{
            binding.lytCheckOnline.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckOnline.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckOnline.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            binding.lytCheckOffline.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckOffline.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckOffline.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            parent.delegateProfile().onlineWorkId = "2"
        }

        // CHECK STATUS RUMAH
        if (data.homeOwnershipId == "1" || data.homeOwnershipId.isNullOrEmpty()){
            binding.lytCheckRumahSendiri.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckRumahSendiri.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckRumahSendiri.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            binding.lytCheckRumahTidakSendiri.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckRumahTidakSendiri.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckRUmahTidakSendiri.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            parent.delegateProfile().homeOwnershipId = "1"
        }else{
            binding.lytCheckRumahSendiri.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgCheckRumahSendiri.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblCheckRumahSendiri.setTextColor(ContextCompat.getColor(requireContext(), R.color.ligh_gray2))
            binding.lytCheckRumahTidakSendiri.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgCheckRumahTidakSendiri.setImageResource(R.drawable.ic_radio_active)
            binding.lblCheckRUmahTidakSendiri.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            parent.delegateProfile().homeOwnershipId = "2"
        }

    }

    private fun bindOcrKtp(ocrKtpData: OcrKtpData) {
        binding.edtIDCard.setText(ocrKtpData.nIK?.value.toString())
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var iid = p0!!.getId()
        when (iid) {
            R.id.spinnerBank -> {
                var bank = arrayAdapter.getItemData(p2)
                parent.delegateProfile().bankId = bank.bankId
            }

            R.id.spinnerReligion -> {
                var religion = arrayAdapterAgama.getItemData(p2)
                parent.delegateProfile().agamaId = religion.agama_id
            }

            R.id.spinnerEducation -> {
                var education = arrayAdapterPendidikan.getItemData(p2)
                parent.delegateProfile().pendidikanId = education.id
            }

            R.id.spinnerProfession -> {
                var profession = arrayAdapterProfesi.getItemData(p2)
                parent.delegateProfile().pekerjaanId = profession.pekerjaan_id
            }

            R.id.spinnerFieldWork -> {
                var fieldWork = arrayAdapterBidangPekerjaan.getItemData(p2)
                parent.delegateProfile().industriId = fieldWork.id
            }

            R.id.spinnerWorkExperience -> {
                var workExperience = arrayAdapterPengalamanKerja.getItemData(p2)
                parent.delegateProfile().periodePengalamanId = workExperience.id
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDataChanged(profile: Data) {
        bindProfile(profile)
    }

    override fun onOcrKtpChanged(ocrKtpData: OcrKtpData) {
        bindOcrKtp(ocrKtpData)
    }

    override fun onBankListChanged(bankList: List<com.javaindoku.yotaniniaga.model.profile.bank.Data>) {
        listBank = bankList.toMutableList()
        arrayAdapter = CustomSpinnerAdapter(requireActivity(), bankList)
        binding.spinnerBank.adapter = arrayAdapter
        if (!parent.delegateProfile().bankId.isNullOrEmpty())
            binding.spinnerBank.setSelection(arrayAdapter.getItemPositionById(parent.delegateProfile().bankId.toString()))
    }

    override fun onReligionListChanged(religionList: List<DataAgama>) {
        listAgama = religionList.toMutableList()
        arrayAdapterAgama = CustomSpinnerAdapter(requireActivity(), religionList)
        binding.spinnerReligion.adapter = arrayAdapterAgama
        if (!parent.delegateProfile().agamaId.isNullOrEmpty())
            binding.spinnerReligion.setSelection(arrayAdapterAgama.getItemPositionById(parent.delegateProfile().agamaId.toString()))
    }

    override fun onEducationListChanged(educationList: List<DataPendidikan>) {
        listPendidikan = educationList.toMutableList()
        arrayAdapterPendidikan = CustomSpinnerAdapter(requireActivity(), educationList)
        binding.spinnerEducation.adapter = arrayAdapterPendidikan
        if (!parent.delegateProfile().pendidikanId.isNullOrEmpty())
            binding.spinnerEducation.setSelection(arrayAdapterPendidikan.getItemPositionById(parent.delegateProfile().pendidikanId.toString()))
    }

    override fun onProfessionListChanged(professinoList: List<DataProfesi>) {
        listProfesi = professinoList.toMutableList()
        arrayAdapterProfesi = CustomSpinnerAdapter(requireActivity(), professinoList)
        binding.spinnerProfession.adapter = arrayAdapterProfesi
        if (!parent.delegateProfile().pekerjaanId.isNullOrEmpty())
            binding.spinnerProfession.setSelection(arrayAdapterProfesi.getItemPositionById(parent.delegateProfile().pekerjaanId.toString()))
    }

    override fun onFieldWorkListChanged(fieldWorkList: List<DataBidangPekerjaan>) {
        listBidangPekerjaan = fieldWorkList.toMutableList()
        arrayAdapterBidangPekerjaan = CustomSpinnerAdapter(requireActivity(), fieldWorkList)
        binding.spinnerFieldWork.adapter = arrayAdapterBidangPekerjaan
        if (!parent.delegateProfile().industriId.isNullOrEmpty())
            binding.spinnerFieldWork.setSelection(arrayAdapterBidangPekerjaan.getItemPositionById(parent.delegateProfile().industriId.toString()))

    }

    override fun onWorkExperienceListChanged(workExperienceList: List<DataPengalamanKerja>) {
        listPengalamanKerja = workExperienceList.toMutableList()
        arrayAdapterPengalamanKerja = CustomSpinnerAdapter(requireActivity(), workExperienceList)
        binding.spinnerWorkExperience.adapter = arrayAdapterPengalamanKerja
        if (!parent.delegateProfile().periodePengalamanId.isNullOrEmpty())
            binding.spinnerWorkExperience.setSelection(arrayAdapterPengalamanKerja.getItemPositionById(parent.delegateProfile().periodePengalamanId.toString()))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onNextListener = context as View.OnClickListener
    }

    override fun onError(profile: Data) {
        if (profile.nama.isNullOrBlank()) {
            binding.edtFarmerName.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorFarmerName.visibility = VISIBLE
        } else {
            binding.edtFarmerName.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorFarmerName.visibility = GONE
        }

        if (profile.mobile.isNullOrBlank()) {
            binding.edtPhoneNumber.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorPhoneNumber.visibility = VISIBLE
        } else {
            binding.edtPhoneNumber.setBackgroundResource(R.drawable.shape_disabled_edittext)
            binding.lblErrorPhoneNumber.visibility = GONE
        }

        if (profile.akunBank.isNullOrBlank()) {
            binding.edtNameOfBank.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorNameOfBank.visibility = VISIBLE
        } else {
            binding.edtNameOfBank.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorNameOfBank.visibility = GONE
        }

        if (profile.noRekening.isNullOrBlank()) {
            binding.edtNoOfBank.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorNoBank.visibility = VISIBLE
        } else {
            binding.edtNoOfBank.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorNoBank.visibility = GONE
        }

        if (profile.email.isNullOrBlank()) {
            binding.edtEmail.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorEmail.visibility = VISIBLE
        } else {
            binding.edtEmail.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorEmail.visibility = GONE
        }

        if (profile.noKtp.isNullOrBlank() || profile.noKtp.toString().trim().length < 15
            || profile.noKtp.toString().trim().length > 16) {
            binding.edtIDCard.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorIdCard.visibility = VISIBLE
        } else {
            binding.edtIDCard.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorIdCard.visibility = GONE
        }

        if (profile.npwp.isNullOrBlank() || profile.npwp.toString().trim().length != 15) {
            binding.edtNPWP.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorNPWP.visibility = VISIBLE
        } else {
            binding.edtNPWP.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorNPWP.visibility = GONE
        }

        if (profile.tempatLahir.isNullOrBlank()) {
            binding.edtBirthPlace.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorBirthPlace.visibility = VISIBLE
        } else {
            binding.edtBirthPlace.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorBirthPlace.visibility = GONE
        }

        if (profile.tanggalLahir.isNullOrBlank()) {
            binding.edtBirthDate.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorBirthDate.visibility = VISIBLE
        } else {
            binding.edtBirthDate.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorBirthDate.visibility = GONE
        }

        if (profile.gaji.isNullOrBlank()) {
            binding.edtIncome.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorIncome.visibility = VISIBLE
        } else {
            binding.edtIncome.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorIncome.visibility = GONE
        }

        if (profile.totalAset.isNullOrBlank()) {
            binding.edtTotalAset.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorTotalAset.visibility = VISIBLE
        } else {
            binding.edtTotalAset.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorTotalAset.visibility = GONE
        }

    }

    private fun showDatePickerDialogBirthDate() {
        val c: Calendar = Calendar.getInstance()
        val mYear: Int = c.get(Calendar.YEAR)
        val mMonth: Int = c.get(Calendar.MONTH)
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = requireActivity().let {
            DatePickerDialog(
                it,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "yyyy-MM-dd" //In which you need put here
                    val sdf = SimpleDateFormat(myFormat)
                    binding.lblBirthDate.text = sdf.format(c.time)
                    parent.delegateProfile().tanggalLahir = binding.lblBirthDate.text.toString()
                }, mYear, mMonth, mDay
            )
        }
        datePickerDialog.show()
    }

}