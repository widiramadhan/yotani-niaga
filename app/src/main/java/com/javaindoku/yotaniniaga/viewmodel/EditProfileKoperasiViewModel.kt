package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.profile.badanusaha.BadanUsaha
import com.javaindoku.yotaniniaga.model.profile.bank.Bank
import com.javaindoku.yotaniniaga.model.profile.kabupaten.Kabupaten
import com.javaindoku.yotaniniaga.model.profile.kecamatan.Kecamatan
import com.javaindoku.yotaniniaga.model.profile.kelurahan.Kelurahan
import com.javaindoku.yotaniniaga.model.profile.provinsi.Provinsi
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.ProfilePetaniDetail
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.repository.editprofilekoperasi.EditProfileKoperasiRepository
import com.javaindoku.yotaniniaga.repository.editprofilepetani.BankRepository
import com.javaindoku.yotaniniaga.repository.editprofilepetani.FarmerAddressRepository
import com.javaindoku.yotaniniaga.repository.profilePetaniDetail.ProfilePetaniDetailRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class EditProfileKoperasiViewModel @Inject constructor(
    private val repository: EditProfileKoperasiRepository,
    private val profileRepository: ProfilePetaniDetailRepository,
    private val addressRepository: FarmerAddressRepository,
    private val bankRepository: BankRepository,
): BaseViewModel() {
    var editProfileResult = MutableLiveData<ApiResult<ResponseApi>>()
    val edtName = MutableLiveData<String>()
    var userProfile = MutableLiveData<ApiResult<ProfilePetaniDetail>>()
    var bankResult = MutableLiveData<ApiResult<Bank>>()
    var badanUsahaResult = MutableLiveData<ApiResult<BadanUsaha>>()
    var provinsiResult : MutableLiveData<ApiResult<Provinsi>> = MutableLiveData<ApiResult<Provinsi>>()
    var kabupatenResult : MutableLiveData<ApiResult<Kabupaten>> = MutableLiveData<ApiResult<Kabupaten>>()
    var kecamatanResult : MutableLiveData<ApiResult<Kecamatan>> = MutableLiveData<ApiResult<Kecamatan>>()
    var kelurahanResult : MutableLiveData<ApiResult<Kelurahan>> = MutableLiveData<ApiResult<Kelurahan>>()

    fun updateProfile(token: String, profile: Data, imageBase64: String)  {
        val jsonBody = JSONObject()
        jsonBody.put("user_id", profile.userId)
        jsonBody.put("badan_usaha", profile.badanUsaha)
        jsonBody.put("nama_koperasi", profile.nama)
        jsonBody.put("bank_id", profile.bankId)
        jsonBody.put("akun_bank", profile.akunBank)
        jsonBody.put("no_rekening", profile.noRekening)
        jsonBody.put("latitude", profile.latitude)
        jsonBody.put("longitude", profile.longitude)
        jsonBody.put("alamat", profile.alamat)
        jsonBody.put("rt", profile.rt)
        jsonBody.put("rw", profile.rw)
        jsonBody.put("provinsi_id", profile.provinsiId)
        jsonBody.put("kabupaten_kota_id", profile.kabupatenKotaId)
        jsonBody.put("kecamatan_id", profile.kecamatanId)
        jsonBody.put("kelurahan_id", profile.kelurahanId)
        jsonBody.put("kode_pos", profile.kodePos)
        jsonBody.put("foto", "")

        if (!imageBase64.isNullOrEmpty()){
            jsonBody.put("foto", "data:image/jpeg;base64,$imageBase64")
        }

        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        repository.editProfileKoperasi(token, body).observeForever {
            editProfileResult.postValue(it)
        }
    }

    fun fetchProfilePetaniDetail(userToken: String, userId: String){
        profileRepository.profilePetaniDetail(userToken, userId).observeForever{
            userProfile.postValue(it)
        }
    }

    fun getBank(){
        bankRepository.getBank().observeForever{
            bankResult.postValue(it)
        }
    }

    fun getBadanUsaha(){
        bankRepository.getBadanUsaha().observeForever{
            badanUsahaResult.postValue(it)
        }
    }

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
}