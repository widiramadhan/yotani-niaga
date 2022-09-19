package com.javaindoku.yotaniniaga.model.transactionfarmer

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class TransactionFarmer (
//    var id: String = "",
//    var quota: String = "",
//    var quotaUnit: String = "",
//    var price: String = "",
//    var quotaFill: String = "",
//    var quotaFillUnit: String = "",
//    var companyName: String ="",
//    var image: String = ""
    @SerializedName("akte")
    val akte: String,
    @SerializedName("akun_bank")
    val akunBank: String,
    @SerializedName("alamat")
    val alamat: String,
    @SerializedName("bank_id")
    val bankId: String,
    @SerializedName("harga")
    val price: String,
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
    val quota: String,
    @SerializedName("kouta_terisi")
    var quotaFill: String= "",
    @Json(name = "quota_unit")
    val _quotaUnit: String? ="",
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("nama_pabrik")
    val companyName: String,
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
    val website: String,
    @SerializedName("image")
    val image: String = "",
    var quotaFillUnit: String = ""
) : Parcelable {
    val quotaUnit = _quotaUnit ?: "kg"
}