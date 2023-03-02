package de.bitb.buttonbuddy

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import io.mockk.mockk

inline fun <reified T : ViewModel, reified F : Fragment> launchFrag(viewModel: T) {
    val args = Bundle().apply {
//            putString(KEY_BUDDY_UUID, uuid)
    }

    val scenario =
        launchFragmentInContainer<F>(args, factory = mockk())
    scenario.onFragment { fragment ->
        fragment.viewModelStore.clear()
        fragment.viewModelStore.put(T::class.java.name, viewModel)
    }
}