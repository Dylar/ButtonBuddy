package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.misc.Resource
import de.bitb.buttonbuddy.ui.composable.UiText

class LoginUC(
    private val infoRepo: InfoRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(firstName: String, lastName: String) : Resource<Unit> {
        if (firstName.isBlank()) {
            return Resource.Error(UiText.DynamicString("Vorname darf nicht leer sein"))
        }
        if (lastName.isBlank()) {
            return Resource.Error(UiText.DynamicString("Nachname darf nicht leer sein"))
        }
        val oldInfo = infoRepo.getInfo() ?: Info()
        val newInfo = oldInfo.copy(
            //TODO make login anders
            firstName = firstName,
            lastName = lastName,
        )
        infoRepo.saveInfo(newInfo)
        buddyRepo.loadBuddies(newInfo.buddies)
        return Resource.Success(Unit)
    }
}