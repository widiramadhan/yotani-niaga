package com.javaindoku.yotaniniaga.view.editprofilepetani.listener

import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data

interface DataChangedListenerAddress {
    fun onDataChanged(profile: Data)
    fun onProvinsiListChanged(listProvinsi: List<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>)
    fun onKabupatenListChanged(listKabupaten: List<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>)
    fun onKecamatanListChanged(listKecamatan: List<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>)
    fun onKelurahanListChanged(listKelurahan: List<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>)
    fun onProvinsiDomisiliListChanged(listProvinsi: List<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>)
    fun onKabupatenDomisiliListChanged(listKabupaten: List<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>)
    fun onKecamatanDomisiliListChanged(listKecamatan: List<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>)
    fun onKelurahanDomisiliListChanged(listKelurahan: List<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>)
}
