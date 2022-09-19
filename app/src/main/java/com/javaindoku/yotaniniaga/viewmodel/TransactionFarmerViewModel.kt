package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.checkGarden.CheckGarden
import com.javaindoku.yotaniniaga.model.checkGarden.CheckGardenFarmerRequest
import com.javaindoku.yotaniniaga.model.checkGarden.CheckGardenKoperasiRequest
import com.javaindoku.yotaniniaga.model.factory.FactoryBody
import com.javaindoku.yotaniniaga.repository.transactionfarmer.TransactionFarmerRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import javax.inject.Inject

class TransactionFarmerViewModel @Inject constructor(private val repository: TransactionFarmerRepository) : BaseViewModel() {
    val pageInit = MutableLiveData<Int>()
    var factoryBody = FactoryBody()
    var token: String = ""
    var totalScrollYRecyclerView = 0
    var checkGardenResult : MutableLiveData<ApiResult<CheckGarden>> = MutableLiveData<ApiResult<CheckGarden>>()

    private val repoResult = Transformations.map(pageInit) {
        repository.getTransactionFarmer(factoryBody, token)
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

    fun checkGardenKoperasi(checkGardenKoperasiRequest: CheckGardenKoperasiRequest, token: String)  {
        repository.checkGardenKoperasi(checkGardenKoperasiRequest, token).observeForever {
            checkGardenResult.postValue(it)
        }
    }

    fun checkGardenFarmer(checkGardenFarmerRequest: CheckGardenFarmerRequest, token: String)  {
        repository.checkGardenFarmer(checkGardenFarmerRequest, token).observeForever {
            checkGardenResult.postValue(it)
        }
    }

}