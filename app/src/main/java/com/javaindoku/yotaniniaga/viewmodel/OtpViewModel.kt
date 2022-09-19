package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.repository.register.RegisterRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import javax.inject.Inject

class OtpViewModel @Inject constructor(val repository: RegisterRepository) : BaseViewModel() {
    val verifyOtpResult = MutableLiveData<ApiResult<ResponseApi>>()

    fun verifyOtp(phonenumber: String, request_id: String, code: String) {
        repository.verifyOtpPin(phonenumber, request_id, code).observeForever{
            verifyOtpResult.postValue(it)
        }
    }
}
