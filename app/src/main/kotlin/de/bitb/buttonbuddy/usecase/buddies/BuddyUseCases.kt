package de.bitb.buttonbuddy.usecase.buddies

data class BuddyUseCases(
    val login: LoginUC,
    val scanBuddy: ScanBuddyUC,
    val loadBuddies: LoadBuddiesUC,
    val sendMessage: SendMessageUC,
)