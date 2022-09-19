package com.javaindoku.yotaniniaga.repository.profilekoperasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.profile.Farmer
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.model.responseapi.GeneralResponseApi
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.utilities.authorization
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class ProfileKoperasiRepository @Inject constructor(private val api: Api) {
    fun getFarmer(jsonBody: JSONObject, token: String): Listing<Data> {
        val sourceFactory = ProfileKoperasiDataSourceFactory(api, jsonBody, token)
        val config: PagedList.Config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(30).build()
        val liveData: LiveData<PagedList<Data>> = LivePagedListBuilder(sourceFactory, config).build()
        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.networkState
        }

        return Listing(
            pagedList = liveData,
            networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                it.networkState
            },
            refresh = {
                sourceFactory.sourceLiveData.value?.invalidate()
            },
            retry = {
                sourceFactory.sourceLiveData.value?.retryAllFiled()
            },
            refreshState = refreshState
        )
    }

    data class Listing<T>(
        val pagedList: LiveData<PagedList<T>>,
        val networkState: LiveData<NetworkState>,
        val refreshState: LiveData<NetworkState>,
        val refresh: () -> Unit,
        val retry: () -> Unit
    )

    fun checkIsFarmerBound(requestBody: RequestBody, token: String) : MutableLiveData<ApiResult<GeneralResponseApi<Data>>> {
        val result = MutableLiveData<ApiResult<GeneralResponseApi<Data>>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.checkIsFarmerBound(requestBody, token.authorization())
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

    fun removeFarmer(
        body: RequestBody,
        token: String
    ): MutableLiveData<ApiResult<GeneralResponseApi<List<Any>>>> {
        val result = MutableLiveData<ApiResult<GeneralResponseApi<List<Any>>>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.removeFarmer(body, token.authorization())
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