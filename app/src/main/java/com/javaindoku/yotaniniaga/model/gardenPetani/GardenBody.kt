package com.javaindoku.yotaniniaga.model.gardenPetani

import com.google.gson.annotations.SerializedName

data class GardenBody (
    var user_id: String = "",
    @SerializedName("petani_id")
    var petaniId: String = "",
    var orderBy: String = "",
    var sort: String= ""
)