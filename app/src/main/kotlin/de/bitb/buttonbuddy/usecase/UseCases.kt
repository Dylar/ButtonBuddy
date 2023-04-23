package de.bitb.buttonbuddy.usecase

import de.bitb.buttonbuddy.usecase.buddies.LoadBuddiesUC
import de.bitb.buttonbuddy.usecase.buddies.ScanBuddyUC
import de.bitb.buttonbuddy.usecase.user.LoginUC
import de.bitb.buttonbuddy.usecase.user.RegisterUC
import de.bitb.buttonbuddy.usecase.user.UpdateTokenUC
import de.bitb.buttonbuddy.usecase.message.ReceivingMessageUC
import de.bitb.buttonbuddy.usecase.message.SendMessageUC
import de.bitb.buttonbuddy.usecase.buddies.SetCooldownUC

data class UserUseCases(
    val loginUC: LoginUC,
    val registerUC: RegisterUC,
)

data class BuddyUseCases(
    val scanBuddyUC: ScanBuddyUC,
    val loadBuddiesUC: LoadBuddiesUC,
    val setCooldownUC: SetCooldownUC,
)

data class MessageUseCases(
    val updateTokenUC: UpdateTokenUC,
    val sendMessageUC: SendMessageUC,
    val receivingMessageUC: ReceivingMessageUC,
)