package com.javaindoku.yotaniniaga.repository.transactionfarmer

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.factory.FactoryBody
import com.javaindoku.yotaniniaga.model.transactionfarmer.TransactionFarmer
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.utilities.authorization
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

class TransactionFarmerDataSource (private val api: Api, private val factoryBody: FactoryBody, private val token: String) : PageKeyedDataSource<Int, TransactionFarmer>() {


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
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, TransactionFarmer>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        GlobalScope.launch {
//            delay(3000L)
//            callback.onResult(generateListData(), null, 1)
//            networkState.postValue(NetworkState.LOADED)
//            initialLoad.postValue(NetworkState.LOADED)
//            pageIndex++
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val body = Gson().toJson(factoryBody).toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    val response = api.getFactories(body, token.authorization())
                    if(response.isSuccess)
                    {
                        callback.onResult(response.data , null, null)
                        networkState.postValue(NetworkState.LOADED)
                    }
                    else{
                        networkState.postValue(NetworkState.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                }
                catch (e: Exception)
                {

                    when(e) {
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
            }
        }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, TransactionFarmer>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        GlobalScope.launch {
//            delay(3000L)
//            callback.onResult(generateListData(), pageIndex)
//            networkState.postValue(NetworkState.LOADED)
//            initialLoad.postValue(NetworkState.LOADED)
//            pageIndex++
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, TransactionFarmer>
    ) {

    }


    private fun generateListData(): List<TransactionFarmer> {
        val tmp = mutableListOf<TransactionFarmer>()
//        for(i in 1..30) {
//            tmp.add(
//                TransactionFarmer(
//                id = "$i",
//                companyName = "PT Indocyber Global $i",
//                price = "${i}0000",
//                image = "https://ssl.gstatic.com/ui/v1/icons/mail/rfr/logo_gmail_lockup_default_1x.png",
//                quota = "100",
//                quotaUnit = "Ton",
//                quotaFill = "50/500",
//                quotaFillUnit = "Ton"
//                )
//            )
//        }
        return tmp
    }

}