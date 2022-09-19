package com.javaindoku.yotaniniaga.view.addFarmer.listener

import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import java.util.ArrayList

/*
*
*
*@Author
*@Version
*/
interface DataChangedListener {
    fun onDataChanged(petaniData: Data)
    fun onBankListChanged(bankList: ArrayList<com.javaindoku.yotaniniaga.model.profile.bank.Data>)
}