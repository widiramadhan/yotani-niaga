package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.entity.DummyEntity
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.ProfilePetaniDetail
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.repository.profilePetaniDetail.ProfilePetaniDetailRepository
import com.javaindoku.yotaniniaga.service.database.dao.DummyEntityDao
import com.javaindoku.yotaniniaga.service.network.ApiResult
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class ProfilePetaniDetailViewModel @Inject constructor(private val repository: ProfilePetaniDetailRepository): BaseViewModel() {
    var profilePetaniDetailResult : MutableLiveData<ApiResult<ProfilePetaniDetail>> = MutableLiveData<ApiResult<ProfilePetaniDetail>>()

    fun profilePetaniDetail(userToken: String, userId: String){
        repository.profilePetaniDetail(userToken, userId).observeForever{
            profilePetaniDetailResult.postValue(it)
        }
    }

}