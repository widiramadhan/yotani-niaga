package com.javaindoku.yotaniniaga.repository.login

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.login.Login
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class LoginRepository @Inject constructor(private val api: Api) {
    fun login(phoneNumber: String, password: String, fcmToken: String) : MutableLiveData<ApiResult<Login>> {
        val loginResult = MutableLiveData<ApiResult<Login>>()
        loginResult.postValue(ApiResult.loading())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.loginWorker(phoneNumber, password, fcmToken)
                if(response.isSuccess)
                    loginResult.postValue(ApiResult.success(response))
                else{
                    if(response.message.equals("Password don`t match", true))
                    {
                        loginResult.postValue(ApiResult.errorInt(R.string.sorry_invalid_password))
                    }
                    else if(response.message.equals("User not found or Invalid Email", true))
                    {
                        loginResult.postValue(ApiResult.errorInt(R.string.srUserNotFound))
                    }
                    else{
                        loginResult.postValue(ApiResult.error(response.message))
                    }
                }
            }
            catch (e: Exception)
            {
                when(e) {
                    is UnknownHostException -> {
                        loginResult.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        loginResult.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    is ConnectException -> {
                        loginResult.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    else -> {
                        loginResult.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return loginResult
    }
}