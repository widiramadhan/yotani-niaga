package com.javaindoku.yotaniniaga.repository.invoice

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.deliverykoperasi.DeliveryKoperasi
import com.javaindoku.yotaniniaga.model.factory.FactoryBody
import com.javaindoku.yotaniniaga.model.invoice.Invoice
import com.javaindoku.yotaniniaga.model.invoice.InvoiceRequest
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.utilities.authorization
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

class InvoiceDataSource (
    private val api: Api,
    private val jsonBody: JSONObject, private val token: String
) : PageKeyedDataSource<Int, Invoice>() {

    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()
    var pageIndex = 1
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
        callback: PageKeyedDataSource.LoadInitialCallback<Int, Invoice>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                jsonBody.put("page", "1")
                val body = jsonBody.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val response = api.getInvoices(body, token.authorization())
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
        callback: PageKeyedDataSource.LoadCallback<Int, Invoice>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                jsonBody.put("page", pageIndex)
                val body = jsonBody.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val response = api.getInvoices(body, token.authorization())
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

    override fun loadBefore(params: LoadParams<Int>,callback: LoadCallback<Int, Invoice>) {}
}