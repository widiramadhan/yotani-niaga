package com.javaindoku.yotaniniaga.di.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.javaindoku.yotaniniaga.repository.editprofilepetani.BankRepository
import com.javaindoku.yotaniniaga.repository.editprofilepetani.EditProfilePetaniRepository
import com.javaindoku.yotaniniaga.repository.editprofilepetani.FarmerAddressRepository
import com.javaindoku.yotaniniaga.repository.login.CheckBorrowerRepository
import com.javaindoku.yotaniniaga.repository.notification.ordernotification.OrderNotificationRepository
import com.javaindoku.yotaniniaga.repository.ocrktp.OcrKtpRepository
import com.javaindoku.yotaniniaga.repository.profilePetaniDetail.ProfilePetaniDetailRepository
import com.javaindoku.yotaniniaga.repository.transactionkoperasi.TransactionKoperasiRepository
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.viewmodel.EditProfilePetaniViewModel
import com.javaindoku.yotaniniaga.viewmodel.OrderNotificationViewModel
import com.javaindoku.yotaniniaga.viewmodel.TransactionKoperasiViewModel
import javax.inject.Inject
import javax.inject.Provider

@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor(private val viewModelsMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) :
    ViewModelProvider.Factory {

    @Inject
    lateinit var api: Api

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionKoperasiViewModel::class.java)) {
            return TransactionKoperasiViewModel(
                repository = TransactionKoperasiRepository(api
                )
            ) as T
        }
        else if (modelClass.isAssignableFrom(OrderNotificationViewModel::class.java)) {
            return OrderNotificationViewModel(
                repository = OrderNotificationRepository(api
                )
            ) as T
        }
        else if (modelClass.isAssignableFrom(EditProfilePetaniViewModel::class.java)) {
            return EditProfilePetaniViewModel(
                repository = EditProfilePetaniRepository(api),
                profileRepository = ProfilePetaniDetailRepository(api),
                bankRepository = BankRepository(api),
                addressRepository = FarmerAddressRepository(api),
                checkBorrowerRepository = CheckBorrowerRepository(api),
                ocrKtpRepository = OcrKtpRepository(api)
            ) as T
        }
        val creator = viewModelsMap[modelClass] ?:
        viewModelsMap.asIterable().firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
        return try {
            creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}