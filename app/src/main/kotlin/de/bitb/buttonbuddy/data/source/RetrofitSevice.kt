package de.bitb.buttonbuddy.data.source

import de.bitb.buttonbuddy.BuildConfig
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.core.misc.tryIt
import de.bitb.buttonbuddy.data.model.Message
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
                val msgToken = "key=${BuildConfig.MESSAGE_TOKEN}"
                val request = MessageRequest(msg.toMap(), msg.token)
                val call = api.sendMessage(msgToken, request)
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