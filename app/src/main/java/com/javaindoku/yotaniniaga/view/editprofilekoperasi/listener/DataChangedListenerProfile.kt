package com.javaindoku.yotaniniaga.view.editprofilekoperasi.listener

import com.javaindoku.yotaniniaga.model.profile.badanusaha.BadanUsahaData
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data

interface DataChangedListenerProfile {
    fun onDataChanged(profile: Data)
    fun onBankListChanged(bankList: List<com.javaindoku.yotaniniaga.model.profile.bank.Data>)
    fun onBadanUsahaListChanged(badanUsahaList: List<BadanUsahaData>)
}
