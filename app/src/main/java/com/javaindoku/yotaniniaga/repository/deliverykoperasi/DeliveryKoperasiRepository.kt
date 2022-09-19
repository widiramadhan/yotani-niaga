package com.javaindoku.yotaniniaga.repository.deliverykoperasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.deliverykoperasi.DeliveryKoperasi
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryData
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryReservationBody
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryReservationEditBody
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenBody
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenPetani
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.utilities.authorization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class DeliveryKoperasiRepository @Inject constructor(private val api: Api) {
    fun getDeliveries(deliveryReservationBody: JSONObject, token: String): Listing<DeliveryData> {
        val sourceFactory = DeliveryKoperasiDataSourceFactory(api, deliveryReservationBody, token)
        val config: PagedList.Config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(30).build()
        val liveData: LiveData<PagedList<DeliveryData>> = LivePagedListBuilder(sourceFactory, config).build()
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

    fun rescheduleReservation(deliveryReservationEditBody: DeliveryReservationEditBody, token: String) : MutableLiveData<ApiResult<ResponseApi>> {
        val result = MutableLiveData<ApiResult<ResponseApi>>()
        result.postValue(ApiResult.loading())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val body = Gson().toJson(deliveryReservationEditBody).toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val response = api.rescheduleReservation(body, token.authorization())
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