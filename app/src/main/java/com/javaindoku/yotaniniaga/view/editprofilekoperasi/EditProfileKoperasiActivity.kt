package com.javaindoku.yotaniniaga.view.editprofilekoperasi

import android.app.Dialog
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
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.fragment.KoperasiAddressFragment
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.fragment.KoperasiEditFragment
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.listener.DataChangedListenerAddress
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.listener.DataChangedListenerProfile
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.listener.KoperasiDataErrorListener
import com.javaindoku.yotaniniaga.view.editprofilepetani.listener.FarmerDataErrorListener
import com.javaindoku.yotaniniaga.view.report.adapter.PaymentFragmentAdapter
import com.javaindoku.yotaniniaga.viewmodel.EditProfileKoperasiViewModel
import dagger.android.AndroidInjection
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class EditProfileKoperasiActivity : BaseActivity(), View.OnClickListener {
    var imageUri = ""
    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var dataChangedListenerProfileProfile: DataChangedListenerProfile
    private lateinit var dataChangedListenerProfileAddress: DataChangedListenerAddress
    private lateinit var dataErrorListener: KoperasiDataErrorListener
    private lateinit var addressErrorListener: KoperasiDataErrorListener
    private var profile = Data()

    @Inject
    lateinit var viewModel: EditProfileKoperasiViewModel
    lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        loadingDialog = loadingDialog(this)
        initToolbar()
        val adapter = PaymentFragmentAdapter(supportFragmentManager)
        val profileFragment = KoperasiEditFragment()
        val addressFragment = KoperasiAddressFragment()
        adapter.addFragment(profileFragment, getString(R.string.biodata))
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
        viewModel.editProfileResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                CustomDialog.dialogOneButton(
                    this,
                    true,
                    getString(R.string.success),
                    getString(R.string.profile_updated),
                    R.drawable.ic_dialog_success,
                    true,
                    getString(R.string.srOk),
                ) { activity -> finish()}.show()
            }
        })

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
                viewModel.getBadanUsaha()
                viewModel.getProvinsi()
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

        viewModel.badanUsahaResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val dataBadanUsaha = it.data!!.data!!.toMutableList()
                if (dataBadanUsaha.size > 0) {
                    dataChangedListenerProfileProfile.onBadanUsahaListChanged(dataBadanUsaha)
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


    private fun initToolbar() {
        binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.edit_profile)
        binding.lytToolbar.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
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

    fun submitChanges() {
        if (!formDataIsValid()) {
            dataErrorListener.onError(profile)
            val currentFragment = binding.viewPagerTransaction.currentItem
            binding.viewPagerTransaction.currentItem = currentFragment - 1
        }

        if (!formAddressIsValid()) {
            addressErrorListener.onError(profile)
            return
        }

        viewModel.updateProfile(
            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
            profile,
            convertUriToBitmap(imageUri),
        )
    }

    private fun formAddressIsValid(): Boolean {
        return !profile.alamat.isNullOrBlank() && !profile.rt.isNullOrBlank() && !profile.rw.isNullOrBlank()
                && !profile.kodePos.isNullOrBlank()
    }

    private fun formDataIsValid(): Boolean {
        return !profile.nama.isNullOrBlank() && !profile.mobile.isNullOrBlank() && !profile.akunBank.isNullOrBlank()
                && !profile.noRekening.isNullOrBlank()
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