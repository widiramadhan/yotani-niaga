package com.javaindoku.yotaniniaga.repository.deliverykoperasi

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.javaindoku.yotaniniaga.model.deliverykoperasi.DeliveryKoperasi
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryData
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryReservationBody
import com.javaindoku.yotaniniaga.service.network.Api
import org.json.JSONObject

class DeliveryKoperasiDataSourceFactory(
    private val api: Api,
    private val deliveryReservationBody: JSONObject,
    private val token: String
) : DataSource.Factory<Int, DeliveryData>() {
    val sourceLiveData = MutableLiveData<DeliveryKoperasiDataSource>()
    override fun create(): DataSource<Int, DeliveryData> {
        val itemDataSource =
            DeliveryKoperasiDataSource(api, deliveryReservationBody, token)
        sourceLiveData.postValue(itemDataSource)
        return itemDataSource
    }
}