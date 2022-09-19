package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.factory.FactoryBody
import com.javaindoku.yotaniniaga.model.invoice.Invoice
import com.javaindoku.yotaniniaga.model.invoice.InvoiceRequest
import com.javaindoku.yotaniniaga.repository.invoice.InvoiceRepository
import okhttp3.RequestBody
import org.json.JSONObject
import javax.inject.Inject

class InvoiceViewModel @Inject constructor(private val repository: InvoiceRepository) : BaseViewModel() {
    var totalScrollYRecyclerView = 0
    val pageInit = MutableLiveData<Int>()
    var token: String = ""
    var body: JSONObject = JSONObject()

    private val repoResult = Transformations.map(pageInit) {
        repository.getInvoice(body, token)
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
}