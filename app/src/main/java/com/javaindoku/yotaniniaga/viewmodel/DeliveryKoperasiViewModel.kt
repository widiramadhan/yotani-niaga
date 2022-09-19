package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.checkBorrower.CheckBorrower
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryReservationBody
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryReservationEditBody
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenPetani
import com.javaindoku.yotaniniaga.model.invoiceDetail.InvoiceDetail
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.repository.deliverykoperasi.DeliveryKoperasiRepository
import com.javaindoku.yotaniniaga.repository.invoice.InoviceDetailRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import org.json.JSONObject
import javax.inject.Inject

class DeliveryKoperasiViewModel @Inject constructor(
    private val repository: DeliveryKoperasiRepository,
    private val invoiceDetailRepository: InoviceDetailRepository) : BaseViewModel() {
    var totalScrollYRecyclerView = 0
    val pageInit = MutableLiveData<Int>()
    val deliveryReservationBody = JSONObject()
    val deliveryReservationEditBody = MutableLiveData<DeliveryReservationEditBody>()
    val invoiceDetailResult = MutableLiveData<ApiResult<InvoiceDetail>>()

    var token: String = ""

    private val repoResult = Transformations.map(pageInit) {
        repository.getDeliveries(deliveryReservationBody, token)
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

    val rescheduleResponse = MutableLiveData<ApiResult<ResponseApi>>()

    fun rescheduleReservation(token: String) {
        repository.rescheduleReservation(deliveryReservationEditBody.value!!, token).observeForever {
            rescheduleResponse.postValue(it)
        }
    }

    fun invoiceDetail(userToken: String, userId: String, noInvoice: String) {
        invoiceDetailRepository.invoiceDetail(userToken, userId, noInvoice).observeForever {
            invoiceDetailResult.postValue(it)
        }
    }

}