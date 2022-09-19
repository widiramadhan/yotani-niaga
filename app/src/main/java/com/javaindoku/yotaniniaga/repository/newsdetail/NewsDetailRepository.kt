package com.javaindoku.yotaniniaga.repository.newsdetail

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.news.News
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class NewsDetailRepository @Inject constructor(private val api: Api) {
    fun getNewsDetail(id: String) : MutableLiveData<ApiResult<News>> {
        val result = MutableLiveData<ApiResult<News>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.getNewsDetail(id)
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