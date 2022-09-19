package com.javaindoku.yotaniniaga.repository.ocrktp

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.ocrKtp.OcrKtp
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class OcrKtpRepository @Inject constructor(private val api: Api) {
    fun ocrKtp(userToken: String, body: RequestBody): MutableLiveData<ApiResult<OcrKtp>> {
        val result = MutableLiveData<ApiResult<OcrKtp>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.ocrKtp(body)
                result.postValue(ApiResult.success(response))
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
