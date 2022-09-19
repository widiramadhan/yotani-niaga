package com.javaindoku.yotaniniaga.repository.editprofilekoperasi

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.profile.bank.Bank
import com.javaindoku.yotaniniaga.model.profile.kabupaten.Kabupaten
import com.javaindoku.yotaniniaga.model.profile.kecamatan.Kecamatan
import com.javaindoku.yotaniniaga.model.profile.kelurahan.Kelurahan
import com.javaindoku.yotaniniaga.model.profile.provinsi.Provinsi
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

class EditProfileKoperasiRepository @Inject constructor(private val api: Api) {

    fun editProfileKoperasi(userToken: String, body: RequestBody): MutableLiveData<ApiResult<ResponseApi>> {
        val result = MutableLiveData<ApiResult<ResponseApi>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.editProfileKoperasi(body, userToken.authorization())
                result.postValue(ApiResult.success(response))
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

}