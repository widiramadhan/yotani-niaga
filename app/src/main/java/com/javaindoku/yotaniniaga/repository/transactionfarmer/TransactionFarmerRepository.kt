package com.javaindoku.yotaniniaga.repository.transactionfarmer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.checkGarden.CheckGarden
import com.javaindoku.yotaniniaga.model.checkGarden.CheckGardenFarmerRequest
import com.javaindoku.yotaniniaga.model.checkGarden.CheckGardenKoperasiRequest
import com.javaindoku.yotaniniaga.model.factory.FactoryBody
import com.javaindoku.yotaniniaga.model.transactionfarmer.TransactionFarmer
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.utilities.authorization
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class TransactionFarmerRepository @Inject constructor(private val api: Api) {
    fun getTransactionFarmer(factoryBody: FactoryBody, token: String): Listing<TransactionFarmer> {
        val sourceFactory = TransactionFarmerDataSourceFactory(api, factoryBody, token)
        val config: PagedList.Config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(30).build()
        val liveData: LiveData<PagedList<TransactionFarmer>> = LivePagedListBuilder(sourceFactory, config).build()
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

    fun checkGardenKoperasi(checkGardenKoperasiRequest: CheckGardenKoperasiRequest, token: String) : MutableLiveData<ApiResult<CheckGarden>> {
        val body = Gson().toJson(checkGardenKoperasiRequest).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val result = MutableLiveData<ApiResult<CheckGarden>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.checkGardenKoperasi(body, token.authorization())
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
                        //result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }

    fun checkGardenFarmer(checkGardenFarmerRequest: CheckGardenFarmerRequest, token: String) : MutableLiveData<ApiResult<CheckGarden>> {
        val body = Gson().toJson(checkGardenFarmerRequest).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val result = MutableLiveData<ApiResult<CheckGarden>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.checkGardenFarmer(body, token.authorization())
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
                        //result.postValue(ApiResult.errorInt(R.string.srServerError))
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