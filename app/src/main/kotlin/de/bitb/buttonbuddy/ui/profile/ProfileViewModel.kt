package de.bitb.buttonbuddy.ui.profile

import androidx.lifecycle.LiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    infoRepo: InfoRepository,
) : BaseViewModel(){

    val info: LiveData<Info> = infoRepo.getLiveInfo()

}

