package com.javaindoku.yotaniniaga.repository.sendstocksupplyfarmer

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.model.sendstocksupplyfarmer.AddReservasi
import com.javaindoku.yotaniniaga.model.sendstocksupplyfarmer.SendStockSupplyFarmerBody
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.authorization
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class SendStockSupplyFarmerRepository @Inject constructor(private val api: Api) {
    fun sendStockSupply(sendStockSupplyFarmerBody: SendStockSupplyFarmerBody, token: String) : MutableLiveData<ApiResult<AddReservasi>> {
        val body = Gson().toJson(sendStockSupplyFarmerBody).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val result = MutableLiveData<ApiResult<AddReservasi>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.sendStockSupplyFarmer(body, token.authorization())
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

    fun confirmStockSupply(sendStockSupplyFarmerBody: SendStockSupplyFarmerBody, token: String) : MutableLiveData<ApiResult<AddReservasi>> {
        val body = Gson().toJson(sendStockSupplyFarmerBody).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val result = MutableLiveData<ApiResult<AddReservasi>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.confirmStockSupplyFarmer(body, token.authorization())
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