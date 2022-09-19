package com.javaindoku.yotaniniaga.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.javaindoku.yotaniniaga.di.factory.ViewModelFactory
import com.javaindoku.yotaniniaga.di.scope.ViewModelKey
import com.javaindoku.yotaniniaga.viewmodel.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(OtpViewModel::class)
    abstract fun bindOtpViewModel(otpViewModel: OtpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ListPriceTbsViewModel::class)
    abstract fun bindListPriceTbsViewModel(listPriceTbsViewModel: ListPriceTbsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrderNotificationViewModel::class)
    abstract fun bindListNotificationViewModel(notificationViewModel: OrderNotificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InvoiceViewModel::class)
    abstract fun bindInvoiceViewModel(invoiceVoiceViewModel: InvoiceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransactionFarmerViewModel::class)
    abstract fun bindTransactionFarmerViewModel(transactionFarmerViewModel: TransactionFarmerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsViewModel::class)
    abstract fun bindNewsViewModel(newsViewModel: NewsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfilePetaniDetailViewModel::class)
    abstract fun bindProfilePetaniDetailViewModel(profilePetaniDetailViewModel: ProfilePetaniDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegisterNextViewModel::class)
    abstract fun bindRegisterNextViewModel(registerNextViewModel: RegisterNextViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditProfilePetaniViewModel::class)
    abstract fun bindEditProfilePetaniViewModel(editProfilePetaniViewModel: EditProfilePetaniViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    abstract fun bindRegisterViewModel(registerViewModel: RegisterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SendStockSupplyFarmerViewModel::class)
    abstract fun bindSendStockSupplyFarmerViewModel(sendStockSupplyFarmerViewModel: SendStockSupplyFarmerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FarmerEditViewModel::class)
    abstract fun bindFarmerEditViewModel(farmerEditViewModel: FarmerEditViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FarmerAddressViewModel::class)
    abstract fun bindFarmerAddressViewModel(farmerAddressViewModel: FarmerAddressViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeKoperasiViewModel::class)
    abstract fun bindHomeKoperasiViewModel(homeKoperasiViewModel: HomeKoperasiViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsDetailViewModel::class)
    abstract fun bindNewsDetailVideoModel(newsDetailViewModel: NewsDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddGardenViewModel::class)
    abstract fun bindAddGardenViewModel(addGardenViewModel: AddGardenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaymentNotificationViewModel::class)
    abstract fun bindPaymentNotificationViewModel(paymentNotificationViewModel: PaymentNotificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsNotificationViewModel::class)
    abstract fun bindNewsNotificationViewModel(newsNotificationViewModel: NewsNotificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditProfileKoperasiViewModel::class)
    abstract fun bindEditProfileKoperasiViewModel(editProfileKoperasiViewModel: EditProfileKoperasiViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SendStockSupplyViewModel::class)
    abstract fun bindSendStockSupplyViewModel(sendStockSupplyViewModel: SendStockSupplyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GardenManagementViewModel::class)
    abstract fun bindGardenManagementViewModel(gardenManagementViewModel: GardenManagementViewModel): ViewModel
}