package com.javaindoku.yotaniniaga.di.module

import com.javaindoku.yotaniniaga.view.addFarmer.fragment.FarmerAddressFormFragment
import com.javaindoku.yotaniniaga.view.addFarmer.fragment.FarmerDataFormFragment
import com.javaindoku.yotaniniaga.view.addGarden.fragment.*
import com.javaindoku.yotaniniaga.view.delivery.DeliveryFragment
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.fragment.GardenFragment
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.fragment.KoperasiAddressFragment
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.fragment.KoperasiEditFragment
import com.javaindoku.yotaniniaga.view.editprofilepetani.fragment.FarmerAddressFragment
import com.javaindoku.yotaniniaga.view.editprofilepetani.fragment.FarmerEditFragment
import com.javaindoku.yotaniniaga.view.mainkoperasi.fragment.HomeKoperasiFragment
import com.javaindoku.yotaniniaga.view.mainpetani.HomePetaniFragment
import com.javaindoku.yotaniniaga.view.invoice.InvoiceFragment
import com.javaindoku.yotaniniaga.view.notification.farmer.NewsNotificationFragment
import com.javaindoku.yotaniniaga.view.notification.farmer.OrderNotificationFragment
import com.javaindoku.yotaniniaga.view.notification.farmer.PaymentNotificationFragment
import com.javaindoku.yotaniniaga.view.profile.ProfileFragment
import com.javaindoku.yotaniniaga.view.profilePetani.ProfilePetaniFragment
import com.javaindoku.yotaniniaga.view.report.fragment.PaymentFragment
import com.javaindoku.yotaniniaga.view.sendstocksupply.fragment.SendStockSupplyFragment
import com.javaindoku.yotaniniaga.view.sendstocksupplyfarmer.SendStockSupplyFarmerFragment
import com.javaindoku.yotaniniaga.view.transaction.fragment.TransactionFragment
import com.javaindoku.yotaniniaga.view.transaction.fragment.TransactionSupplyDemandFragment
import com.javaindoku.yotaniniaga.view.transactionfarmer.fragment.TransactionFarmerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule  {
    @ContributesAndroidInjector
    abstract fun provideHomeKoperasiFragment(): HomeKoperasiFragment

    @ContributesAndroidInjector
    abstract fun providePaymentFragment(): PaymentFragment

    @ContributesAndroidInjector
    abstract fun provideProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun provideProfilePetaniFragment(): ProfilePetaniFragment

    @ContributesAndroidInjector
    abstract fun provideInvoiceFragment(): InvoiceFragment

    @ContributesAndroidInjector
    abstract fun provideDeliveryFragment(): DeliveryFragment

    @ContributesAndroidInjector
    abstract fun provideHomePetaniFragment(): HomePetaniFragment

    @ContributesAndroidInjector
    abstract fun provideTransactionFragment(): TransactionFragment

    @ContributesAndroidInjector
    abstract fun provideTransactionSupplyDemandFragment(): TransactionSupplyDemandFragment

    @ContributesAndroidInjector
    abstract fun provideAddressFragment(): KoperasiAddressFragment

    @ContributesAndroidInjector
    abstract fun provideGardenFragment(): GardenFragment

    @ContributesAndroidInjector
    abstract fun provideSendSupplyFragment(): SendStockSupplyFragment

    @ContributesAndroidInjector
    abstract fun provideTransactionFramerFragment(): TransactionFarmerFragment

    @ContributesAndroidInjector
    abstract fun provideSendSupplyFarmerFragment(): SendStockSupplyFarmerFragment

    @ContributesAndroidInjector
    abstract fun provideAddressGardenFragment(): AddressGardenFragment

    @ContributesAndroidInjector
    abstract fun provideGardenAddFragment(): GardenAddFragment

    @ContributesAndroidInjector
    abstract fun provideDocumentLegalityFragment(): DocumentLegalityFragment

    @ContributesAndroidInjector
    abstract fun provideFarmerEditFragment(): FarmerEditFragment

    @ContributesAndroidInjector
    abstract fun provideOrderNotification(): OrderNotificationFragment

    @ContributesAndroidInjector
    abstract fun providePaymentNotification(): PaymentNotificationFragment

    @ContributesAndroidInjector
    abstract fun provideNewsNotification(): NewsNotificationFragment

    @ContributesAndroidInjector
    abstract fun provideFarmerAddressFragment(): FarmerAddressFragment

    @ContributesAndroidInjector
    abstract fun provideCertificateGardenFragment(): CertificateGardenFragment

    @ContributesAndroidInjector
    abstract fun provideKoperasiEditFragment(): KoperasiEditFragment

    @ContributesAndroidInjector
    abstract fun provideAddFarmerDataFormFragment(): FarmerDataFormFragment

    @ContributesAndroidInjector
    abstract fun provideAddFarmerAddressFormFragment(): FarmerAddressFormFragment

    @ContributesAndroidInjector
    abstract fun provideCertificateGardenListFragment(): CertificateGardenListFragment
}