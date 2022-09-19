package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.login.DetailLogin
import com.javaindoku.yotaniniaga.model.profile.bank.Bank
import com.javaindoku.yotaniniaga.model.profile.kabupaten.Kabupaten
import com.javaindoku.yotaniniaga.model.profile.kecamatan.Kecamatan
import com.javaindoku.yotaniniaga.model.profile.kelurahan.Kelurahan
import com.javaindoku.yotaniniaga.model.profile.provinsi.Provinsi
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.ProfilePetaniDetail
import com.javaindoku.yotaniniaga.model.responseapi.GeneralResponseApi
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.repository.AddFarmerRepository
import com.javaindoku.yotaniniaga.repository.editprofilepetani.BankRepository
import com.javaindoku.yotaniniaga.repository.editprofilepetani.FarmerAddressRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

class AddFarmerViewModel @Inject constructor(
    val bankRepository: BankRepository,
    val addressRepository: FarmerAddressRepository,
    val repository: AddFarmerRepository
): BaseViewModel() {

    val farmerResult = MutableLiveData<ApiResult<GeneralResponseApi<Data>>>()
    val addFarmerResult = MutableLiveData<ApiResult<GeneralResponseApi<List<Any>>>>()
    var provinsiResult = MutableLiveData<ApiResult<Provinsi>>()
    var kabupatenResult = MutableLiveData<ApiResult<Kabupaten>>()
    var kecamatanResult = MutableLiveData<ApiResult<Kecamatan>>()
    var kelurahanResult = MutableLiveData<ApiResult<Kelurahan>>()
    var bankResult = MutableLiveData<ApiResult<Bank>>()

    fun getProvinsi(){
        addressRepository.getProvinsi().observeForever{
            provinsiResult.postValue(it)
        }
    }

    fun getkabupaten(provinsiId: Int){
        addressRepository.getKabupaten(provinsiId).observeForever{
            kabupatenResult.postValue(it)
        }
    }

    fun getKecamatan(kabupatenId: Int){
        addressRepository.getKecamatan(kabupatenId).observeForever{
            kecamatanResult.postValue(it)
        }
    }

    fun getKelurahan(kecamatanId: Int){
        addressRepository.getKelurahan(kecamatanId).observeForever{
            kelurahanResult.postValue(it)
        }
    }

    fun getBank(){
        bankRepository.getBank().observeForever{
            bankResult.postValue(it)
        }
    }

    fun fetchPetaniById(token: String, userId: String, petaniId: String) {
        val jsonBody = JSONObject()
        jsonBody.put("user_id", userId)
        jsonBody.put("petani_id", petaniId)
        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        repository.getFarmerDetail(token, body).observeForever {
            farmerResult.postValue(it)
        }
    }

    fun addFarmer(token: String, userId: String, koperasiId: String, farmer: Data, imageBase64: String) {
        val jsonBody = JSONObject()
        jsonBody.put("user_id", userId)
        jsonBody.put("koperasi_id", koperasiId)
        jsonBody.put("nama", farmer.nama)
        jsonBody.put("mobile", farmer.mobile)
        jsonBody.put("email", farmer.email)
        jsonBody.put("bank_id", farmer.bankId)
        jsonBody.put("akun_bank", farmer.akunBank)
        jsonBody.put("no_rekening", farmer.noRekening)
        jsonBody.put("latitude", farmer.latitude)
        jsonBody.put("longitude", farmer.longitude)
        jsonBody.put("alamat", farmer.alamat)
        jsonBody.put("rt", farmer.rt)
        jsonBody.put("rw", farmer.rw)
        jsonBody.put("provinsi_id", farmer.provinsiId)
        jsonBody.put("kabupaten_kota_id", farmer.kabupatenKotaId)
        jsonBody.put("kecamatan_id", farmer.kecamatanId)
        jsonBody.put("kelurahan_id", farmer.kelurahanId)
        jsonBody.put("kode_pos", farmer.kodePos)
        jsonBody.put("foto", "")

        if (!imageBase64.isNullOrEmpty()){
            jsonBody.put("foto", "data:image/jpeg;base64,$imageBase64")
        }

        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        repository.addPetani(token, body).observeForever {
            addFarmerResult.postValue(it)
        }
    }

    fun updateFarmer(token: String, userId: String, koperasiId: String, farmer: Data, imageBase64: String) {
        val jsonBody = JSONObject()
        jsonBody.put("user_id", userId)
        jsonBody.put("koperasi_id", koperasiId)
        jsonBody.put("petani_id", farmer.petaniId)
        jsonBody.put("nama", farmer.nama)
        jsonBody.put("mobile", farmer.mobile)
        jsonBody.put("email", farmer.email)
        jsonBody.put("bank_id", farmer.bankId)
        jsonBody.put("akun_bank", farmer.akunBank)
        jsonBody.put("no_rekening", farmer.noRekening)
        jsonBody.put("latitude", farmer.latitude)
        jsonBody.put("longitude", farmer.longitude)
        jsonBody.put("alamat", farmer.alamat)
        jsonBody.put("rt", farmer.rt)
        jsonBody.put("rw", farmer.rw)
        jsonBody.put("provinsi_id", farmer.provinsiId)
        jsonBody.put("kabupaten_kota_id", farmer.kabupatenKotaId)
        jsonBody.put("kecamatan_id", farmer.kecamatanId)
        jsonBody.put("kelurahan_id", farmer.kelurahanId)
        jsonBody.put("kode_pos", farmer.kodePos)
        jsonBody.put("foto", "")

        if (!imageBase64.isNullOrEmpty()){
            jsonBody.put("foto", "data:image/jpeg;base64,$imageBase64")
        }

        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        repository.editPetani(token, body).observeForever {
            addFarmerResult.postValue(it)
        }
    }
}
