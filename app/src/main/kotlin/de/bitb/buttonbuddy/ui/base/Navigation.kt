package de.bitb.buttonbuddy.ui.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.KEY_BUDDY_UUID

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

fun Fragment.naviToSettings() {
    findNavController(this).navigate(R.id.profile_to_settings)
}

fun Fragment.naviBuddysToSettings() {
    findNavController(this).navigate(R.id.buddies_to_settings)
}

fun Fragment.naviToBuddy(uuid: String? = null) {
    findNavController(this).navigate(R.id.buddies_to_buddy, bundleOf(KEY_BUDDY_UUID to uuid))
}

fun Fragment.naviToProfile() {
    findNavController(this).navigate(R.id.buddies_to_profile)
}

fun Fragment.naviToScan() {
    findNavController(this).navigate(R.id.buddies_to_scan)
}

fun Fragment.naviToRegister() {
    findNavController(this).navigate(R.id.login_to_register)
}