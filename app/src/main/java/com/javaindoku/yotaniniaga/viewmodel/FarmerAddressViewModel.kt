package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.profile.bank.Bank
import com.javaindoku.yotaniniaga.model.profile.kabupaten.Kabupaten
import com.javaindoku.yotaniniaga.model.profile.kecamatan.Kecamatan
import com.javaindoku.yotaniniaga.model.profile.kelurahan.Kelurahan
import com.javaindoku.yotaniniaga.model.profile.provinsi.Provinsi
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.repository.editprofilepetani.EditProfilePetaniRepository
import com.javaindoku.yotaniniaga.repository.editprofilepetani.FarmerAddressRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class FarmerAddressViewModel @Inject constructor(private val repository: FarmerAddressRepository): BaseViewModel() {
    var provinsiResult : MutableLiveData<ApiResult<Provinsi>> = MutableLiveData<ApiResult<Provinsi>>()
    var kabupatenResult : MutableLiveData<ApiResult<Kabupaten>> = MutableLiveData<ApiResult<Kabupaten>>()
    var kecamatanResult : MutableLiveData<ApiResult<Kecamatan>> = MutableLiveData<ApiResult<Kecamatan>>()
    var kelurahanResult : MutableLiveData<ApiResult<Kelurahan>> = MutableLiveData<ApiResult<Kelurahan>>()

    fun getProvinsi(){
        repository.getProvinsi().observeForever{
            provinsiResult.postValue(it)
        }
    }

    fun getkabupaten(provinsiId: Int){
        repository.getKabupaten(provinsiId).observeForever{
            kabupatenResult.postValue(it)
        }
    }

    fun getKecamatan(kabupatenId: Int){
        repository.getKecamatan(kabupatenId).observeForever{
            kecamatanResult.postValue(it)
        }
    }

    fun getKelurahan(kecamatanId: Int){
        repository.getKelurahan(kecamatanId).observeForever{
            kelurahanResult.postValue(it)
        }
    }
}