package de.bitb.buttonbuddy.data.source

import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.core.misc.tryIt
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitApi {

    @Headers("Content-Type: application/json")
    @POST("fcm/send")
    fun sendMessage(
        @Header("Authorization") token: String,
        @Body request: MessageRequest
    ): Call<MessageResponse>

}

class RetrofitService(private val api: RetrofitApi) : MessageService {
    override suspend fun sendMessage(msg: Message): Resource<Unit> {
        return tryIt {
            @Suppress("BlockingMethodInNonBlockingContext")
            withContext(IO) {
                val appKey = // TODO key not in app
                    "key=AAAABWzyIco:APA91bGW62e5atzxL1WIyGEXYd8j1ztelp0kvn2GITJy2HC3_OdLr4ZYf7e6ZhtqrAgM1L6Casb5REDZ6pVGO_eUylcdgzWfyM4ui-g3D9KwEERlDEUr5SVDc3VGQ3FcUDUph2ynWu4C"
                val request = MessageRequest(msg.toMap(), msg.token)
                val call = api.sendMessage(appKey, request)
                val response = call.execute()
                if (response.isSuccessful) {
//                val msgResp = response.body()
                    Resource.Success()
                } else {
                    val error = response.errorBody()?.string() ?: "Sending error"
                    error.asResourceError()
                }
            }
        }
    }
}

data class MessageRequest(
    val data: Map<String, String>,
    val to: String,
    val direct_boot_ok: Boolean = true
)

data class MessageResponse(
    val multicastId: Int,
    val success: Int,
    val failure: Int,
    val results: List<Map<String, String>>
)