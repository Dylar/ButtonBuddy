package de.bitb.buttonbuddy.ui.buddy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import javax.inject.Inject

@HiltViewModel
class BuddyViewModel @Inject constructor(
    private val buddyRepo: BuddyRepository,
    infoRepo: InfoRepository,
) : ViewModel() {

    private val info: LiveData<Info> = infoRepo.getLiveInfo()
    lateinit var buddy: LiveData<Buddy>

    val isMyself: Boolean = buddy.value?.token == info.value?.token

    fun loadData(token: String) {
        buddy = buddyRepo.getBuddy(token)
    }

}

