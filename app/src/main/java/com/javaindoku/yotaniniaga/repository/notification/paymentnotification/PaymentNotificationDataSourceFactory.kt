package com.javaindoku.yotaniniaga.repository.notification.paymentnotification

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.javaindoku.yotaniniaga.model.notification.NotificationBody
import com.javaindoku.yotaniniaga.model.notification.NotificationData
import com.javaindoku.yotaniniaga.service.network.Api

class PaymentNotificationDataSourceFactory (
    private val api: Api,
    private val notifBody: NotificationBody,
    private val token: String
) : DataSource.Factory<Int, NotificationData>() {
    val sourceLiveData = MutableLiveData<PaymentNotificationDataSource>()
    override fun create(): DataSource<Int, NotificationData> {
        val itemDataSource = PaymentNotificationDataSource(api, notifBody, token)
        sourceLiveData.postValue(itemDataSource)
        return itemDataSource
    }
}