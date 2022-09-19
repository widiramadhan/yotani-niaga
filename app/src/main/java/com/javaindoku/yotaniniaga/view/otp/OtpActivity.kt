package com.javaindoku.yotaniniaga.view.otp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.airbnb.paris.extensions.style
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityOtpBinding
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.changepassword.ChangePasswordActivity
import com.javaindoku.yotaniniaga.view.register.RegisterActivity
import com.javaindoku.yotaniniaga.view.register.RegisterNextActivity
import com.javaindoku.yotaniniaga.viewmodel.OtpViewModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.spinner_with_search_layout.*
import javax.inject.Inject

class OtpActivity : BaseActivity() {
    private lateinit var binding: ActivityOtpBinding
    private var phoneNumber = ""
    private var previousActivity = ""
    private var requestId = ""

    //    private val countDown : Long = 300 * 1000 - 1 /*10 * 1000*/
    private lateinit var countDownTimer: CountDownTimer

    @Inject
    lateinit var viewModel: OtpViewModel

    companion object {
        const val PHONE_NUMBER = "phoneNumber"
        const val PREVIOUS_ACTIVITY = "previousActivity"
        const val REQUEST_ID = "requestId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        previousActivity = intent.getStringExtra(PREVIOUS_ACTIVITY)
        requestId = intent.getStringExtra(REQUEST_ID)
        binding.lblPhoneNo.text = phoneNumber
        initToolbar()
        countDownTimer()
        binding.btnGetNewToken.setOnSafeClickListener {
            countDownTimer.cancel()
            countDownTimer.start()
        }
        binding.btnVerification.setOnSafeClickListener {
            viewModel.verifyOtp(phoneNumber, requestId, binding.otpView.text.toString())
            observeViewModel()
        }
    }

    private fun observeViewModel() {
        val loader = loadingDialog(this)
        viewModel.verifyOtpResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loader.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loader.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loader.dismiss()
                if (previousActivity == RegisterActivity::class.simpleName) {
                    val intent = Intent(applicationContext, RegisterNextActivity::class.java)
                    intent.putExtra(RegisterNextActivity.PHONE_NUMBER, phoneNumber)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(applicationContext, ChangePasswordActivity::class.java)
                    intent.putExtra(RegisterNextActivity.PHONE_NUMBER, phoneNumber)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }

    private fun initToolbar() {
        binding.appbarLayout.lblTitleToolbarHome.text = getString(R.string.srInputVerificationCode)
        binding.appbarLayout.imgBackToolbarHome.visibility = View.VISIBLE
        binding.appbarLayout.imgSearchToolbarHome.visibility = View.GONE
        binding.appbarLayout.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
    }

    private fun countDownTimer() {
        var countDown: Long = 300 * 1000 - 1 /*10 * 1000*/
        val rotate = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = countDown
        rotate.interpolator = LinearInterpolator()
        binding.lytTimer.startAnimation(rotate)

        countDownTimer = object : CountDownTimer(countDown, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000)
                val minutes = seconds / 60
                val secondShows = seconds % 60
                binding.lblCountDown.text =
                    (String.format("%02d", minutes) + ":" + String.format("%02d", secondShows))
//                binding.btnGetNewToken.isClickable = false
                binding.btnVerification.style(R.style.ButtonYoTani)
                binding.btnVerification.isEnabled = true
                binding.btnVerification.isClickable = true
            }

            override fun onFinish() {
                binding.lblCountDown.text = "00:00"
                binding.btnGetNewToken.text = resources.getText(R.string.get_new_token)
                binding.btnGetNewToken.isClickable = true
                binding.btnVerification.style(R.style.ButtonYoTaniDisable)
                binding.btnVerification.isEnabled = false
                binding.btnVerification.isClickable = false

            }
        }
        countDownTimer.start()
    }
}