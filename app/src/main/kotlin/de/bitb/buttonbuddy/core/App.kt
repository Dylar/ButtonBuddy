package de.bitb.buttonbuddy.core

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BuddyApp : Application(){
    override fun onCreate() {
//        FirebaseApp.initializeApp(this) // TODO remove?
        super.onCreate()
    }
}