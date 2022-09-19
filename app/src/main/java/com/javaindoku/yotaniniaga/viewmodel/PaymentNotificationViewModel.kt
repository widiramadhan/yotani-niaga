package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.notification.Notification
import com.javaindoku.yotaniniaga.model.notification.NotificationBody
import com.javaindoku.yotaniniaga.model.notification.NotificationReadBody
import com.javaindoku.yotaniniaga.repository.notification.paymentnotification.PaymentNotificationRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import javax.inject.Inject

class PaymentNotificationViewModel  @Inject constructor(private val repository: PaymentNotificationRepository) : BaseViewModel() {
    val pageInit = MutableLiveData<Int>()
    var notifBody = NotificationBody()
    var token: String = ""
    var totalScrollYRecyclerView = 0
    var readNotificationResult: MutableLiveData<ApiResult<Notification>> = MutableLiveData<ApiResult<Notification>>()

    private val repoResult = Transformations.map(pageInit) {
        repository.getNotification(notifBody, token)
    }

    val pos = Transformations.switchMap(repoResult) {
        it.pagedList
    }

    val networkState = Transformations.switchMap(repoResult) {
        it.networkState
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

    fun refresh(){
        repoResult.value?.refresh?.invoke()
    }

    fun readNotification(readNotifBody: NotificationReadBody, token: String) {
        repository.readNotification(readNotifBody, token).observeForever {
            readNotificationResult.postValue(it)
        }
    }
}