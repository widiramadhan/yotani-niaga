package com.javaindoku.yotaniniaga.view.editprofilepetani

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.iceteck.silicompressorr.SiliCompressor
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityEditProfileBinding
import com.javaindoku.yotaniniaga.model.ocrKtp.OcrKtpData
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.addGarden.listener.DataChangedListener
import com.javaindoku.yotaniniaga.view.editprofilepetani.listener.DataChangedListenerProfile
import com.javaindoku.yotaniniaga.view.editprofilepetani.fragment.FarmerAddressFragment
import com.javaindoku.yotaniniaga.view.editprofilepetani.fragment.FarmerEditFragment
import com.javaindoku.yotaniniaga.view.editprofilepetani.listener.DataChangedListenerAddress
import com.javaindoku.yotaniniaga.view.editprofilepetani.listener.FarmerDataErrorListener
import com.javaindoku.yotaniniaga.view.mainkoperasi.MainKoperasiActivity
import com.javaindoku.yotaniniaga.view.report.adapter.PaymentFragmentAdapter
import com.javaindoku.yotaniniaga.view.splashscreen.SplashScreenActivity
import com.javaindoku.yotaniniaga.viewmodel.EditProfilePetaniViewModel
import dagger.android.AndroidInjection
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class EditProfilePetaniActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var dataChangedListenerProfileProfile: DataChangedListenerProfile
    private lateinit var dataChangedListenerProfileAddress: DataChangedListenerAddress
    private lateinit var dataErrorListener: FarmerDataErrorListener
    private lateinit var addressErrorListener: FarmerDataErrorListener
    private var profile = Data()
    private var ocrKtpData = OcrKtpData()
    var borrowerStatus = ""
    var imageUri = ""
    var imageUriKTP = ""
    var imageUriBpjsTk = ""
    var imageUriBpjsKes = ""
    var imageUriKK = ""

    @Inject
    lateinit var viewModel: EditProfilePetaniViewModel
    lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        loadingDialog = loadingDialog(this)
        initToolbar()
        val adapter = PaymentFragmentAdapter(supportFragmentManager)
        val profileFragment = FarmerEditFragment()
        val addressFragment = FarmerAddressFragment()
        adapter.addFragment(profileFragment, getString(R.string.farmer))
        adapter.addFragment(addressFragment, getString(R.string.address))
        dataChangedListenerProfileProfile = profileFragment
        dataChangedListenerProfileAddress = addressFragment
        dataErrorListener = profileFragment
        addressErrorListener = addressFragment
        binding.viewPagerTransaction.offscreenPageLimit = 1
        binding.viewPagerTransaction.adapter = adapter
        binding.tabEditProfile.setupWithViewPager(binding.viewPagerTransaction)
        getProfile()
        observeViewModel()
    }

    private fun observeViewModel() {

        // POST UPDATE PROFILE
        viewModel.editProfileResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                if(it.data?.isSuccess == true){
                    viewModel.checkBorrower(profile.mobile.toString())
                } else {
                    Toast.makeText(this, it.data?.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        // CHECK BORROWER
        viewModel.checkBorrowerResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                if (it.data?.isSuccess == true){
                    prefHelper.setStringToShared(SharedPrefKeys.BORROWER_ID, it.data?.data?.borrowerId.toString())
                    CustomDialog.dialogOneButton(
                        this,
                        false,
                        getString(R.string.success),
                        getString(R.string.profile_updated),
                        R.drawable.ic_dialog_success,
                        true,
                        getString(R.string.srOk),
                    ) { activity ->
                        if (borrowerStatus == "" || borrowerStatus == "null") {
                            startActivity(Intent(applicationContext, SplashScreenActivity::class.java))
                            finish()
                        } else {
                            finish()
                        }
                    }.show()
                } else {
                    Toast.makeText(this, it.data?.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        // GET PROFILE USER
        viewModel.userProfile.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                profile = it.data?.data ?: Data()
                dataChangedListenerProfileProfile.onDataChanged(profile)
                dataChangedListenerProfileAddress.onDataChanged(profile)
                viewModel.getBank()
                viewModel.getProvinsi()
                viewModel.getReligion()
                viewModel.getEducation()
                viewModel.getProfession()
                viewModel.getFieldWork()
                viewModel.getWorkExperience()
                viewModel.getProvinsiDomisili()
            }
        })

        viewModel.bankResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataBank = it.data!!.data!!.toMutableList()
                if (dataBank.size > 0) {
                    dataChangedListenerProfileProfile.onBankListChanged(dataBank)
                }
            }
        })

        viewModel.provinsiResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataProvinsi = it.data!!.data!!
                dataChangedListenerProfileAddress.onProvinsiListChanged(dataProvinsi)
            }
        })

        viewModel.kabupatenResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataKabupaten = it.data!!.data!!
                dataChangedListenerProfileAddress.onKabupatenListChanged(dataKabupaten)
            }
        })

        viewModel.kecamatanResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataKecamatan = it.data!!.data!!
                dataChangedListenerProfileAddress.onKecamatanListChanged(dataKecamatan)
            }
        })

        viewModel.kelurahanResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataKelurahan = it.data!!.data!!
                dataChangedListenerProfileAddress.onKelurahanListChanged(dataKelurahan)
            }
        })

        viewModel.religonResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataReligion = it.data!!.data!!.toMutableList()
                if (dataReligion.size > 0) {
                    dataChangedListenerProfileProfile.onReligionListChanged(dataReligion)
                }
            }
        })

        viewModel.educationResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataEducation = it.data!!.data!!.toMutableList()
                if (dataEducation.size > 0) {
                    dataChangedListenerProfileProfile.onEducationListChanged(dataEducation)
                }
            }
        })

        viewModel.professionResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataProfession = it.data!!.data!!.toMutableList()
                if (dataProfession.size > 0) {
                    dataChangedListenerProfileProfile.onProfessionListChanged(dataProfession)
                }
            }
        })

        viewModel.fieldWorkResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataFieldwork = it.data!!.data!!.toMutableList()
                if (dataFieldwork.size > 0) {
                    dataChangedListenerProfileProfile.onFieldWorkListChanged(dataFieldwork)
                }
            }
        })

        viewModel.workExperienceResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataWorkExperience = it.data!!.data!!.toMutableList()
                if (dataWorkExperience.size > 0) {
                    dataChangedListenerProfileProfile.onWorkExperienceListChanged(dataWorkExperience)
                }
            }
        })

        // DOMISILI
        viewModel.provinsiDomisiliResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataProvinsi = it.data!!.data!!
                dataChangedListenerProfileAddress.onProvinsiDomisiliListChanged(dataProvinsi)
            }
        })

        viewModel.kabupatenDomisiliResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataKabupaten = it.data!!.data!!
                dataChangedListenerProfileAddress.onKabupatenDomisiliListChanged(dataKabupaten)
            }
        })

        viewModel.kecamatanDomisiliResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataKecamatan = it.data!!.data!!
                dataChangedListenerProfileAddress.onKecamatanDomisiliListChanged(dataKecamatan)
            }
        })

        viewModel.kelurahanDomisiliResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataKelurahan = it.data!!.data!!
                dataChangedListenerProfileAddress.onKelurahanDomisiliListChanged(dataKelurahan)
            }
        })

        // OCR KTP
        viewModel.ocrKtpResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                if (it.data?.success == true){
                    ocrKtpData = it.data?.data ?: OcrKtpData()
                    dataChangedListenerProfileProfile.onOcrKtpChanged(ocrKtpData)
                    Toast.makeText(this, it.data?.message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, it.data?.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    fun setDataChangeNotifier(dataChangedListener: DataChangedListenerProfile) {
        this.dataChangedListenerProfileProfile = dataChangedListener
        dataChangedListener.onDataChanged(profile)
    }

    fun setAddressDataChangeNotifier(dataChangedListener: DataChangedListenerAddress) {
        this.dataChangedListenerProfileAddress = dataChangedListener
        dataChangedListener.onDataChanged(profile)
    }

    fun getKabupaten(provinsiId: Int) {
        viewModel.getkabupaten(provinsiId)
    }

    fun getKecamatan(kabupatenId: Int) {
        viewModel.getKecamatan(kabupatenId)
        //val loadingDialog = loadingDialog(this)
    }

    fun getKelurahan(kecamatanId: Int) {
        viewModel.getKelurahan(kecamatanId)
    }

    fun getKabupatenDomisili(provinsiId: Int) {
        viewModel.getkabupatenDomisili(provinsiId)
    }

    fun getKecamatanDomisili(kabupatenId: Int) {
        viewModel.getKecamatanDomisili(kabupatenId)
        //val loadingDialog = loadingDialog(this)
    }

    fun getKelurahanDomisili(kecamatanId: Int) {
        viewModel.getKelurahanDomisili(kecamatanId)
    }

    private fun initToolbar() {
        binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.edit_profile)
        binding.lytToolbar.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
        val borrowedId = prefHelper.getStringFromShared(SharedPrefKeys.BORROWER_ID).toString()
        borrowerStatus = prefHelper.getStringFromShared(SharedPrefKeys.BORROWER_ID).toString()
        if (borrowedId == "" || borrowedId == "null") {
            CustomDialog.dialogOneButton(
                this,
                false,
                getString(R.string.lable_information_Help),
                getString(R.string.complete_profile),
                R.drawable.ic_dialog_success,
                true,
                getString(R.string.srOk),
            ) { activity -> }.show()}
    }

    override fun onClick(view: View?) {
        val currentFragment = binding.viewPagerTransaction.currentItem
        if (view?.id == R.id.btnConfirmationPositive) {
            binding.viewPagerTransaction.currentItem = currentFragment + 1
        } else {
            binding.viewPagerTransaction.currentItem = currentFragment - 1
        }
    }

    fun delegateProfile() : Data {
        return profile
    }

    private fun getProfile() {
        viewModel.fetchProfilePetaniDetail(
            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
            prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString()
        )
    }

    fun sendOcrKtp() {
        viewModel.fetchOcrKtp(
            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
            convertUriToBitmap(imageUriKTP)
        )
    }

    fun submitChanges() {
        if (!formDataIsValid()) {
            dataErrorListener.onError(profile)
            val currentFragment = binding.viewPagerTransaction.currentItem
            binding.viewPagerTransaction.currentItem = currentFragment - 1
            return
        }

        if (!formAddressIsValid()) {
            addressErrorListener.onError(profile)
            return
        }

        viewModel.updateProfile(
            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
            profile,
            convertUriToBitmap(imageUri),
            convertUriToBitmap(imageUriKTP),
            convertUriToBitmap(imageUriBpjsTk),
            convertUriToBitmap(imageUriBpjsKes),
            convertUriToBitmap(imageUriKK),
        )
    }

    private fun formAddressIsValid(): Boolean {
        return !profile.alamat.isNullOrBlank() && !profile.rt.isNullOrBlank() && !profile.rw.isNullOrBlank()
                && !profile.kodePos.isNullOrBlank() && !profile.ktpAlamat.isNullOrBlank() && !profile.ktpRT.isNullOrBlank()
                && !profile.ktpRW.isNullOrBlank() && !profile.ktpKodePos.isNullOrBlank()
    }

    private fun formDataIsValid(): Boolean {
        return !profile.nama.isNullOrBlank() && !profile.mobile.isNullOrBlank() && !profile.akunBank.isNullOrBlank()
                && !profile.noRekening.isNullOrBlank()  && !profile.email.isNullOrBlank()
                && !profile.noKtp.isNullOrBlank() && (profile.noKtp.toString().trim().length == 15 || profile.noKtp.toString().trim().length == 16)
                && !profile.npwp.isNullOrBlank() && profile.npwp.toString().trim().length == 15
                && !profile.tempatLahir.isNullOrBlank() && !profile.tanggalLahir.isNullOrBlank()
                && !profile.gaji.isNullOrBlank() && !profile.totalAset.isNullOrBlank()
    }

    private fun convertUriToBitmap(uri: String): String {
        if (uri.isEmpty()) return ""
        val imageBitmap: Bitmap = SiliCompressor.with(this).getCompressBitmap(uri)
        return encodeImage(imageBitmap)
    }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.NO_WRAP)
    }
}