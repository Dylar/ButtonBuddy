package de.bitb.buttonbuddy.core

import de.bitb.buttonbuddy.shared.buildInfo
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService
import io.mockk.coEvery

suspend fun LocalDatabase.mockLocalDatabase(
    messages: List<Message> = emptyList()
) {
    for (message in messages) {
        insert(message)
    }
}

fun RemoteService.mockRemoteService(
    info: Info? = null,
    buddies: List<Buddy> = emptyList(),
    sendMessageError: String? = null,
    saveMessageError: String? = null
) {
    var xInfo = info ?: buildInfo()
    coEvery { loadBuddies(any()) }.returns(Resource.Success(buddies))
    coEvery { saveInfo(any()) }.answers {
        Resource.Success(Unit).also { xInfo = args.first() as Info }
    }
    coEvery { getInfo(any(), any()) }.answers { Resource.Success(xInfo) }
    coEvery { saveMessage(any()) }.returns(
        saveMessageError?.let { Resource.Error(it) } ?: Resource.Success(Unit)
    )
    coEvery { sendMessage(any()) }.returns(
        sendMessageError?.let { Resource.Error(it) } ?: Resource.Success(Unit)
    )

    coEvery { sendMessage(any()) }.returns(
        sendMessageError?.let { Resource.Error(it) } ?: Resource.Success(Unit)
    )
}