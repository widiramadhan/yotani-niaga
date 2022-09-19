package com.javaindoku.yotaniniaga.view.addGarden.listener

import com.javaindoku.yotaniniaga.model.gardenPetani.GardenDataSchema

interface DataChangedListener {
    fun onDataChanged(gardenData: GardenDataSchema)
}
