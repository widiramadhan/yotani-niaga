package com.javaindoku.yotaniniaga.view.addGarden.listener

import com.javaindoku.yotaniniaga.model.gardenPetani.CertificateGarden

interface CertificateListChangedListener {
    fun onDataChanged(certificateList: List<CertificateGarden>)
}
