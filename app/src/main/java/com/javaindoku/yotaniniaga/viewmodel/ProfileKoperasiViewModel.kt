package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.model.responseapi.GeneralResponseApi
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.repository.profilekoperasi.ProfileKoperasiRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class ProfileKoperasiViewModel @Inject constructor(private val repository: ProfileKoperasiRepository) :
    BaseViewModel() {
    val pageInit = MutableLiveData<Int>()
    val jsonBody = JSONObject()
    var token = ""
    val checkPetaniResult = MutableLiveData<ApiResult<GeneralResponseApi<Data>>>()
    val postResult = MutableLiveData<ApiResult<GeneralResponseApi<List<Any>>>>()

    private val repoResult = Transformations.map(pageInit) {
        repository.getFarmer(jsonBody, token)
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

    fun removeFarmer(userId: String, koperasiId: String, petaniId: String, token: String) {
        val jsonBody = JSONObject()
        jsonBody.put("user_id", userId)
        jsonBody.put("koperasi_id", koperasiId)
        jsonBody.put("petani_id", petaniId)
        val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        repository.removeFarmer(requestBody, token).observeForever{
            postResult.postValue(it)
        }
    }

    fun checkIsFarmerBound(userId: String, mobile: String, token: String) {
        val jsonBody = JSONObject()
        jsonBody.put("user_id", userId)
        jsonBody.put("mobile", mobile)
        val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        repository.checkIsFarmerBound(requestBody, token).observeForever{
            checkPetaniResult.postValue(it)
        }
    }

}