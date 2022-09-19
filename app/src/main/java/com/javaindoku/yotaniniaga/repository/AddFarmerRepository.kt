package com.javaindoku.yotaniniaga.repository

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.model.responseapi.GeneralResponseApi
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.authorization
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class AddFarmerRepository @Inject constructor(val api: Api) {

    fun addPetani(token: String, body: RequestBody): MutableLiveData<ApiResult<GeneralResponseApi<List<Any>>>> {
        val result = MutableLiveData<ApiResult<GeneralResponseApi<List<Any>>>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.addFarmer(body, token.authorization())
                result.postValue(ApiResult.success(response))
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

    fun editPetani(token: String, body: RequestBody): MutableLiveData<ApiResult<GeneralResponseApi<List<Any>>>> {
        val result = MutableLiveData<ApiResult<GeneralResponseApi<List<Any>>>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.editFarmer(body, token.authorization())
                result.postValue(ApiResult.success(response))
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

    fun getFarmerDetail(token: String, body: RequestBody): MutableLiveData<ApiResult<GeneralResponseApi<Data>>> {
        val result = MutableLiveData<ApiResult<GeneralResponseApi<Data>>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.getFarmerDetail(body, token.authorization())
                result.postValue(ApiResult.success(response))
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
