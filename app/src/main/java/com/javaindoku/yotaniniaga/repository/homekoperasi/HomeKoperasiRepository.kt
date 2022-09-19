package com.javaindoku.yotaniniaga.repository.homekoperasi

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenBody
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenDataSchema
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenKoperasiBody
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenPetani
import com.javaindoku.yotaniniaga.model.news.News
import com.javaindoku.yotaniniaga.model.responseapi.GeneralResponseApi
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.authorization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class HomeKoperasiRepository @Inject constructor(private val api: Api) {
    fun getNews() : MutableLiveData<ApiResult<News>> {
        val result = MutableLiveData<ApiResult<News>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.getNews("1")
                if(response.isSuccess)
                {
                    result.postValue(ApiResult.success(response))
                }
                else
                {
                    result.postValue(ApiResult.errorInt(R.string.srServerError))
                }
            }
            catch (e: Exception)
            {
                when(e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    is ConnectException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }

    fun getGarden(gardenBody: GardenBody, token: String) : MutableLiveData<ApiResult<GardenPetani>> {
        val result = MutableLiveData<ApiResult<GardenPetani>>()
        result.postValue(ApiResult.loading())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val body = Gson().toJson(gardenBody).toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val response = api.getGardenPetani(body, token.authorization())
                if (response.isSuccess) {
                    result.postValue(ApiResult.success(response))
                } else {
                    result.postValue(ApiResult.errorInt(R.string.srServerError))
                }
            } catch (e: Exception) {
                when(e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    is ConnectException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }

    fun getGardenKoperasi(gardenBody: GardenKoperasiBody, token: String) : MutableLiveData<ApiResult<GardenPetani>> {
        val result = MutableLiveData<ApiResult<GardenPetani>>()
        result.postValue(ApiResult.loading())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val body = Gson().toJson(gardenBody).toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val response = api.getKoperasiFarms(body, token.authorization())
                if (response.isSuccess) {
                    result.postValue(ApiResult.success(response))
                } else {
                    result.postValue(ApiResult.errorInt(R.string.srServerError))
                }
            } catch (e: Exception) {
                when(e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    is ConnectException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
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