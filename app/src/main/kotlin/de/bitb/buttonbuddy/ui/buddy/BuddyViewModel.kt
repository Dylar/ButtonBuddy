package de.bitb.buttonbuddy.ui.buddy

import android.util.Log
import androidx.lifecycle.LiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class BuddyViewModel @Inject constructor(
    private val buddyRepo: BuddyRepository,
    infoRepo: InfoRepository,
) : BaseViewModel() {

    val info: LiveData<Info> = infoRepo.getLiveInfo()
    lateinit var buddy: LiveData<Buddy>

    fun initBuddyState(uuid: String) {
        Log.e(toString(),"uuid: $uuid" )
        buddy = buddyRepo.getBuddy(uuid)
    }

}

