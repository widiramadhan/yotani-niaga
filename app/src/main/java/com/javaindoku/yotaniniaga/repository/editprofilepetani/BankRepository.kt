package com.javaindoku.yotaniniaga.repository.editprofilepetani

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.profile.agama.Agama
import com.javaindoku.yotaniniaga.model.profile.badanusaha.BadanUsaha
import com.javaindoku.yotaniniaga.model.profile.bank.Bank
import com.javaindoku.yotaniniaga.model.profile.bidangpekerjaan.BidangPekerjaan
import com.javaindoku.yotaniniaga.model.profile.pendidikan.Pendidikan
import com.javaindoku.yotaniniaga.model.profile.pengalamankerja.PengalamanKerja
import com.javaindoku.yotaniniaga.model.profile.profesi.Profesi
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.ApiResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class BankRepository @Inject constructor(private val api: Api) {

    fun getBank() : MutableLiveData<ApiResult<Bank>> {
        val result = MutableLiveData<ApiResult<Bank>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.getBank()
                result.postValue(ApiResult.success(response))
            }
            catch (e: Exception)
            {
                when(e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        if(e.response()!=null){
                            val errorJson = e.response()!!.errorBody()!!.string()
                        }
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }

    fun getBadanUsaha() : MutableLiveData<ApiResult<BadanUsaha>> {
        val result = MutableLiveData<ApiResult<BadanUsaha>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.getBadanUsaha()
                result.postValue(ApiResult.success(response))
            }
            catch (e: Exception)
            {
                when(e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        if(e.response()!=null){
                            //val errorJson = e.response()!!.errorBody()!!.string()
                        }
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }

    fun getReligion() : MutableLiveData<ApiResult<Agama>> {
        val result = MutableLiveData<ApiResult<Agama>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.getReligion()
                result.postValue(ApiResult.success(response))
            }
            catch (e: Exception)
            {
                when(e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        if(e.response()!=null){
                            val errorJson = e.response()!!.errorBody()!!.string()
                        }
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }

    fun getEducation() : MutableLiveData<ApiResult<Pendidikan>> {
        val result = MutableLiveData<ApiResult<Pendidikan>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.getEducation()
                result.postValue(ApiResult.success(response))
            }
            catch (e: Exception)
            {
                when(e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        if(e.response()!=null){
                            val errorJson = e.response()!!.errorBody()!!.string()
                        }
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }

    fun getProfession() : MutableLiveData<ApiResult<Profesi>> {
        val result = MutableLiveData<ApiResult<Profesi>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.getProfession()
                result.postValue(ApiResult.success(response))
            }
            catch (e: Exception)
            {
                when(e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        if(e.response()!=null){
                            val errorJson = e.response()!!.errorBody()!!.string()
                        }
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }

    fun getFieldWork() : MutableLiveData<ApiResult<BidangPekerjaan>> {
        val result = MutableLiveData<ApiResult<BidangPekerjaan>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.getFieldWork()
                result.postValue(ApiResult.success(response))
            }
            catch (e: Exception)
            {
                when(e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        if(e.response()!=null){
                            val errorJson = e.response()!!.errorBody()!!.string()
                        }
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }

    fun getWorkExperience() : MutableLiveData<ApiResult<PengalamanKerja>> {
        val result = MutableLiveData<ApiResult<PengalamanKerja>>()
        result.postValue(ApiResult.loading())
        GlobalScope.launch {
            try {
                val response = api.getWOrkExperience()
                result.postValue(ApiResult.success(response))
            }
            catch (e: Exception)
            {
                when(e) {
                    is UnknownHostException -> {
                        result.postValue(ApiResult.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        if(e.response()!=null){
                            val errorJson = e.response()!!.errorBody()!!.string()
                        }
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                    else -> {
                        result.postValue(ApiResult.errorInt(R.string.srServerError))
                    }
                }
            }
        }
        return result
    }

}