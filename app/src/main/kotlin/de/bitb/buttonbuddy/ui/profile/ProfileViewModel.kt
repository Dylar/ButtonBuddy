package de.bitb.buttonbuddy.ui.profile

import androidx.lifecycle.LiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    userRepo: UserRepository,
) : BaseViewModel(){

    val user: LiveData<User> = userRepo.getLiveUser()

}

