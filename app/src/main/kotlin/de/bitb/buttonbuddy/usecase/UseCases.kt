package de.bitb.buttonbuddy.usecase

import de.bitb.buttonbuddy.usecase.buddies.LoadBuddiesUC
import de.bitb.buttonbuddy.usecase.buddies.ScanBuddyUC
import de.bitb.buttonbuddy.usecase.info.LoginUC
import de.bitb.buttonbuddy.usecase.info.UpdateTokenUC
import de.bitb.buttonbuddy.usecase.message.ReceivingMessageUC
import de.bitb.buttonbuddy.usecase.message.SendMessageUC

data class InfoUseCases(
    val loginUC: LoginUC,
)

data class BuddyUseCases(
    val scanBuddyUC: ScanBuddyUC,
    val loadBuddiesUC: LoadBuddiesUC,
)

data class MessageUseCases(
    val updateTokenUC: UpdateTokenUC,
    val sendMessageUC: SendMessageUC,
    val receivingMessageUC: ReceivingMessageUC,
)