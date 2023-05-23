package de.bitb.buttonbuddy.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.core.misc.tryIt
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.model.User
import kotlinx.coroutines.tasks.await

class FirestoreService(
    private val firestore: FirebaseFirestore,
    private val fireAuth: FirebaseAuth
) : UserRemoteDao, BuddyRemoteDao, MessageRemoteDao, SettingsRemoteDao {

    private val buddyCollection
        get() = firestore.collection("Buddies")

    override suspend fun getUser(email: String): Resource<User?> {
        return tryIt {
            val snap = buddyCollection
                .whereEqualTo("email", email)
                .get().await()
            Resource.Success(snap.toObjects(User::class.java).firstOrNull())
        }
    }

    override suspend fun isUserLoggedIn(): Resource<Boolean> {
        return tryIt {
            val user = fireAuth.currentUser
            Resource.Success(user != null)
        }
    }

    override suspend fun registerUser(email: String, pw: String): Resource<Unit> =
        tryIt {
            val authResult = fireAuth.createUserWithEmailAndPassword(email, pw).await()
            if (authResult.user != null) Resource.Success() else "Not registered".asResourceError()
        }

    override suspend fun loginUser(email: String, pw: String): Resource<Boolean> {
        return tryIt {
            val authResult = fireAuth.signInWithEmailAndPassword(email, pw).await()
            Resource.Success(authResult.user != null)
        }
    }

    override suspend fun saveUser(user: User): Resource<Unit> {
        return tryIt {
            val doc = buddyCollection
                .whereEqualTo("uuid", user.uuid)
                .get().await()
                .documents.firstOrNull()?.reference

            doc?.update(user.toMap()) ?: buddyCollection.add(user)
            Resource.Success()
        }
    }

    override suspend fun loadMessages(uuid: String): Resource<List<Message>> {
        return tryIt {
            val messages = buddyCollection
                .whereEqualTo("uuid", uuid)
                .get().await()
                .documents.firstOrNull()?.reference?.collection("messages")
                ?.get()?.await()?.toObjects(Message::class.java)
            Resource.Success(messages)
        }
    }

    override suspend fun saveMessage(msg: Message): Resource<Unit> {
        return tryIt {
            val msgCol = buddyCollection
                .whereEqualTo("uuid", msg.fromUuid)
                .get().await()
                .documents.firstOrNull()
                ?.reference?.collection("messages")
                ?: return@tryIt "No message collection found".asResourceError()

            msgCol.add(msg)
            Resource.Success()
        }
    }

    override suspend fun loadBuddies(
        userUuid: String,
        buddyIds: List<String>
    ): Resource<List<Buddy>> {
        return tryIt {
            val buddies = buddyCollection
                .whereIn("uuid", buddyIds)
                .get().await().documents.map { snap ->
                    Buddy(userUuid, snap.data?.mapValues { it.value as Any } ?: mapOf())
                }
            Resource.Success(buddies)
        }
    }

    override suspend fun loadCooldowns(
        userUuid: String,
    ): Resource<Map<String, Long>> {
        return tryIt {
            val docs = buddyCollection
                .whereEqualTo("uuid", userUuid)
                .get().await().documents.firstOrNull()

            val cooldowns = docs?.data?.get("cooldowns") as? Map<String, Long> ?: mapOf()
            Resource.Success(cooldowns)
        }
    }

    override suspend fun updateCooldown(
        userUuid: String,
        buddyUuid: String,
        cooldown: Long,
    ): Resource<Unit> {
        return tryIt {
            val doc = buddyCollection
                .whereEqualTo("uuid", buddyUuid)
                .get().await()
                .documents.firstOrNull()?.reference
            val data = mapOf(
                // only mutable values
                "cooldowns.$userUuid" to cooldown
            )
            doc?.update(data) ?: "no Buddy found".asResourceError<Unit>()
            Resource.Success()
        }
    }
}