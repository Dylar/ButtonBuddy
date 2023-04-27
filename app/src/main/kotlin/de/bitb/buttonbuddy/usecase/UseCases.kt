package de.bitb.buttonbuddy.usecase

import de.bitb.buttonbuddy.usecase.buddies.ScanBuddyUC
import de.bitb.buttonbuddy.usecase.user.LoginUC
import de.bitb.buttonbuddy.usecase.user.RegisterUC
import de.bitb.buttonbuddy.usecase.user.UpdateTokenUC
import de.bitb.buttonbuddy.usecase.message.ReceivingMessageUC
import de.bitb.buttonbuddy.usecase.message.SendMessageUC
import de.bitb.buttonbuddy.usecase.buddies.SetCooldownUC
import de.bitb.buttonbuddy.usecase.user.LoadDataUC

data class UserUseCases(
    val loadDataUC: LoadDataUC,
    val loginUC: LoginUC,
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