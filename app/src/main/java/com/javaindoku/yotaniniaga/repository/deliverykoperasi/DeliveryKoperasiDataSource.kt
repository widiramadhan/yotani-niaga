package com.javaindoku.yotaniniaga.repository.deliverykoperasi

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.deliverykoperasi.DeliveryKoperasi
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryData
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryReservationBody
import com.javaindoku.yotaniniaga.model.invoice.Invoice
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.utilities.authorization
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

class DeliveryKoperasiDataSource(
    private val api: Api,
    private val jsonBody: JSONObject,
    private val token: String
) : PageKeyedDataSource<Int, DeliveryData>() {

    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()
    var pageIndex: Int = 1
    val totalPage = MutableLiveData<Int>()
    val lastNumber = MutableLiveData(0)

    private var retry: (() -> Any)? = null


    fun retryAllFiled() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            CoroutineScope(Dispatchers.IO).launch {
                it.invoke()
            }
        }
    }

    override fun loadInitial(
        params: PageKeyedDataSource.LoadInitialParams<Int>,
        callback: PageKeyedDataSource.LoadInitialCallback<Int, DeliveryData>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                jsonBody.put("page", "1")
                val body = jsonBody.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val response = api.getDeliveryReservatioon(body, token.authorization())
                if (response.isSuccess) {
                    callback.onResult(response.data!!.toMutableList(), null, 2)
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                }
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> {
                        networkState.postValue(NetworkState.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {

                        networkState.postValue(NetworkState.errorInt(R.string.srServerError))
                    }
                    is ConnectException -> {
                        networkState.postValue(NetworkState.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    else -> {
                        networkState.postValue(NetworkState.errorInt(R.string.srServerError))
                    }
                }
            }
            pageIndex++
        }
    }

    override fun loadAfter(
        params: PageKeyedDataSource.LoadParams<Int>,
        callback: PageKeyedDataSource.LoadCallback<Int, DeliveryData>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                jsonBody.put("page", pageIndex)
                val body = jsonBody.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val response = api.getDeliveryReservatioon(body, token.authorization())
                if (response.isSuccess) {
                    callback.onResult(response.data!!.toMutableList(), pageIndex)
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                }
            } catch (e: Exception) {

                when (e) {
                    is UnknownHostException -> {
                        networkState.postValue(NetworkState.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {

                        networkState.postValue(NetworkState.errorInt(R.string.srServerError))
                    }
                    is ConnectException -> {
                        networkState.postValue(NetworkState.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    else -> {
                        networkState.postValue(NetworkState.errorInt(R.string.srServerError))
                    }
                }
            }
            pageIndex++
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, DeliveryData>
    ) {

    }


    private fun generateListData(): List<DeliveryKoperasi> {
        val tmp = mutableListOf<DeliveryKoperasi>()
        for (i in 0..30) {
            tmp.add(
                DeliveryKoperasi(
                    id = "$i",
                    date = "2020-08-21 15:48:33 GMT+07:00",
                    status = "Dikemas",
                    invoiceNo = "INVALSJDKLASJD",
                    factory = "PT Arya Duta",
                    totalReservation = "10 Ton",
                    idUnique = "ASJKDLKA",
                    name = "Fulan",
                    image = "https://ci5.googleusercontent.com/proxy/ZYGgMtVL-iaTQZQWVHSkSTaE1zZKaO3L-Vu9DnnNI2XsC58Q2W5XC5aExagv9_sSV4_7YqxwbUc1kSU4Yi-eHqS-0niOWQaPujy-NpQ0VSu6H3_PBErq6-QEeHz1nOrmMw=s0-d-e1-ft#http://gitlab.zoom.us/w_p/95382753957/418b86ee-95ff-4cb1-a1e8-06465d6460c6.png",
                    numberPlate = "B6459WHA",
                    barcode = "${1273812 + i}"
                )
            )
        }
        return tmp
    }

}