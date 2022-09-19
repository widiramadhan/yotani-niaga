package com.javaindoku.yotaniniaga.repository.invoice

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.javaindoku.yotaniniaga.model.factory.FactoryBody
import com.javaindoku.yotaniniaga.model.invoice.Invoice
import com.javaindoku.yotaniniaga.model.invoice.InvoiceRequest
import com.javaindoku.yotaniniaga.service.network.Api
import okhttp3.RequestBody
import org.json.JSONObject

class InvoiceDataSourceFactory (private val api: Api, private val body: JSONObject, private val token: String) : DataSource.Factory<Int, Invoice>() {
    val sourceLiveData = MutableLiveData<InvoiceDataSource>()
    override fun create(): DataSource<Int, Invoice> {
        val itemDataSource = InvoiceDataSource(api, body, token)
        sourceLiveData.postValue(itemDataSource)
        return itemDataSource
    }
}