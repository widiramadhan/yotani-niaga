package com.javaindoku.yotaniniaga.model.ocrKtp


import com.google.gson.annotations.SerializedName

data class OcrKtpData(
    @SerializedName("Agama")
    var agama: Agama? = null,
    @SerializedName("Alamat")
    var alamat: Alamat? = null,
    @SerializedName("Berlaku Hingga")
    var berlakuHingga: BerlakuHingga? = null,
    @SerializedName("Gol. Darah")
    var golDarah: GolDarah? = null,
    @SerializedName("Jenis Kelamin")
    var jenisKelamin: JenisKelamin? = null,
    @SerializedName("Kecamatan")
    var kecamatan: Kecamatan? = null,
    @SerializedName("Kel/Desa")
    var kelDesa: KelDesa? = null,
    @SerializedName("Kewarganegaraan")
    var kewarganegaraan: Kewarganegaraan? = null,
    @SerializedName("NIK")
    var nIK: NIK? = null,
    @SerializedName("Nama")
    var nama: Nama? = null,
    @SerializedName("Pekerjaan")
    var pekerjaan: Pekerjaan? = null,
    @SerializedName("RT/RW")
    var rTRW: RTRW? = null,
    @SerializedName("Status Perkawinan")
    var statusPerkawinan: StatusPerkawinan? = null,
    @SerializedName("Tempat/Tgl Lahir")
    var tempatTglLahir: TempatTglLahir? = null
)