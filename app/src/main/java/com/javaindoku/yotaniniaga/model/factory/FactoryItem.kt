package com.javaindoku.yotaniniaga.model.factory


import com.google.gson.annotations.SerializedName

data class FactoryItem(
    @SerializedName("akte")
    val akte: String,
    @SerializedName("akun_bank")
    val akunBank: String,
    @SerializedName("alamat")
    val alamat: String,
    @SerializedName("bank_id")
    val bankId: String,
    @SerializedName("harga")
    val harga: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("jarak_pabrik")
    val jarakPabrik: Double,
    @SerializedName("jumlah_karyawan")
    val jumlahKaryawan: String,
    @SerializedName("kabupaten_kota_id")
    val kabupatenKotaId: Int,
    @SerializedName("kapasitas")
    val kapasitas: String,
    @SerializedName("kecamatan_id")
    val kecamatanId: String,
    @SerializedName("kelurahan_id")
    val kelurahanId: String,
    @SerializedName("kode_pos")
    val kodePos: String,
    @SerializedName("kuota")
    val kuota: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("nama_pabrik")
    val namaPabrik: String,
    @SerializedName("no_izin_usaha")
    val noIzinUsaha: String,
    @SerializedName("no_rekening")
    val noRekening: String,
    @SerializedName("npwp")
    val npwp: String,
    @SerializedName("parent_pabrik_id")
    val parentPabrikId: String,
    @SerializedName("provinsi_id")
    val provinsiId: String,
    @SerializedName("row_status")
    val rowStatus: String,
    @SerializedName("siup")
    val siup: String,
    @SerializedName("website")
    val website: String
)