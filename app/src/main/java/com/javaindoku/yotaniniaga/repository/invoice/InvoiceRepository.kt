package com.javaindoku.yotaniniaga.repository.invoice

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.javaindoku.yotaniniaga.model.invoice.Invoice
import com.javaindoku.yotaniniaga.model.invoice.InvoiceRequest
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.NetworkState
import okhttp3.RequestBody
import org.json.JSONObject
import javax.inject.Inject

class InvoiceRepository @Inject constructor(private val api: Api) {
    fun getInvoice(body: JSONObject, token: String): Listing<Invoice> {
        val sourceFactory = InvoiceDataSourceFactory(api, body, token)
        val config: PagedList.Config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(30).build()
        val liveData: LiveData<PagedList<Invoice>> = LivePagedListBuilder(sourceFactory, config).build()
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
}