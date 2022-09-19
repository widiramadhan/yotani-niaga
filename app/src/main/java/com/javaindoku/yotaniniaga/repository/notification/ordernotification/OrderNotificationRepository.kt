package com.javaindoku.yotaniniaga.repository.notification.ordernotification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.model.notification.Notification
import com.javaindoku.yotaniniaga.model.notification.NotificationBody
import com.javaindoku.yotaniniaga.model.notification.NotificationData
import com.javaindoku.yotaniniaga.model.notification.NotificationReadBody
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
import com.javaindoku.yotaniniaga.R

class OrderNotificationRepository @Inject constructor(private val api: Api) {
    fun getNotification(notifBody: NotificationBody, token: String): Listing<NotificationData> {
        val sourceFactory = OrderNotificationDataSourceFactory(api, notifBody, token)
        val config: PagedList.Config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(30).build()
        val liveData: LiveData<PagedList<NotificationData>> = LivePagedListBuilder(sourceFactory, config).build()
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

    fun readNotification(readNotifBody: NotificationReadBody, token: String): MutableLiveData<ApiResult<Notification>> {
        val result = MutableLiveData<ApiResult<Notification>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val body = Gson().toJson(readNotifBody).toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val response = api.readNotification(body, token.authorization())
                result.postValue(ApiResult.success(response))
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        if (e.response() != null) {
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