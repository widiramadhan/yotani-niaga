package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.gardenPetani.*
import com.javaindoku.yotaniniaga.model.profile.kabupaten.Kabupaten
import com.javaindoku.yotaniniaga.model.profile.kecamatan.Kecamatan
import com.javaindoku.yotaniniaga.model.profile.kelurahan.Kelurahan
import com.javaindoku.yotaniniaga.model.profile.provinsi.Provinsi
import com.javaindoku.yotaniniaga.model.responseapi.GeneralResponseApi
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.repository.editprofilepetani.FarmerAddressRepository
import com.javaindoku.yotaniniaga.repository.garden.GardenRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class AddGardenViewModel @Inject constructor(
    private val gardenRepository: GardenRepository,
    private val farmerAddressRepository: FarmerAddressRepository,
) : BaseViewModel() {

    var gardenCertificateListResult = MutableLiveData<ApiResult<GeneralResponseApi<List<DocumentGarden>>>>()
    var certificateListResult = MutableLiveData<ApiResult<GeneralResponseApi<List<CertificateGarden>>>>()
    var gardenListResult = MutableLiveData<ApiResult<GeneralResponseApi<List<GardenDataSchema>>>>()
    var gardenResult = MutableLiveData<ApiResult<GeneralResponseApi<List<GardenDataSchema>>>>()
    var addGardenResult = MutableLiveData<ApiResult<ResponseApi>>()
    var provinsiResult : MutableLiveData<ApiResult<Provinsi>> = MutableLiveData<ApiResult<Provinsi>>()
    var kabupatenResult : MutableLiveData<ApiResult<Kabupaten>> = MutableLiveData<ApiResult<Kabupaten>>()
    var kecamatanResult : MutableLiveData<ApiResult<Kecamatan>> = MutableLiveData<ApiResult<Kecamatan>>()
    var kelurahanResult : MutableLiveData<ApiResult<Kelurahan>> = MutableLiveData<ApiResult<Kelurahan>>()
    var certificateTypeResult = MutableLiveData<ApiResult<GeneralResponseApi<List<CertificateType>>>>()
    var documentTypeResult = MutableLiveData<ApiResult<GeneralResponseApi<List<DocumentType>>>>()
    var landStatusResult = MutableLiveData<ApiResult<GeneralResponseApi<List<LandStatus>>>>()
    var cropTypeResult = MutableLiveData<ApiResult<GeneralResponseApi<List<CropType>>>>()

    fun fetchGarden(token: String, userId: String, petaniId: String, orderBy:String, sort: String) {
        val jsonBody = JSONObject()
        jsonBody.put("user_id", userId)
        jsonBody.put("petani_id", petaniId)
        jsonBody.put("orderBy", orderBy)
        jsonBody.put("sort", sort)
        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        gardenRepository.getGarden(token, body).observeForever {
            gardenListResult.postValue(it)
        }
    }

    fun fetchGardenById(token: String, userId: String, kebunId: String) {
        val jsonBody = JSONObject()
        jsonBody.put("user_id", userId)
        jsonBody.put("kebun_id", kebunId)
        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        gardenRepository.getGardenById(token, body).observeForever {
            gardenResult.postValue(it)
        }
    }

    fun addGarden(token: String, userId: String, petaniId: String, garden: GardenDataSchema, documentGardenList: List<DocumentGarden>, certificateGardenList: List<CertificateGarden>) {
        val jsonBody = JSONObject()
        val jsonArrayDocument = JSONArray()
        val jsonArrayCertificate = JSONArray()
        jsonBody.put("user_id", userId)
        jsonBody.put("petani_id", petaniId)
        jsonBody.put("latitude", garden.latitude)
        jsonBody.put("longitude", garden.longitude)
        jsonBody.put("alamat", garden.alamat)
        jsonBody.put("rt", garden.rt)
        jsonBody.put("rw", garden.rw)
        jsonBody.put("provinsi_id", garden.provinsiId)
        jsonBody.put("kabupaten_kota_id", garden.kabupatenKotaId)
        jsonBody.put("kecamatan_id", garden.kecamatanId)
        jsonBody.put("kelurahan_id", garden.kelurahanId)
        jsonBody.put("kode_pos", garden.kodePos)
        jsonBody.put("luas_kebun", garden.luasKebun)
        jsonBody.put("potensi_produksi", garden.potensiProduksi)
        jsonBody.put("tahun_tanam_id", garden.tahunTanamId)
        jsonBody.put("jenis_bibit_id", garden.jenisBibitId)
        jsonBody.put("jumlah_pohon", garden.jumlahPohon)
        jsonBody.put("status_lahan_id", garden.statusLahanId)
        /*jsonBody.put("sertifikasi_id", garden.sertifikasiId)
        jsonBody.put("sertifikasi_no", garden.sertifikasiNumber)
        jsonBody.put("sertifikasi_dari", garden.sertifikasiDari)
        jsonBody.put("sertifikasi_sampai", garden.sertifikasiSampai)
        jsonBody.put("sertifikasi_image", garden.fotoSertifikatBase64)*/
        for(document in documentGardenList) {
            var documentGarden = JSONObject()
            documentGarden.put("dokumen_id", document.dokumen_id)
            documentGarden.put("nomor", document.nomor)
            documentGarden.put("foto", document.foto)
            jsonArrayDocument.put(documentGarden)
        }
        jsonBody.put("list_dokumen", jsonArrayDocument)
        for(certificate in certificateGardenList) {
            var certificateGarden = JSONObject()
            certificateGarden.put("sertifikasi_id", certificate.sertifikasi_id)
            certificateGarden.put("sertifikasi_no", certificate.sertifikasi_no)
            certificateGarden.put("sertifikasi_dari", certificate.sertifikasi_dari)
            certificateGarden.put("sertifikasi_sampai", certificate.sertifikasi_sampai)
            certificateGarden.put("sertifikasi_image", certificate.sertifikasi_image)
            jsonArrayCertificate.put(certificateGarden)
        }
        jsonBody.put("list_sertifikasi", jsonArrayCertificate)
        jsonBody.put("foto", garden.fotoBase64)

        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        gardenRepository.addGarden(token, body).observeForever {
            addGardenResult.postValue(it)
        }
    }

    fun updateGarden(token: String, userId: String, petaniId: String, garden: GardenDataSchema, documentGardenList: List<DocumentGarden>, certificateGardenList: List<CertificateGarden>) {
        val jsonBody = JSONObject()
        val jsonArrayDocument = JSONArray()
        val jsonArrayCertificate = JSONArray()
        jsonBody.put("kebun_id", garden.id)
        jsonBody.put("user_id", userId)
        jsonBody.put("petani_id", petaniId)
        jsonBody.put("latitude", garden.latitude)
        jsonBody.put("longitude", garden.longitude)
        jsonBody.put("alamat", garden.alamat)
        jsonBody.put("rt", garden.rt)
        jsonBody.put("rw", garden.rw)
        jsonBody.put("provinsi_id", garden.provinsiId)
        jsonBody.put("kabupaten_kota_id", garden.kabupatenKotaId)
        jsonBody.put("kecamatan_id", garden.kecamatanId)
        jsonBody.put("kelurahan_id", garden.kelurahanId)
        jsonBody.put("kode_pos", garden.kodePos)
        jsonBody.put("luas_kebun", garden.luasKebun)
        jsonBody.put("potensi_produksi", garden.potensiProduksi)
        jsonBody.put("tahun_tanam_id", garden.tahunTanamId)
        jsonBody.put("jenis_bibit_id", garden.jenisBibitId)
        jsonBody.put("status_lahan_id", garden.statusLahanId)
        jsonBody.put("jumlah_pohon", garden.jumlahPohon)
        /*jsonBody.put("sertifikasi_id", garden.sertifikasiId)
        jsonBody.put("sertifikasi_no", garden.sertifikasiNumber)
        jsonBody.put("sertifikasi_dari", garden.sertifikasiDari)
        jsonBody.put("sertifikasi_sampai", garden.sertifikasiSampai)
        jsonBody.put("sertifikasi_image", garden.fotoSertifikatBase64)*/
        for(document in documentGardenList) {
            var documentGarden = JSONObject()
            documentGarden.put("dokumen_id", document.dokumen_id)
            documentGarden.put("nomor", document.nomor)
            documentGarden.put("foto", document.foto)
            jsonArrayDocument.put(documentGarden)
        }
        jsonBody.put("list_dokumen", jsonArrayDocument)
        for(certificate in certificateGardenList) {
            var certificateGarden = JSONObject()
            certificateGarden.put("sertifikasi_id", certificate.sertifikasi_id)
            certificateGarden.put("sertifikasi_no", certificate.sertifikasi_no)
            certificateGarden.put("sertifikasi_dari", certificate.sertifikasi_dari)
            certificateGarden.put("sertifikasi_sampai", certificate.sertifikasi_sampai)
            certificateGarden.put("sertifikasi_image", certificate.sertifikasi_image)
            jsonArrayCertificate.put(certificateGarden)
        }
        jsonBody.put("list_sertifikasi", jsonArrayCertificate)

        if (!garden.fotoBase64.isNullOrEmpty()) {
            jsonBody.put("foto", garden.fotoBase64)
        } else {
            jsonBody.put("foto", "")
        }

        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        gardenRepository.updateGarden(token, body).observeForever {
            addGardenResult.postValue(it)
        }
    }

    fun fetchGardenCertificates(token: String, userId: String, kebunId: String, orderBy:String = "nomor", sort: String = "asc") {
        val jsonBody = JSONObject()
        jsonBody.put("user_id", userId)
        jsonBody.put("kebun_id", kebunId)
        jsonBody.put("orderBy", orderBy)
        jsonBody.put("sort", sort)
        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        gardenRepository.getGardenDocuments(token, body).observeForever {
            gardenCertificateListResult.postValue(it)
        }
    }

    fun fetchCertificatesList(token: String, userId: String, kebunId: String, orderBy:String = "nomor", sort: String = "asc") {
        val jsonBody = JSONObject()
        jsonBody.put("user_id", userId)
        jsonBody.put("kebun_id", kebunId)
        jsonBody.put("orderBy", orderBy)
        jsonBody.put("sort", sort)
        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        gardenRepository.getGardenCertificate(token, body).observeForever {
            certificateListResult.postValue(it)
        }
    }

    fun getProvinsi(){
        farmerAddressRepository.getProvinsi().observeForever{
            provinsiResult.postValue(it)
        }
    }

    fun getkabupaten(provinsiId: Int){
        farmerAddressRepository.getKabupaten(provinsiId).observeForever{
            kabupatenResult.postValue(it)
        }
    }

    fun getKecamatan(kabupatenId: Int){
        farmerAddressRepository.getKecamatan(kabupatenId).observeForever{
            kecamatanResult.postValue(it)
        }
    }

    fun getKelurahan(kecamatanId: Int){
        farmerAddressRepository.getKelurahan(kecamatanId).observeForever{
            kelurahanResult.postValue(it)
        }
    }

    fun getCropType(){
        gardenRepository.getCropType().observeForever{
            cropTypeResult.postValue(it)
        }
    }

    fun getCertificationType(){
        gardenRepository.getCertifactionType().observeForever{
            certificateTypeResult.postValue(it)
        }
    }

    fun getDocumentType(){
        gardenRepository.getDocumentType().observeForever{
            documentTypeResult.postValue(it)
        }
    }

    fun getLandStatus(){
        gardenRepository.getLandStatus().observeForever{
            landStatusResult.postValue(it)
        }
    }


}
