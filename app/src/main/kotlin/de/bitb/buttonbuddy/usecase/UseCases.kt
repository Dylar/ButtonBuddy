package de.bitb.buttonbuddy.usecase

import de.bitb.buttonbuddy.usecase.buddies.ScanBuddyUC
import de.bitb.buttonbuddy.usecase.buddies.SetCooldownUC
import de.bitb.buttonbuddy.usecase.message.ReceivingMessageUC
import de.bitb.buttonbuddy.usecase.message.SendMessageUC
import de.bitb.buttonbuddy.usecase.user.*

data class UserUseCases(
    val loadDataUC: LoadDataUC,
    val loginUC: LoginUC,
    val logoutUC: LogoutUC,
    val registerUC: RegisterUC,
)

data class BuddyUseCases(
    val scanBuddyUC: ScanBuddyUC,
    val setCooldownUC: SetCooldownUC,
)

data class MessageUseCases(
    val updateTokenUC: UpdateTokenUC,
    val sendMessageUC: SendMessageUC,
    val receivingMessageUC: ReceivingMessageUC,
)