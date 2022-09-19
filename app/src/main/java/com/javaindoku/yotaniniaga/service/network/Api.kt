package com.javaindoku.yotaniniaga.service.network

import com.javaindoku.yotaniniaga.model.checkBorrower.CheckBorrower
import com.javaindoku.yotaniniaga.model.checkGarden.CheckGarden
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryReservation
import com.javaindoku.yotaniniaga.model.factory.Factory
import com.javaindoku.yotaniniaga.model.gardenPetani.*
import com.javaindoku.yotaniniaga.model.invoice.Invoice
import com.javaindoku.yotaniniaga.model.invoice.InvoiceWrapper
import com.javaindoku.yotaniniaga.model.invoiceDetail.InvoiceDetail
import com.javaindoku.yotaniniaga.model.login.Login
import com.javaindoku.yotaniniaga.model.news.News
import com.javaindoku.yotaniniaga.model.notification.Notification
import com.javaindoku.yotaniniaga.model.ocrKtp.OcrKtp
import com.javaindoku.yotaniniaga.model.otp.SendOtp
import com.javaindoku.yotaniniaga.model.profile.agama.Agama
import com.javaindoku.yotaniniaga.model.profile.badanusaha.BadanUsaha
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
import com.javaindoku.yotaniniaga.model.responseapi.GeneralResponseApi
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.model.sendstocksupplyfarmer.AddReservasi
import okhttp3.RequestBody
import retrofit2.http.*
import java.util.HashMap

interface Api {
    @FormUrlEncoded
    @POST("niaga/auth/login")
    suspend fun loginWorker(@Field("username") phoneNo: String,
                            @Field("password") password: String,
                            @Field("fcm_token") fcmToken: String): Login

    @FormUrlEncoded
    @POST("niaga/profile/getProfile")
    suspend fun profilePetaniDetail(@HeaderMap auth: Map<String, String>,
                                          @Field("user_id") userId: String): ProfilePetaniDetail

    @POST("niaga/auth/register")
    suspend fun register(@Body body: RequestBody): ResponseApi

    /*@POST("niaga/wilayah/getProvinsi")
    suspend fun getProvinsi(): Provinsi*/

    @POST("niaga/wilayah/provinsi")
    suspend fun getProvinsi(): Provinsi

    /*@FormUrlEncoded
    @POST("niaga/wilayah/getKabupatenKota")
    suspend fun getKabupaten(@Field("provinsi_id") id: Int): Kabupaten*/

    @FormUrlEncoded
    @POST("niaga/wilayah/kabupatenKota")
    suspend fun getKabupaten(@Field("provinsi_id") id: Int): Kabupaten

    /*@FormUrlEncoded
    @POST("niaga/wilayah/getKecamatan")
    suspend fun getKecamatan(@Field("kabupaten_kota_id") id: Int): Kecamatan*/

    @FormUrlEncoded
    @POST("niaga/wilayah/kecamatan")
    suspend fun getKecamatan(@Field("kabupaten_kota_id") id: Int): Kecamatan

    /*@FormUrlEncoded
    @POST("niaga/wilayah/getKelurahanDesa")
    suspend fun getKelurahan(@Field("kecamatan_id") id: Int): Kelurahan*/

    @FormUrlEncoded
    @POST("niaga/wilayah/kelurahanDesa")
    suspend fun getKelurahan(@Field("kecamatan_id") id: Int): Kelurahan

    @GET("niaga/listBank")
    suspend fun getBank(): Bank

    /*@POST("niaga/profile/updatePetani")
    suspend fun editProfilePetani(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): ResponseApi*/

    //  Update profile petani with register borrower
    @POST("niaga/profile/petaniUpdate")
    suspend fun editProfilePetani(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): EditProfile

    @POST("niaga/pabrik/getPabrik")
    suspend fun getFactories(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): Factory

    @POST("niaga/kebun/getKebun")
    suspend fun getGardenPetani(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): GardenPetani

    @FormUrlEncoded
    @POST("niaga/auth/registerCheck")
    suspend fun getRegisteredData(@Field("mobile") mobile: String): ResponseApi

    @POST("niaga/reservasi/add")
    suspend fun sendStockSupplyFarmer(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): AddReservasi

    @FormUrlEncoded
    @POST("niaga/news")
    suspend fun getNews(@Field("page") page: String): News

    @GET("niaga/news/{id}")
    suspend fun getNewsDetail(@Path("id") id: String): News

    @POST("niaga/reservasi/getAll")
    suspend fun getDeliveryReservatioon(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): DeliveryReservation

    @POST("niaga/reservasi/edit")
    suspend fun rescheduleReservation(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): ResponseApi

    @POST("niaga/kebun/getKebun")
    suspend fun getGarden(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): GeneralResponseApi<List<GardenDataSchema>>

    @POST("niaga/kebun/findOneKebun")
    suspend fun getGardenById(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): GeneralResponseApi<List<GardenDataSchema>>

    @POST("niaga/kebun/addKebun")
    suspend fun addGarden(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): ResponseApi

    @POST("niaga/kebun/updateKebun")
    suspend fun updateGarden(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): ResponseApi

