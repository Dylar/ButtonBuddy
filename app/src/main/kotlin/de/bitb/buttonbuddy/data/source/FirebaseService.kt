package de.bitb.buttonbuddy.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.core.misc.Resource
import kotlinx.coroutines.tasks.await

class FirestoreService(
    private val firestore: FirebaseFirestore,
    private val fireAuth: FirebaseAuth
) : UserRemoteDao, BuddyRemoteDao, MessageRemoteDao, SettingsRemoteDao {

    private val buddyCollection
        get() = firestore.collection("Buddies")

    override suspend fun getUser(email: String): Resource<User?> {
        return try {
            if (!isUserLoggedIn()) {
                throw Exception("User not logged in")
            }
            val snap = buddyCollection
                .whereEqualTo("email", email)
                .get().await()
            Resource.Success(snap.toObjects(User::class.java).firstOrNull())
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val user = fireAuth.currentUser
        return user != null
    }

    override suspend fun registerUser(email: String, pw: String): Resource<Unit> =
        try {
            val authResult = fireAuth.createUserWithEmailAndPassword(email, pw).await()
            if (authResult.user != null) Resource.Success(Unit) else Resource.Error("Not registered")
        } catch (e: Exception) {
            Resource.Error(e)
        }

    override suspend fun loginUser(email: String, pw: String): Resource<Boolean> {
        return try {
            val authResult = fireAuth.signInWithEmailAndPassword(email, pw).await()
            Resource.Success(authResult.user != null)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun saveUser(user: User): Resource<Unit> {
        return try {
            val doc = buddyCollection
                .whereEqualTo("uuid", user.uuid)
                .get().await()
                .documents.firstOrNull()?.reference

            doc?.update(user.toMap()) ?: buddyCollection.add(user)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun saveMessage(msg: Message): Resource<Unit> {
        return try {
            val msgCol = buddyCollection
                .whereEqualTo("uuid", msg.fromUuid)
                .get().await()
                .documents.firstOrNull()
                ?.reference?.collection("messages")
                ?: return Resource.Error("No message collection found")

            msgCol.add(msg)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun loadBuddies(
        userUuid: String,
        buddyIds: List<String>
    ): Resource<List<Buddy>> {
        return try {
            val buddies = buddyCollection
                .whereIn("uuid", buddyIds)
                .get().await().documents.map { snap ->
                    Buddy(userUuid, snap.data?.mapValues { it.value as Any } ?: mapOf())
                }
            Resource.Success(buddies)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun loadCooldowns(
        userUuid: String,
    ): Resource<Map<String, Long>> {
        return try {
            val docs = buddyCollection
                .whereEqualTo("uuid", userUuid)
                .get().await().documents.firstOrNull()

            val cooldowns = docs?.data?.get("cooldowns") as? Map<String, Long> ?: mapOf()
            Resource.Success(cooldowns)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun updateCooldown(
        userUuid: String,
        buddyUuid: String,
        cooldown: Long,
    ): Resource<Unit> {
        return try {
            val doc = buddyCollection
                .whereEqualTo("uuid", buddyUuid)
                .get().await()
                .documents.firstOrNull()?.reference
            val data = mapOf(
                // only mutable values
                "cooldowns.$userUuid" to cooldown
            )
            doc?.update(data) ?: Resource.Error<Unit>("no Buddy found")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}