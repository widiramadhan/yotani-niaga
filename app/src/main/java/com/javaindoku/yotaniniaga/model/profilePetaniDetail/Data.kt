package com.javaindoku.yotaniniaga.model.profilePetaniDetail


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("akun_bank")
    var akunBank: String? = null,
    @SerializedName("alamat")
    var alamat: String? = null,
    @SerializedName("bank_id")
    var bankId: String? = null,
    @SerializedName("bank_name")
    var bankName: String? = null,
    @SerializedName("created_by")
    var createdBy: String? = null,
    @SerializedName("created_date")
    var createdDate: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("foto")
    var foto: String? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("user_id_petani")
    var userIdPetani: String? = null,
    @SerializedName("petani_id")
    var petaniId: String? = null,
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
    @SerializedName("mobile")
    var mobile: String? = null,
    @SerializedName("nama")
    var nama: String? = null,
    @SerializedName("nama_petani")
    var namaPetani: String? = null,
    @SerializedName("no_kk")
    var noKk: String? = null,
    @SerializedName("no_ktp")
    var noKtp: String? = null,
    @SerializedName("no_rekening")
    var noRekening: String? = null,
    @SerializedName("npwp")
    var npwp: String? = null,
    @SerializedName("provinsi_id")
    var provinsiId: String? = null,
    @SerializedName("row_status")
    var rowStatus: String? = null,
    @SerializedName("system_time")
    var systemTime: String? = null,
    @SerializedName("type_user")
    var typeUser: String? = null,
    @SerializedName("updated_by")
    var updatedBy: String? = null,
    @SerializedName("updated_date")
    var updatedDate: String? = null,
    @SerializedName("user_id")
    var userId: String? = null,
    @SerializedName("rt")
    var rt: String? = null,
    @SerializedName("rw")
    var rw: String? = null,
    @SerializedName("badan_usaha")
    var badanUsaha: String? = null,
    @SerializedName("nama_koperasi")
    var namaKoperasi: String? = null,
    @SerializedName("type_badan_usaha")
    var type_badan_usaha: String? = null,
    @SerializedName("agama_id")
    var agamaId: String? = null,
    @SerializedName("pendidikan_id")
    var pendidikanId: String? = null,
    @SerializedName("pekerjaan_id")
    var pekerjaanId: String? = null,
    @SerializedName("industri_id")
    var industriId: String? = null,
    @SerializedName("periode_pengalaman_id")
    var periodePengalamanId: String? = null,
    @SerializedName("tempat_lahir")
    var tempatLahir: String? = null,
    @SerializedName("tanggal_lahir")
    var tanggalLahir: String? = null,
    @SerializedName("jenis_kelamin")
    var jenisKelamin: String? = null,
    @SerializedName("status_nikah_id")
    var statusNikahId: String? = null,
    @SerializedName("online_work_id")
    var onlineWorkId: String? = null,
    @SerializedName("home_ownership_id")
    var homeOwnershipId: String? = null,
    @SerializedName("gaji")
    var gaji: String? = null,
    @SerializedName("total_aset")
    var totalAset: String? = null,
    @SerializedName("ktp_latitude")
    var ktpLatitude: String? = null,
    @SerializedName("ktp_longitude")
    var ktpLongitude: String? = null,
    @SerializedName("ktp_alamat")
    var ktpAlamat: String? = null,
    @SerializedName("ktp_rt")
    var ktpRT: String? = null,
    @SerializedName("ktp_rw")
    var ktpRW: String? = null,
    @SerializedName("ktp_kode_pos")
    var ktpKodePos: String? = null,
    @SerializedName("ktp_provinsi_id")
    var ktpProvinsiId: String? = null,
    @SerializedName("ktp_kabupaten_kota_id")
    var ktpKabupatenKotaId: String? = null,
    @SerializedName("ktp_kecamatan_id")
    var ktpKecamatanId: String? = null,
    @SerializedName("ktp_kelurahan_id")
    var ktpKelurahanId: String? = null,
    @SerializedName("foto_ktp")
    var fotoKtp: String? = null,
    @SerializedName("ceklis_ktp")
    var ceklisKtp: String? = null,
    @SerializedName("bpjs_tk")
    var bpjsTk: String? = null,
    @SerializedName("bpjs_kes")
    var bpjsKes: String? = null,
    @SerializedName("foto_bpjs_tk")
    var fotoBpjsTk: String? = null,
    @SerializedName("foto_bpjs_kes")
    var fotoBpjsKes: String? = null,
    @SerializedName("provinsi_name")
    var provinsiName: String? = null,
    @SerializedName("kecamatan_name")
    var kecamatanName: String? = null,
    @SerializedName("kabupaten_kota_name")
    var kabupatenKotaName: String? = null,
    @SerializedName("kelurahan_desa_name")
    var kelurahanDesaName: String? = null,
    @SerializedName("foto_kk")
    var fotoKk: String? = null,
    @SerializedName("foto_npwp")
    var fotoNpwp: String? = null,
)