    @GET("niaga/listStatusLahan")
    suspend fun getLandStatus(): GeneralResponseApi<List<LandStatus>>

    @GET("niaga/listJenisBibit")
    suspend fun getCropType(): GeneralResponseApi<List<CropType>>

    @GET("niaga/listSertifikasi")
    suspend fun getCertificationType(): GeneralResponseApi<List<CertificateType>>

    @GET("niaga/listJenisDokumen")
    suspend fun getDocumentType(): GeneralResponseApi<List<DocumentType>>

    /*@POST("niaga/invoice/invoiceAr")
    suspend fun getInvoices(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): InvoiceWrapper<List<Invoice>>*/

    @POST("niaga/invoice/invoiceAp")
    suspend fun getInvoices(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): InvoiceWrapper<List<Invoice>>

    @POST("niaga/kebun/getKebunSertifikat")
    suspend fun getGardenCertificates(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): GeneralResponseApi<List<DocumentGarden>>

    @POST("niaga/reservasi/add/confirm")
    suspend fun confirmStockSupplyFarmer(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): AddReservasi

    @POST("niaga/kebun/deleteKebun")
    suspend fun deleteGarden(@Body body: RequestBody, @HeaderMap authorization: HashMap<String, String>): ResponseApi

    @POST("niaga/notifkasi/getNotifikasi")
    suspend fun getNotification(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): Notification

    @POST("niaga/notifkasi/readNotifikasi")
    suspend fun readNotification(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): Notification

    @POST("niaga/notifkasi/postNotifikasi")
    suspend fun postNotification(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): Notification

    @POST("niaga/koperasi/petani")
    suspend fun getListPetaniByKoperasiId(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): GeneralResponseApi<List<Data>>

    @POST("niaga/koperasi/checkPetani")
    suspend fun checkIsFarmerBound(@Body requestBody: RequestBody, @HeaderMap authorization: HashMap<String, String>): GeneralResponseApi<Data>

    @POST("niaga/koperasi/addPetani")
    suspend fun addFarmer(@Body requestBody: RequestBody, @HeaderMap authorization: HashMap<String, String>): GeneralResponseApi<List<Any>>

    @POST("niaga/koperasi/editPetani")
    suspend fun editFarmer(@Body requestBody: RequestBody, @HeaderMap authorization: HashMap<String, String>): GeneralResponseApi<List<Any>>

    @POST("niaga/koperasi/deletePetani")
    suspend fun removeFarmer(@Body body: RequestBody, @HeaderMap authorization: HashMap<String, String>): GeneralResponseApi<List<Any>>

    @POST("niaga/koperasi/petaniDetail")
    suspend fun getFarmerDetail(@Body body: RequestBody, @HeaderMap authorization: HashMap<String, String>): GeneralResponseApi<Data>

    @POST("niaga/koperasi/kebun")
    suspend fun getKoperasiFarms(@Body body: RequestBody, @HeaderMap authorization: HashMap<String, String>): GardenPetani

    @POST("niaga/profile/updateKoperasi")
    suspend fun editProfileKoperasi(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): ResponseApi

    @GET("niaga/listBadanUsaha")
    suspend fun getBadanUsaha(): BadanUsaha

    @FormUrlEncoded
    @POST("sendOTP")
    suspend fun getOtpPin(@Field("phonenumber") phoneNumber: String): SendOtp

    @FormUrlEncoded
    @POST("verifyRequestOTP")
    suspend fun verifyPin(@Field("phonenumber") phonenumber: String,
                          @Field("request_id") request_id: String,
                          @Field("code") code: String): ResponseApi

    @POST("niaga/kebun/checkKebunKoperasi")
    suspend fun checkGardenKoperasi(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): CheckGarden

    @POST("niaga/kebun/checkKebunPetani")
    suspend fun checkGardenFarmer(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): CheckGarden

    @GET("niaga/listAgama")
    suspend fun getReligion(): Agama

    @GET("niaga/listPendidikan")
    suspend fun getEducation(): Pendidikan

    @GET("niaga/listProfesi")
    suspend fun getProfession(): Profesi

    @GET("niaga/listBidangPekerjaan")
    suspend fun getFieldWork(): BidangPekerjaan

    @GET("niaga/listPeriodePengalaman")
    suspend fun getWOrkExperience(): PengalamanKerja

    @FormUrlEncoded
    @POST("niaga/checkBorrower")
    suspend fun checkBorrower(@Field("mobile") mobile: String): CheckBorrower

    @FormUrlEncoded
    @POST("niaga/invoice/invoiceAp/getOne")
    suspend fun invoiceDetail(@Field("user_id") userId: String,
                              @Field("no_invoice") noInvoice: String,
                              @HeaderMap auth: Map<String, String>): InvoiceDetail

    @POST("niaga/kebun/getSertifikatList")
    suspend fun getCertificateList(@Body body: RequestBody, @HeaderMap auth: Map<String, String>): GeneralResponseApi<List<CertificateGarden>>

    // OCR KTP hit API OCR
    @POST("niaga/classifyDoc")
    suspend fun ocrKtp(@Body body: RequestBody): OcrKtp

}
