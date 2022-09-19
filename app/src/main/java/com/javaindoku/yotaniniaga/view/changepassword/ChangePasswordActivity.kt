package com.javaindoku.yotaniniaga.view.changepassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import com.airbnb.paris.extensions.style
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityChangePasswordBinding
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.utilities.isAlphanumericV2

class ChangePasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        initToolbar()
        var isAlphanumeric = false
        var isLengthValid = false
        binding.edtNewPassword.doOnTextChanged { text, start, count, after ->
            if(text.toString().isAlphanumericV2()) {
                binding.lblAtLeastOneLetter.style(R.style.TextViewValid)
                isAlphanumeric = true
            }
            else
            {
                binding.lblAtLeastOneLetter.style(R.style.TextViewNotValid)
                isAlphanumeric = false
            }

            if(text.toString().length<8) {
                binding.lblMinimumPassword.style(R.style.TextViewNotValid)
                isLengthValid = false
            }
            else
            {
                binding.lblMinimumPassword.style(R.style.TextViewValid)
                isLengthValid = true
            }

            if(isAlphanumeric && isLengthValid) {
                if(text.toString().equals(binding.edtConfirmNewPassword.text.toString())) {
                    binding.btnChangePassword.style(R.style.ButtonYoTani)
                }
                else
                {
                    binding.btnChangePassword.style(R.style.ButtonYoTaniDisable)
                }
            }
        }

        binding.edtConfirmNewPassword.doOnTextChanged { text, start, count, after ->
            if(isAlphanumeric && isLengthValid) {
                if(text.toString().equals(binding.edtNewPassword.text.toString())){
                    binding.btnChangePassword.style(R.style.ButtonYoTani)
                }
                else
                {
                    binding.btnChangePassword.style(R.style.ButtonYoTaniDisable)
                }
            }
        }
    }

    private fun validationNewPassword() : Boolean {
        val newPassword = binding.edtNewPassword.text.toString()
        return if(newPassword.length<8)
            false
        else newPassword.isAlphanumericV2()
    }

    private fun confirmNewPassword() : Boolean {
        if(binding.edtNewPassword.text.toString().equals(binding.edtConfirmNewPassword.text.toString()))
            return true
        return false
    }

    private fun initToolbar() {
        binding.appbarLayout.lblTitleToolbarHome.text = getString(R.string.srChangePassword)
        binding.appbarLayout.imgBackToolbarHome.visibility = View.VISIBLE
        binding.appbarLayout.imgSearchToolbarHome.visibility = View.GONE
        binding.appbarLayout.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
    }
}