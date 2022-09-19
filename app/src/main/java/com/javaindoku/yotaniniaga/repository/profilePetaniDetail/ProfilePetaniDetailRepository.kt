package com.javaindoku.yotaniniaga.repository.profilePetaniDetail

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.ProfilePetaniDetail
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

class ProfilePetaniDetailRepository @Inject constructor(private val api: Api) {
    fun profilePetaniDetail(userToken: String, userId: String) : MutableLiveData<ApiResult<ProfilePetaniDetail>> {
        val result = MutableLiveData<ApiResult<ProfilePetaniDetail>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.profilePetaniDetail(userToken.authorization(), userId)
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