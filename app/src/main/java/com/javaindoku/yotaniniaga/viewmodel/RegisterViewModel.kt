package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.otp.SendOtp
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.repository.register.RegisterRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import javax.inject.Inject

class RegisterViewModel @Inject constructor(private val repository:RegisterRepository) : BaseViewModel() {
    var otpSentResult = MutableLiveData<ApiResult<SendOtp>>()
    var checkRegisterNumber : MutableLiveData<ApiResult<ResponseApi>> = MutableLiveData<ApiResult<ResponseApi>>()

    fun checkRegisterPhoneNumber(phoneNumber: String)  {
        repository.checkRegisterPhoneNumber(phoneNumber).observeForever {
            checkRegisterNumber.postValue(it)
        }
    }

    fun getOtpPin(phoneNumber: String) {
        repository.getOtpPin(phoneNumber).observeForever{
            otpSentResult.postValue(it)
        }
    }

}