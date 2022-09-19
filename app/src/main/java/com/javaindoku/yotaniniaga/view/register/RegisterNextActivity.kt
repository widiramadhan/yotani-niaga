package com.javaindoku.yotaniniaga.view.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityRegisterNextBinding
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.utilities.isAlphanumericV2
import com.javaindoku.yotaniniaga.view.login.LoginActivity
import com.javaindoku.yotaniniaga.viewmodel.RegisterNextViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject


class RegisterNextActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterNextBinding

    companion object {
        const val PHONE_NUMBER = "phoneNumber"
    }
    private var farmerOrSupplier = ConstValue.FARMER

    @Inject
    lateinit var viewModel: RegisterNextViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_next)
        binding.appbarLayout.lblTitleToolbarHome.text = getString(R.string.register)
        binding.appbarLayout.imgSearchToolbarHome.visibility = View.GONE
        binding.appbarLayout.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
        val phoneNumber = intent.getStringExtra(PHONE_NUMBER)

        observeViewModel()
        binding.lytFarmer.setOnClickListener {
            binding.lytFarmer.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgFarmer.setImageResource(R.drawable.ic_radio_active)
            binding.lblFarmer.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

            binding.lytSupplier.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgSupplier.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblSupplier.setTextColor(ContextCompat.getColor(this, R.color.ligh_gray2))

            farmerOrSupplier = ConstValue.FARMER
        }

        binding.lytSupplier.setOnClickListener {
            binding.lytFarmer.setBackgroundResource(R.drawable.bg_check_worker_agent_pasif)
            binding.imgFarmer.setImageResource(R.drawable.ic_radio_pasive2)
            binding.lblFarmer.setTextColor(ContextCompat.getColor(this, R.color.ligh_gray2))

            binding.lytSupplier.setBackgroundResource(R.drawable.bg_check_worker_agent_active)
            binding.imgSupplier.setImageResource(R.drawable.ic_radio_active)
            binding.lblSupplier.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

            farmerOrSupplier = ConstValue.SUPPLIER
        }

        binding.btnRegisterWorker.setOnSafeClickListener {
            if(!binding.txtPasswordRegisterWorker.text.toString().isAlphanumericV2()){
                CustomDialog.dialogOneButton(
                    this,
                    true,
                    getString(R.string.password_format),
                    getString(R.string.password_must_contain_minimum_8_character_alphanumeric),
                    R.drawable.ic_dialog_failed,
                    false,
                    getString(R.string.srOk),
                    {}
                ).show()
                binding.lblErrorPasswordRegister.visibility = View.VISIBLE
            }
            else if (!binding.txtPasswordRegisterWorker.text.toString().equals(binding.txtPasswordRegisterWorker.text.toString())) {
                binding.lblErrorConfirmPasswordRegister.visibility = View.GONE
                CustomDialog.dialogOneButton(
                    this,
                    true,
                    getString(R.string.password_format),
                    getString(R.string.srErrorMessageConfirmPasswordNotSame),
                    R.drawable.ic_dialog_failed,
                    false,
                    getString(R.string.srOk),
                    {}
                ).show()
                binding.lblErrorConfirmPasswordRegister.visibility = View.VISIBLE
            }
            else {
                viewModel.register(
                    binding.txtFullNameRegisterWorker.text.toString(),
                    phoneNumber,
                    farmerOrSupplier,
                    binding.txtConfirmPasswordRegisterWorker.text.toString()
                )
            }
        }
    }

    private fun observeViewModel() {
        val loadingDialog = loadingDialog(this)
        viewModel.registerResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(applicationContext, getString(it.messageInt!!), Toast.LENGTH_SHORT)
                    .show()
            } else {
                loadingDialog.dismiss()
                CustomDialog.dialogOneButton(
                    this,
                    false,
                    getString(R.string.register_success),
                    getString(R.string.now_you_can_login),
                    R.drawable.ic_dialog_success,
                    true,
                    getString(R.string.action_sign_in),
                    {
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                ).show()
            }
        })
    }
}