package de.bitb.buttonbuddy.data.source

import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.core.misc.Resource
import kotlinx.coroutines.tasks.await

class FirestoreService(
    private val firestore: FirebaseFirestore
) : UserRemoteDao, BuddyRemoteDao, MessageRemoteDao {

    private val buddyCollection
        get() = firestore.collection("Buddies")

    override suspend fun getUser(firstName: String, lastName: String): Resource<User?> {
        return try {
            val snap = buddyCollection
                .whereEqualTo("firstName", firstName) //TODO not by name
                .whereEqualTo("lastName", lastName)
                .get().await()
            Resource.Success(snap.toObjects(User::class.java).firstOrNull())
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

    override suspend fun loadBuddies(buddyIds: List<String>): Resource<List<Buddy>> {
        return try {
            val buddies = buddyCollection
                .whereIn("uuid", buddyIds)
                .get().await()
                .toObjects(Buddy::class.java)
            Resource.Success(buddies)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

}