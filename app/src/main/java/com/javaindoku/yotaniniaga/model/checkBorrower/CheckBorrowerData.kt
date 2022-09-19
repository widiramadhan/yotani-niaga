package com.javaindoku.yotaniniaga.model.checkBorrower


import com.google.gson.annotations.SerializedName

data class CheckBorrowerData(
    @SerializedName("borrower_id")
    var borrowerId: String? = null,
    @SerializedName("user_id")
    var userId: String? = null
)