package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.gardenPetani.CropToSend
import com.javaindoku.yotaniniaga.model.profile.Farmer
import javax.inject.Inject

class SendStockSupplyViewModel @Inject constructor() : BaseViewModel() {

    val listPetani = MutableLiveData<List<Farmer>>()
    val listKebun = MutableLiveData<List<CropToSend>>()

    fun getListPetaniByKoperasi(idKoperasi: String) {
        val list = ArrayList<Farmer>()
        list.add(Farmer("1", "Rodhi Omdo", "08999395526"))
        list.add(Farmer("2", "Grant Walter", "08999395525"))
        list.add(Farmer("3", "Lucas Cavalier", "08999395523"))
        listPetani.value = list
    }

    fun getListKebunByPetani(idPetani: String) {
        val list = ArrayList<CropToSend>()
        if (idPetani == "1") {
            list.add(
                CropToSend("1", "Pohon Jati", "Tanggerang", "7000", "Kab. Tanggerang",
                "Bojong Nangka", "Kelapa Dua", "1", "Rodhi Omdo"))
            list.add(CropToSend("2", "Pohon Durian", "Tanggerang", "2000", "Kab. Tanggerang",
                "Bojong Nangka", "Kelapa Dua", "1", "Rodhi Omdo"))
        } else if (idPetani == "2") {
            list.add(
                CropToSend("3", "Pohon Apel", "Jakarta", "7000", "Jakarta Barat",
                    "Grogol Petamburan", "Tanjung Duren", "2", "Grant Walter"))
            list.add (CropToSend("4", "Pohon Durian", "Jakarta", "7000", "Jakarta Barat",
                "Grogol Petamburan", "Tanjung Duren", "2", "Grant Walter"))
        } else if (idPetani == "3") {
            list.add(
                CropToSend("5", "Pohon Jeruk", "Jakarta", "7000", "Jakarta Barat",
                    "Grogol Petamburan", "Tanjung Duren", "3", "Lucas Cavalier"))
        }
        listKebun.value = list
    }
}
