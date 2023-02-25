package de.bitb.buttonbuddy.usecase.info

import de.bitb.buttonbuddy.usecase.message.ReceivingMessageUC

data class InfoUseCases(
    val login: LoginUC,
    val updateTokenUC: UpdateTokenUC,
    val receivingMessageUC: ReceivingMessageUC
)