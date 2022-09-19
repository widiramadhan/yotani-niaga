package com.javaindoku.yotaniniaga.service.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.javaindoku.yotaniniaga.utilities.PrefHelper
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import dagger.android.AndroidInjection
import javax.inject.Inject

/*
*
*
*@Author
*@Version
*/
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var prefHelper: PrefHelper

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        AndroidInjection.inject(this)
        prefHelper.setStringToShared(SharedPrefKeys.FIREBASE_TOKEN, newToken)
    }
}