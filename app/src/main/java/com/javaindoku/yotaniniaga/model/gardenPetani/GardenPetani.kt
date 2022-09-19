package com.javaindoku.yotaniniaga.model.gardenPetani


import com.google.gson.annotations.SerializedName

data class GardenPetani(
    @SerializedName("data")
    var `data`: List<GardenDataSchema>,
    @SerializedName("isSuccess")
    var isSuccess: Boolean,
    @SerializedName("message")
    var message: String
)