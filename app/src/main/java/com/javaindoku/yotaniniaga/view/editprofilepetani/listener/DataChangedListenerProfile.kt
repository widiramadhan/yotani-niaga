package com.javaindoku.yotaniniaga.view.editprofilepetani.listener

import com.javaindoku.yotaniniaga.model.ocrKtp.OcrKtpData
import com.javaindoku.yotaniniaga.model.profile.bank.Bank
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data

interface DataChangedListenerProfile {
    fun onDataChanged(profile: Data)
    fun onBankListChanged(bankList: List<com.javaindoku.yotaniniaga.model.profile.bank.Data>)
    fun onReligionListChanged(religionList: List<com.javaindoku.yotaniniaga.model.profile.agama.DataAgama>)
    fun onEducationListChanged(educationList: List<com.javaindoku.yotaniniaga.model.profile.pendidikan.DataPendidikan>)
    fun onProfessionListChanged(professinoList: List<com.javaindoku.yotaniniaga.model.profile.profesi.DataProfesi>)
    fun onFieldWorkListChanged(fieldWorkList: List<com.javaindoku.yotaniniaga.model.profile.bidangpekerjaan.DataBidangPekerjaan>)
    fun onWorkExperienceListChanged(workExperienceList: List<com.javaindoku.yotaniniaga.model.profile.pengalamankerja.DataPengalamanKerja>)
    fun onOcrKtpChanged(ocrKtpData: OcrKtpData)
}
