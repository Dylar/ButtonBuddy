package de.bitb.buttonbuddy.ui

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import de.bitb.buttonbuddy.R

fun Fragment.naviToBuddy(token: String? = null) {
    findNavController(this).navigate(R.id.buddies_to_buddy, bundleOf("token" to token))
}