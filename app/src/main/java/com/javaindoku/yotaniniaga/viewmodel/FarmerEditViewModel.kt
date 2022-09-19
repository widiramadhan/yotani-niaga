package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.profile.bank.Bank
import com.javaindoku.yotaniniaga.repository.editprofilepetani.BankRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import javax.inject.Inject

class FarmerEditViewModel @Inject constructor(private val repository: BankRepository): BaseViewModel() {
    var bankResult : MutableLiveData<ApiResult<Bank>> = MutableLiveData<ApiResult<Bank>>()

    fun getBank(){
        repository.getBank().observeForever{
            bankResult.postValue(it)
        }
    }
}