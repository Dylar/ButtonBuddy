package de.bitb.buttonbuddy.ui.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import de.bitb.buttonbuddy.R
import android.provider.Settings

const val BACK_ID = -42

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

fun Fragment.naviToBuddy(uuid: String? = null) {
    findNavController(this).navigate(R.id.buddies_to_buddy, bundleOf("uuid" to uuid))
}

fun Fragment.naviToScan(){
    findNavController(this).navigate(R.id.buddies_to_scan)
}