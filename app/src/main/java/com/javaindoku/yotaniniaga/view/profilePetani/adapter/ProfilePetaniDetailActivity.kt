package com.javaindoku.yotaniniaga.view.profilePetani.adapter

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
import com.javaindoku.yotaniniaga.databinding.ActivityProfilePetaniDetailBinding
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.ProfilePetaniDetail
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.CustomProfile
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.StringFormat
import com.javaindoku.yotaniniaga.utilities.StringFormat.stringCheck
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.editprofilepetani.EditProfilePetaniActivity
import com.javaindoku.yotaniniaga.viewmodel.ProfilePetaniDetailViewModel
import com.kotlinpermissions.KotlinPermissions
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.toolbar_home.view.*
import javax.inject.Inject

class ProfilePetaniDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityProfilePetaniDetailBinding

    @Inject
    lateinit var viewModel: ProfilePetaniDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_petani_detail)
        binding.btnEditProfile.setOnSafeClickListener {
            KotlinPermissions.with(this) // where this is an FragmentActivity instance
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onAccepted { permissions ->
                    if (permissions.size == 1) {
                        startActivity(Intent(applicationContext, EditProfilePetaniActivity::class.java))
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
        binding.lytToolbar.imgBackToolbarHome.setOnSafeClickListener {
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
        binding.lblNameUser.setText(data?.nama.toString())
        binding.lblPhoneNo.setText(data?.mobile.toString())
        binding.lblEmail.setText(stringCheck(data?.email, ""))
        binding.lblAddress.setText(stringCheck(data?.alamat, "") + ", " +
                stringCheck(data?.kelurahanDesaName, "") + ", "+
                stringCheck(data?.kecamatanName, "") + ", " +
                stringCheck(data?.kabupatenKotaName, "") + ", " +
                stringCheck(data?.provinsiName, "") + ", " +
                stringCheck(data?.kodePos, ""))
        binding.lblKtp.setText(stringCheck(data?.noKtp, ""))
        binding.lblKk.setText(stringCheck(data?.noKk, ""))
        binding.lblNpwp.setText(stringCheck(data?.npwp, ""))
        binding.lblBank.setText(stringCheck(data?.bankName, ""))
        binding.lblNameOfBank.setText(stringCheck(data?.akunBank, ""))
        binding.lblNoOfBank.setText(stringCheck(data?.noRekening, ""))
        CustomProfile.showRemoteImageUsingGlide(applicationContext, binding.cimgProfilePicture, data?.foto.toString(), R.drawable.ic_businessman)
    }
}