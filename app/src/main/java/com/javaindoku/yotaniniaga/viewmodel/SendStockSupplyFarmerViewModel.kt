package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.sendstocksupplyfarmer.AddReservasi
import com.javaindoku.yotaniniaga.model.sendstocksupplyfarmer.SendStockSupplyFarmerBody
import com.javaindoku.yotaniniaga.repository.sendstocksupplyfarmer.SendStockSupplyFarmerRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import javax.inject.Inject

class SendStockSupplyFarmerViewModel @Inject constructor(private val repository: SendStockSupplyFarmerRepository) : BaseViewModel() {
    var sendStockResult : MutableLiveData<ApiResult<AddReservasi>> = MutableLiveData<ApiResult<AddReservasi>>()
    var confirmStockResult : MutableLiveData<ApiResult<AddReservasi>> = MutableLiveData<ApiResult<AddReservasi>>()

    fun sendStock(sendStockSupplyFarmerBody: SendStockSupplyFarmerBody, token: String)  {
        repository.sendStockSupply(sendStockSupplyFarmerBody, token).observeForever {
            sendStockResult.postValue(it)
        }
    }

    fun confirmStock(sendStockSupplyFarmerBody: SendStockSupplyFarmerBody, token: String)  {
        repository.confirmStockSupply(sendStockSupplyFarmerBody, token).observeForever {
            confirmStockResult.postValue(it)
        }
    }
}