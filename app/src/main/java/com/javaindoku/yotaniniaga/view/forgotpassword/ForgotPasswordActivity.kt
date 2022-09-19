package com.javaindoku.yotaniniaga.view.forgotpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityForgotPasswordBinding
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.otp.OtpActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_register.*

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        binding.btnContinue.setOnSafeClickListener {
            val phoneNo = binding.edtRegisteredPhoneNo.text.toString()
            if(phoneNo.isNullOrBlank() || phoneNo.length<9){
                binding.edtRegisteredPhoneNo.background = resources.getDrawable(
                    R.drawable.bg_shape_edittext_error,
                    null
                )
                binding.lblErrorEmptyPhoneNo.visibility = View.VISIBLE
            }
            else {
                binding.edtRegisteredPhoneNo.background = resources.getDrawable(
                    R.drawable.bg_shape_edittext,
                    null
                )
                binding.lblErrorEmptyPhoneNo.visibility = View.GONE
                CustomDialog.dialogOneButton(
                    activity = this@ForgotPasswordActivity,
                    cancelable = true,
                    title = getString(R.string.srTitleSuccess),
                    message = getString(R.string.phone_number_correct_continue_change_password),
                    imageId = R.drawable.ic_dialog_success,
                    isPositive = true,
                    labelPositiveButton = getString(R.string.continue_text),
                    positiveAction = {
                        val intent = Intent(this, OtpActivity::class.java)
                        intent.putExtra(OtpActivity.PHONE_NUMBER, binding.edtRegisteredPhoneNo.text.toString())
                        intent.putExtra(OtpActivity.PREVIOUS_ACTIVITY, this::class.simpleName)
                        startActivity(intent)
                    }

                ).show()
            }
        }
        initToolbar()
    }

    private fun initToolbar() {
        binding.appbarLayout.lblTitleToolbarHome.text = getString(R.string.forgot_password)
        binding.appbarLayout.imgBackToolbarHome.visibility = View.VISIBLE
        binding.appbarLayout.imgSearchToolbarHome.visibility = View.GONE
        binding.appbarLayout.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}