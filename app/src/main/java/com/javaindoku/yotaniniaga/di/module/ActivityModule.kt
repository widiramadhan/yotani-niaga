package com.javaindoku.yotaniniaga.di.module

import com.javaindoku.yotaniniaga.view.mainkoperasi.MainKoperasiActivity
import com.javaindoku.yotaniniaga.di.scope.ActivityScope
import com.javaindoku.yotaniniaga.view.addFarmer.AddFarmerActivity
import com.javaindoku.yotaniniaga.view.addGarden.AddGardenActivity
import com.javaindoku.yotaniniaga.view.changepassword.ChangePasswordActivity
import com.javaindoku.yotaniniaga.view.detailGarden.GardenViewDetailActivity
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.EditProfileKoperasiActivity
import com.javaindoku.yotaniniaga.view.editprofilepetani.EditProfilePetaniActivity
import com.javaindoku.yotaniniaga.view.forgotpassword.ForgotPasswordActivity
import com.javaindoku.yotaniniaga.view.gardenmanagement.GardenManagementActivity
import com.javaindoku.yotaniniaga.view.listgarden.ListGardenActivity
import com.javaindoku.yotaniniaga.view.listpks.ListPksActivity
import com.javaindoku.yotaniniaga.view.listpricetbs.ListPriceTbsActivity
import com.javaindoku.yotaniniaga.view.login.LoginActivity
import com.javaindoku.yotaniniaga.view.news.NewsActivity
import com.javaindoku.yotaniniaga.view.news.NewsDetailActivity
import com.javaindoku.yotaniniaga.view.notification.NotificationActivity
import com.javaindoku.yotaniniaga.view.otp.OtpActivity
import com.javaindoku.yotaniniaga.view.profile.adapter.ProfileDetailActivity
import com.javaindoku.yotaniniaga.view.profilePetani.adapter.ProfilePetaniDetailActivity
import com.javaindoku.yotaniniaga.view.register.RegisterActivity
import com.javaindoku.yotaniniaga.view.register.RegisterNextActivity
import com.javaindoku.yotaniniaga.view.report.ReportActivity
import com.javaindoku.yotaniniaga.view.schedulefertilization.ScheduleFertilizationActivity
import com.javaindoku.yotaniniaga.view.splashscreen.SplashScreenActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideMainActivity(): MainKoperasiActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideLoginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideSplashScreenActivity(): SplashScreenActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideForgotPasswordActivity(): ForgotPasswordActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideNewsActivity(): NewsActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideNewsDetailActivity(): NewsDetailActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideListPksActivity(): ListPksActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideListGardenActivity(): ListGardenActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideReportActivity(): ReportActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideListPriceTbsActivity(): ListPriceTbsActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideScheduleFertilizationActivity(): ScheduleFertilizationActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideRegisterActivity(): RegisterActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideRegisterNextActivity(): RegisterNextActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideNotificationActivity(): NotificationActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideProfileDetailActivity(): ProfileDetailActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideEditProfileActivity(): EditProfileKoperasiActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideAddGardenActivity(): AddGardenActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideProfilePetaniDetailActivity(): ProfilePetaniDetailActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideEditProfilePetaniActivity(): EditProfilePetaniActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideChangePasswordActivity(): ChangePasswordActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideAddFarmerActivity(): AddFarmerActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideGardenManagementActivity(): GardenManagementActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideOtpActivity(): OtpActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideGardenViewDetailActivity(): GardenViewDetailActivity
}