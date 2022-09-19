package com.javaindoku.yotaniniaga.model.profile.bank


import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

data class Data(
    @SerializedName("bank_code")
    var bankCode: String? = null,
    @SerializedName("bank_id")
    var bankId: String? = null,
    @SerializedName("bank_name")
    var bankName: String? = null
) : AdapterDisplayText {
    override fun displayName(): String {
        return bankName.toString()
    }

    override fun selectId(): Any {
        return bankId.toString()
    }
}