package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.checkBorrower.CheckBorrower
import com.javaindoku.yotaniniaga.model.entity.DummyEntity
import com.javaindoku.yotaniniaga.model.login.Login
import com.javaindoku.yotaniniaga.repository.login.CheckBorrowerRepository
import com.javaindoku.yotaniniaga.repository.login.LoginRepository
import com.javaindoku.yotaniniaga.service.database.dao.DummyEntityDao
import com.javaindoku.yotaniniaga.service.network.ApiResult
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val checkBorrowerRepository: CheckBorrowerRepository,
    val dummyEntityDao: DummyEntityDao
) : BaseViewModel() {

    val loginResult = MutableLiveData<ApiResult<Login>>()
    val checkBorrowerResult = MutableLiveData<ApiResult<CheckBorrower>>()

    fun testDb() {
        viewModelScope.launch {
            dummyEntityDao.insert(DummyEntity(1, "test"))
        }
    }

    fun login(phoneNumber: String, password: String, fcmToken: String) {
        loginRepository.login(phoneNumber, password, fcmToken).observeForever {
            loginResult.postValue(it)
        }
    }

    fun checkBorrower(mobile: String) {
        checkBorrowerRepository.checkBorrower(mobile).observeForever {
            checkBorrowerResult.postValue(it)
        }
    }


}