package de.bitb.buttonbuddy.usecase.message

data class MessageUseCases(
    val sendMessageUC: SendMessageUC,
    val receivingMessageUC: ReceivingMessageUC,
)