package com.javaindoku.yotaniniaga.repository.notification.newsnotification

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.notification.NotificationBody
import com.javaindoku.yotaniniaga.model.notification.NotificationData
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.utilities.authorization
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

class NewsNotificationDataSource (
    private val api: Api,
    private val notifBody: NotificationBody,
    private val token: String
) : PageKeyedDataSource<Int, NotificationData>() {

    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()
    var pageIndex : Long = 1
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
        callback: LoadInitialCallback<Int, NotificationData>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        GlobalScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    notifBody.page = "1"
                    val body = Gson().toJson(notifBody).toString()
                        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    val response = api.getNotification(body, token.authorization())
                    if (response.isSuccess) {
                        callback.onResult(response.data, null, 2)
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
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, NotificationData>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        GlobalScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    notifBody.page = pageIndex.toString()
                    val body = Gson().toJson(notifBody).toString()
                        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    val response = api.getNotification(body, token.authorization())
                    if (response.isSuccess) {
                        callback.onResult(response.data, pageIndex.toInt())
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
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, NotificationData>
    ) {

    }


    private var c = 0
    private fun generateListData(): List<NotificationData> {
        val tmp = mutableListOf<NotificationData>()
        /*val date = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzzz")
        for(i in 0..30) {
            if(i<2)
            {
                tmp.add(Notification("$i", sdf.format(date.time)  ,"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png", "Buah anda telah ditimbang oleh fulan"))
            }
            else
            {
                tmp.add(Notification("$i", sdf.format(date.time)  ,"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png", "Buah anda telah ditimbang oleh fulan", true))
            }
            date.add(Calendar.HOUR, c)
            c--
        }*/
        return tmp
    }

}