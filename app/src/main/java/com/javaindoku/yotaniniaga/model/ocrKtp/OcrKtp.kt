package com.javaindoku.yotaniniaga.model.ocrKtp


import com.google.gson.annotations.SerializedName

data class OcrKtp(
    @SerializedName("data")
    var `data`: OcrKtpData? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("person_photo")
    var personPhoto: String? = null,
    @SerializedName("segmented")
    var segmented: String? = null,
    @SerializedName("signature_photo")
    var signaturePhoto: String? = null,
    @SerializedName("isSuccess")
    var success: Boolean? = null,
    @SerializedName("type")
    var type: String? = null
)