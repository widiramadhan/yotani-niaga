package com.javaindoku.yotaniniaga.repository.register

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.otp.SendOtp
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class RegisterRepository @Inject constructor(private val api: Api) {
    fun checkRegisterPhoneNumber(phoneNo: String): MutableLiveData<ApiResult<ResponseApi>> {
        val result = MutableLiveData<ApiResult<ResponseApi>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.getRegisteredData(phoneNo)
                if (response.isSuccess) {
                    result.postValue(ApiResult.success(response))
                } else {
                    if (response.message.contains("Nomor telepon sudah terdaftar")) {
                        result.postValue(ApiResult.errorInt(R.string.phonNumberExist))
                    } else {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }

                }
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        if (e.response() != null) {
                            val errorJson = e.response()!!.errorBody()!!.string()
                            val responseApi = Gson().fromJson(errorJson, ResponseApi::class.java)
                        }
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }

    fun getOtpPin(phoneNumber: String): MutableLiveData<ApiResult<SendOtp>> {
        val result = MutableLiveData<ApiResult<SendOtp>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.getOtpPin(phoneNumber)
                if (response.isSuccess!!) {
                    result.postValue(ApiResult.success(response))
                } else {
                    if (response.message!!.contains("Nomor telepon sudah terdaftar")) {
                        result.postValue(ApiResult.errorInt(R.string.phonNumberExist))
                    } else {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }

                }
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        if (e.response() != null) {
                            val errorJson = e.response()!!.errorBody()!!.string()
                            val responseApi = Gson().fromJson(errorJson, ResponseApi::class.java)
                        }
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }

    fun verifyOtpPin(phonenumber: String, request_id: String, code: String): MutableLiveData<ApiResult<ResponseApi>> {
        val result = MutableLiveData<ApiResult<ResponseApi>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.verifyPin(phonenumber, request_id, code)
                if (response.isSuccess) {
                    result.postValue(ApiResult.success(response))
                } else {
                    if (response.message.contains("Nomor telepon sudah terdaftar")) {
                        result.postValue(ApiResult.errorInt(R.string.phonNumberExist))
                    } else {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }

                }
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        if (e.response() != null) {
                            val errorJson = e.response()!!.errorBody()!!.string()
                            //val responseApi = Gson().fromJson(errorJson, ResponseApi::class.java)
                        }
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }
}