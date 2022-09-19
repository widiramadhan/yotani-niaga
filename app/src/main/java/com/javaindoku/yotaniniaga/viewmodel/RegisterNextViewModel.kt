package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.repository.registernext.RegisterNextRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class RegisterNextViewModel @Inject constructor(private val repository: RegisterNextRepository) : BaseViewModel() {
    var registerResult : MutableLiveData<ApiResult<ResponseApi>> = MutableLiveData<ApiResult<ResponseApi>>()

    fun register(name: String, phoneNumber: String, typeUser: String, password: String)  {
        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("mobile", phoneNumber)
        jsonBody.put("type_user", typeUser)
        jsonBody.put("password", password)
        jsonBody.put("confirm_password", password)

        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        repository.register(body).observeForever {
            registerResult.postValue(it)
        }
    }
}