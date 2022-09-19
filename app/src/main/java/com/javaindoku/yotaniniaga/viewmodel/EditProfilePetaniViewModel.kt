package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.checkBorrower.CheckBorrower
import com.javaindoku.yotaniniaga.model.ocrKtp.OcrKtp
import com.javaindoku.yotaniniaga.model.profile.agama.Agama
import com.javaindoku.yotaniniaga.model.profile.bank.Bank
import com.javaindoku.yotaniniaga.model.profile.bidangpekerjaan.BidangPekerjaan
import com.javaindoku.yotaniniaga.model.profile.editprofile.EditProfile
import com.javaindoku.yotaniniaga.model.profile.kabupaten.Kabupaten
import com.javaindoku.yotaniniaga.model.profile.kecamatan.Kecamatan
import com.javaindoku.yotaniniaga.model.profile.kelurahan.Kelurahan
import com.javaindoku.yotaniniaga.model.profile.pendidikan.Pendidikan
import com.javaindoku.yotaniniaga.model.profile.pengalamankerja.PengalamanKerja
import com.javaindoku.yotaniniaga.model.profile.profesi.Profesi
import com.javaindoku.yotaniniaga.model.profile.provinsi.Provinsi
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.ProfilePetaniDetail
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.repository.editprofilepetani.EditProfilePetaniRepository
import com.javaindoku.yotaniniaga.repository.editprofilepetani.BankRepository
import com.javaindoku.yotaniniaga.repository.editprofilepetani.FarmerAddressRepository
import com.javaindoku.yotaniniaga.repository.login.CheckBorrowerRepository
import com.javaindoku.yotaniniaga.repository.ocrktp.OcrKtpRepository
import com.javaindoku.yotaniniaga.repository.profilePetaniDetail.ProfilePetaniDetailRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class EditProfilePetaniViewModel @Inject constructor(
    private val repository: EditProfilePetaniRepository,
    private val profileRepository: ProfilePetaniDetailRepository,
    private val addressRepository: FarmerAddressRepository,
    private val bankRepository: BankRepository,
    private val checkBorrowerRepository: CheckBorrowerRepository,
    private val ocrKtpRepository: OcrKtpRepository
): BaseViewModel() {
    var editProfileResult = MutableLiveData<ApiResult<EditProfile>>()
    val edtName = MutableLiveData<String>()
    var userProfile = MutableLiveData<ApiResult<ProfilePetaniDetail>>()
    var bankResult = MutableLiveData<ApiResult<Bank>>()
    var provinsiResult : MutableLiveData<ApiResult<Provinsi>> = MutableLiveData<ApiResult<Provinsi>>()
    var kabupatenResult : MutableLiveData<ApiResult<Kabupaten>> = MutableLiveData<ApiResult<Kabupaten>>()
    var kecamatanResult : MutableLiveData<ApiResult<Kecamatan>> = MutableLiveData<ApiResult<Kecamatan>>()
    var kelurahanResult : MutableLiveData<ApiResult<Kelurahan>> = MutableLiveData<ApiResult<Kelurahan>>()
    var religonResult = MutableLiveData<ApiResult<Agama>>()
    var educationResult = MutableLiveData<ApiResult<Pendidikan>>()
    var professionResult = MutableLiveData<ApiResult<Profesi>>()
    var fieldWorkResult = MutableLiveData<ApiResult<BidangPekerjaan>>()
    var workExperienceResult = MutableLiveData<ApiResult<PengalamanKerja>>()
    var provinsiDomisiliResult : MutableLiveData<ApiResult<Provinsi>> = MutableLiveData<ApiResult<Provinsi>>()
    var kabupatenDomisiliResult : MutableLiveData<ApiResult<Kabupaten>> = MutableLiveData<ApiResult<Kabupaten>>()
    var kecamatanDomisiliResult : MutableLiveData<ApiResult<Kecamatan>> = MutableLiveData<ApiResult<Kecamatan>>()
    var kelurahanDomisiliResult : MutableLiveData<ApiResult<Kelurahan>> = MutableLiveData<ApiResult<Kelurahan>>()
    val checkBorrowerResult = MutableLiveData<ApiResult<CheckBorrower>>()
    val ocrKtpResult = MutableLiveData<ApiResult<OcrKtp>>()

    fun updateProfile(token: String, profile: Data, imageBase64: String, imageBase64KTP: String,
                      imageBase64BpjsTK: String, imageBase64BpjsKes: String, imageBase64KK: String)  {

        val jsonBody = JSONObject()
        jsonBody.put("user_id", profile.userId)
        jsonBody.put("nama", profile.nama)
        jsonBody.put("mobile", profile.mobile)
        jsonBody.put("email", profile.email)
        jsonBody.put("bank_id", profile.bankId)
        jsonBody.put("akun_bank", profile.akunBank)
        jsonBody.put("no_rekening", profile.noRekening)
        jsonBody.put("no_ktp", profile.noKtp)
        jsonBody.put("npwp", profile.npwp)
        jsonBody.put("jenis_kelamin", profile.jenisKelamin)
        jsonBody.put("tempat_lahir", profile.tempatLahir)
        jsonBody.put("tanggal_lahir", profile.tanggalLahir)
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
        jsonBody.put("ktp_alamat", profile.ktpAlamat)
        jsonBody.put("ktp_rt", profile.ktpRT)
        jsonBody.put("ktp_rw", profile.ktpRW)
        jsonBody.put("ktp_provinsi_id", profile.ktpProvinsiId)
        jsonBody.put("ktp_kabupaten_kota_id", profile.ktpKabupatenKotaId)
        jsonBody.put("ktp_kecamatan_id", profile.ktpKecamatanId)
        jsonBody.put("ktp_kelurahan_id", profile.ktpKelurahanId)
        jsonBody.put("ktp_kode_pos", profile.ktpKodePos)
        jsonBody.put("agama_id", profile.agamaId)
        jsonBody.put("status_nikah_id", profile.statusNikahId)
        jsonBody.put("profesi_id", profile.pekerjaanId)
        jsonBody.put("industri_id", profile.industriId)
        jsonBody.put("online_work_id", profile.onlineWorkId)
        jsonBody.put("gaji", profile.gaji)
        jsonBody.put("periode_pengalaman_id", profile.periodePengalamanId)
        jsonBody.put("pendidikan_id", profile.pendidikanId)
        jsonBody.put("total_aset", profile.totalAset)
        jsonBody.put("home_ownership_id", profile.homeOwnershipId)
        jsonBody.put("bpjs_tk", profile.bpjsTk)
        jsonBody.put("bpjs_kes", profile.bpjsKes)
        jsonBody.put("no_kk", profile.noKk)
        if (!imageBase64.isNullOrEmpty()){
            jsonBody.put("foto", "data:image/jpeg;base64,$imageBase64")
        }
        if (!imageBase64KTP.isNullOrEmpty()){
            jsonBody.put("foto_ktp", "data:image/jpeg;base64,$imageBase64KTP")
        }
        if (!imageBase64BpjsTK.isNullOrEmpty()){
            jsonBody.put("foto_bpjs_tk", "data:image/jpeg;base64,$imageBase64BpjsTK")
        }
        if (!imageBase64BpjsKes.isNullOrEmpty()){
            jsonBody.put("foto_bpjs_kes", "data:image/jpeg;base64,$imageBase64BpjsKes")
        }
        if (!imageBase64KK.isNullOrEmpty()){
            jsonBody.put("foto_kk", "data:image/jpeg;base64,$imageBase64KK")
        }

        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        repository.editProfilePetani(token, body).observeForever {
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

    fun getReligion(){
        bankRepository.getReligion().observeForever{
            religonResult.postValue(it)
        }
    }

    fun getEducation(){
        bankRepository.getEducation().observeForever{
            educationResult.postValue(it)
        }
    }

    fun getProfession(){
        bankRepository.getProfession().observeForever{
            professionResult.postValue(it)
        }
    }

    fun getFieldWork(){
        bankRepository.getFieldWork().observeForever{
            fieldWorkResult.postValue(it)
        }
    }

    fun getWorkExperience(){
        bankRepository.getWorkExperience().observeForever{
            workExperienceResult.postValue(it)
        }
    }

    fun getProvinsiDomisili(){
        addressRepository.getProvinsi().observeForever{
            provinsiDomisiliResult.postValue(it)
        }
    }

    fun getkabupatenDomisili(provinsiId: Int){
        addressRepository.getKabupaten(provinsiId).observeForever{
            kabupatenDomisiliResult.postValue(it)
        }
    }

    fun getKecamatanDomisili(kabupatenId: Int){
        addressRepository.getKecamatan(kabupatenId).observeForever{
            kecamatanDomisiliResult.postValue(it)
        }
    }

    fun getKelurahanDomisili(kecamatanId: Int){
        addressRepository.getKelurahan(kecamatanId).observeForever{
            kelurahanDomisiliResult.postValue(it)
        }
    }

    fun checkBorrower(mobile: String) {
        checkBorrowerRepository.checkBorrower(mobile).observeForever {
            checkBorrowerResult.postValue(it)
        }
    }

    fun fetchOcrKtp(token: String, imageBase64KTP: String)  {
        val jsonBody = JSONObject()
        if (!imageBase64KTP.isNullOrEmpty()){
            jsonBody.put("image", "data:image/jpeg;base64,$imageBase64KTP")
        }
        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        ocrKtpRepository.ocrKtp(token, body).observeForever {
            ocrKtpResult.postValue(it)
        }
    }

}