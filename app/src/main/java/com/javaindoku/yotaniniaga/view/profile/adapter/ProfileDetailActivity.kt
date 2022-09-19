package com.javaindoku.yotaniniaga.view.profile.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityProfileDetailBinding
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.CustomProfile
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.StringFormat
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.EditProfileKoperasiActivity
import com.javaindoku.yotaniniaga.viewmodel.ProfilePetaniDetailViewModel
import com.kotlinpermissions.KotlinPermissions
import dagger.android.AndroidInjection
import javax.inject.Inject

class ProfileDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileDetailBinding

    @Inject
    lateinit var viewModel: ProfilePetaniDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_detail)
        binding.lblNameUser.setText(prefHelper.getStringFromShared(SharedPrefKeys.NAME))
        CustomProfile.showRemoteImageUsingGlide(
            this,
            binding.cimgProfilePicture,
            prefHelper.getStringFromShared(SharedPrefKeys.IMAGE_PROFILE),
            R.drawable.ic_businessman
        )
        binding.btnEditProfile.setOnClickListener {
            KotlinPermissions.with(this) // where this is an FragmentActivity instance
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onAccepted { permissions ->
                    if (permissions.size == 1) {
                        startActivity(
                            Intent(
                                applicationContext,
                                EditProfileKoperasiActivity::class.java
                            )
                        )
                    }
                }.onDenied { permissions ->
                }.onForeverDenied { permissions ->
                }.ask()
        }
        initToolbar()
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    private fun initToolbar() {
        binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.profile)
        binding.lytToolbar.imgSearchToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgBackToolbarHome.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getData() {
        viewModel.profilePetaniDetail(
            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
            prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString()
        )
    }

    private fun observeViewModel() {
        val loadingDialog = loadingDialog(this)
        viewModel.profilePetaniDetailResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                Toast.makeText(applicationContext, getString(it.messageInt!!), Toast.LENGTH_SHORT)
                    .show()
                loadingDialog.dismiss()
            } else {
                loadingDialog.dismiss()
                prefHelper.setStringToShared(SharedPrefKeys.NAME, it.data?.data?.nama.toString())
                prefHelper.setStringToShared(
                    SharedPrefKeys.IMAGE_PROFILE,
                    it.data?.data?.foto.toString()
                )
                bindView(it.data?.data)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun bindView(data: Data?) {
        binding.lblNameUser.setText(data?.namaKoperasi.toString())
        binding.lblCompanyName.setText(data?.namaKoperasi.toString())
        binding.lblCompanyAddress.setText(
            StringFormat.stringCheck(data?.alamat, "") + ", " +
                    StringFormat.stringCheck(data?.kelurahanDesaName, "") + ", " +
                    StringFormat.stringCheck(data?.kecamatanName, "") + ", " +
                    StringFormat.stringCheck(data?.kabupatenKotaName, "") + ", " +
                    StringFormat.stringCheck(data?.provinsiName, "") + ", " +
                    StringFormat.stringCheck(data?.kodePos, "")
        )
        binding.lblBank.setText(data?.bankName.toString())
        binding.lblAccountNo.setText(data?.noRekening.toString())
        binding.lblAtasNama.setText(data?.namaKoperasi.toString())
        CustomProfile.showRemoteImageUsingGlide(
            applicationContext,
            binding.cimgProfilePicture,
            data?.foto.toString(),
            R.drawable.ic_businessman
        )
    }
}