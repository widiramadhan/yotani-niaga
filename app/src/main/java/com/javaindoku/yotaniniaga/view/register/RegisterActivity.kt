package com.javaindoku.yotaniniaga.view.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityRegisterBinding
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.otp.OtpActivity
import com.javaindoku.yotaniniaga.viewmodel.RegisterViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding

    @Inject
    lateinit var viewModel: RegisterViewModel
    private var phoneNumber = ""

    companion object {
        const val PHONE_NUMBER = "phoneNumber"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        phoneNumber = intent.getStringExtra(PHONE_NUMBER) ?: ""
        binding.txtNoTlpRegister.setText(phoneNumber)
        observeViewModel()

        binding.btnRegister.setOnSafeClickListener {
            if (binding.txtNoTlpRegister.text.toString()
                    .isNullOrBlank() || binding.txtNoTlpRegister.text.toString().length < 9
            ) {
                binding.txtNoTlpRegister.background = resources.getDrawable(
                    R.drawable.bg_shape_edittext_error,
                    null
                )
                binding.lblErrorRegisteredPhoneNoLogin.visibility = View.VISIBLE
            } else if (!binding.cbRegister.isChecked) {
                CustomDialog.dialogOneButton(
                    this,
                    true,
                    getString(R.string.check_again),
                    getString(R.string.sorry_you_have_to_check_term_and_condition),
                    R.drawable.ic_dialog_failed,
                    false,
                    getString(R.string.srOk),
                    {}
                ).show()
                binding.txtNoTlpRegister.background = resources.getDrawable(
                    R.drawable.bg_shape_edittext,
                    null
                )
                binding.lblErrorRegisteredPhoneNoLogin.visibility = View.GONE
            } else {
                phoneNumber = binding.txtNoTlpRegister.text.toString()
                val dialog = CustomDialog.customDialogTwoButton(this,
                    cancelable = true,
                    title = phoneNumber,
                    message = getString(R.string.srConfirmationRegister),
                    labelPositiveButton = getString(R.string.srTxtBtnPositive),
                    labelNegativeButton = getString(R.string.srTxtBtnNegative),
                    positiveAction = {
                        checkRegisterPhoneNumber()
                    },
                    negativeAction = {

                    })
                dialog.show()
            }
        }
    }

    private fun observeViewModel() {
        val loadingDialog = loadingDialog(this)
        viewModel.checkRegisterNumber.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                CustomDialog.dialogOneButton(this,
                    true,
                    getString(R.string.check_again),
                    getString(
                        it.messageInt!!
                    ),
                    R.drawable.ic_dialog_failed,
                    false,
                    getString(R.string.srOk),
                    {}).show()
            } else {
                loadingDialog.dismiss()
                viewModel.getOtpPin(phoneNumber)
            }
        })
        viewModel.otpSentResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                CustomDialog.dialogOneButton(this,
                    true,
                    getString(R.string.check_again),
                    it.message!!.toString(),
                    R.drawable.ic_dialog_failed,
                    false,
                    getString(R.string.srOk),
                    {}).show()
            } else {
                loadingDialog.dismiss()
                val requsetid = it.data!!.data!!.request_id.toString()
                toOtpActivity(requsetid)
            }
        })
    }

    private fun checkRegisterPhoneNumber() {
        viewModel.checkRegisterPhoneNumber(phoneNumber)
    }

    private fun toOtpActivity(requestId: String) {
        val intent = Intent(this, OtpActivity::class.java)
        intent.putExtra(OtpActivity.PHONE_NUMBER, binding.txtNoTlpRegister.text.toString())
        intent.putExtra(OtpActivity.PREVIOUS_ACTIVITY, this::class.simpleName)
        intent.putExtra(OtpActivity.REQUEST_ID, requestId)
        startActivity(intent)
    }
}