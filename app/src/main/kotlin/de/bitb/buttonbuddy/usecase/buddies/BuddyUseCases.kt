package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.usecase.message.SendMessageUC

data class BuddyUseCases(
    val scanBuddy: ScanBuddyUC,
    val loadBuddies: LoadBuddiesUC,
    val sendMessage: SendMessageUC,
)