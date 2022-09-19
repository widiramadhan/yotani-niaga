package com.javaindoku.yotaniniaga.model.gardenPetani


import com.google.gson.annotations.SerializedName

data class GardenDataSchema(
    @SerializedName("alamat")
    var alamat: String? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("jenis_bibit_id")
    var jenisBibitId: String? = null,
    @SerializedName("kabupaten_kota_id")
    var kabupatenKotaId: String? = null,
    @SerializedName("kecamatan_id")
    var kecamatanId: String? = null,
    @SerializedName("kelurahan_id")
    var kelurahanId: String? = null,
    @SerializedName("kode_pos")
    var kodePos: String? = null,
    @SerializedName("latitude")
    var latitude: String? = null,
    @SerializedName("longitude")
    var longitude: String? = null,
    @SerializedName("luas_kebun")
    var luasKebun: String? = null,
    @SerializedName("petani_id")
    var petaniId: String? = null,
    @SerializedName("potensi_produksi")
    var potensiProduksi: String? = null,
    @SerializedName("provinsi_id")
    var provinsiId: String? = null,
    @SerializedName("row_status")
    var rowStatus: String? = null,
    @SerializedName("rt")
    var rt: String? = null,
    @SerializedName("rw")
    var rw: String? = null,
    @SerializedName("sertifikasi_id")
    var sertifikasiId: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("status_lahan_id")
    var statusLahanId: String? = null,
    @SerializedName("tahun_tanam_id")
    var tahunTanamId: String? = null,
    @SerializedName("sertifikasi_no")
    var sertifikasiNumber: String? = null,
    @SerializedName("sertifikasi_dari")
    var sertifikasiDari: String? = null,
    @SerializedName("sertifikasi_sampai")
    var sertifikasiSampai: String? = null,
    @SerializedName("jumlah_pohon")
    var jumlahPohon: String? = "",
    @SerializedName("foto")
    var foto: String? = "",
    @SerializedName("sertifikasi_image")
    var fotoSertifikasi: String? = "",
    @Transient
    var fotoBase64: String = "",
    @Transient
    var fotoSertifikatBase64: String = "",
    @SerializedName("kebun_id")
    val kebunId: String? = "",
    @SerializedName("provinsi_name")
    val provinsiName: String? = "",
    @SerializedName("kabupaten_kota_name")
    val kabupatenKotaName: String? = "",
    @SerializedName("kecamatan_name")
    val kecamatanName: String? = "",
    @SerializedName("kelurahan_name")
    val kelurahanName: String? = "",
    @SerializedName("jenis_bibit_name")
    val jenisBibitName: String? = "",
    @SerializedName("status_lahan_name")
    val statusLahanName: String? = "",
    @SerializedName("sertifikasi_name")
    val sertifikasiName: String? = ""
)