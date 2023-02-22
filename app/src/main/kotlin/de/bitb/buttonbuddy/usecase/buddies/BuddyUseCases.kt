package de.bitb.buttonbuddy.usecase.buddies

data class BuddyUseCases(
    val scanBuddy: ScanBuddyUC,
    val loadBuddies: LoadBuddiesUC,
    val sendMessage: SendMessageUC,
)