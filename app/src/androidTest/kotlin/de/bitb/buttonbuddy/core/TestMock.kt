package de.bitb.buttonbuddy.core

import de.bitb.buttonbuddy.buildInfo
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.data.source.RemoteService
import io.mockk.coEvery

fun RemoteService.mockRemoteService(info: Info? = null, buddies: List<Buddy> = emptyList()) {
    var xInfo = info ?: buildInfo()
    coEvery { loadBuddies(any()) }.returns(Resource.Success(buddies))
    coEvery { saveInfo(any()) }.answers {
        Resource.Success(Unit).also { xInfo = args.first() as Info }
    }
    coEvery { getInfo(any(), any()) }.answers { Resource.Success(xInfo) }
    coEvery { saveMessage(any()) }.returns(Resource.Success(Unit))
    coEvery { sendMessage(any()) }.returns(Resource.Success(Unit))
}