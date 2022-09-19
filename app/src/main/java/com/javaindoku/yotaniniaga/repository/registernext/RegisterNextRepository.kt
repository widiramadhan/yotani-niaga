package com.javaindoku.yotaniniaga.repository.registernext

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class RegisterNextRepository @Inject constructor(private val api: Api) {
    fun register(body: RequestBody) : MutableLiveData<ApiResult<ResponseApi>> {
        val result = MutableLiveData<ApiResult<ResponseApi>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.register(body)
                if(response.isSuccess)
                {
                    result.postValue(ApiResult.success(response))
                }
                else
                {
                    if(response.message.contains("already registered"))
                    {
                        result.postValue(ApiResult.errorInt(R.string.registered_phone))
                    }
                    else
                    {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }

                }
            }
            catch (e: Exception)
            {
                when(e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        if(e.response()!=null){
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
}