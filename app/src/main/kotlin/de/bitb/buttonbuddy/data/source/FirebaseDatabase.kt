package de.bitb.buttonbuddy.data.source

import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.misc.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreDatabase @Inject constructor(
    private val firestore: FirebaseFirestore
) : RemoteDatabase {

    private val buddyCollection
        get() = firestore.collection("Buddies")

    override suspend fun getInfo(firstName: String, lastName: String): Resource<Info?> {
        return try {
            val snap = buddyCollection
                .whereEqualTo("firstName", firstName) //TODO not by name
                .whereEqualTo("lastName", lastName)
                .get().await()
            Resource.Success(snap.toObjects(Info::class.java).firstOrNull())
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override suspend fun saveInfo(info: Info): Resource<Unit> {
        return try {
            val doc = buddyCollection
                .whereEqualTo("uuid", info.uuid)
                .get().await()
                .documents.firstOrNull()?.reference

            doc?.update(info.toMap()) ?: buddyCollection.add(info)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override suspend fun loadBuddies(buddyIds: List<String>): Resource<List<Buddy>> {
        return try {
            val snap = buddyCollection
                .whereArrayContains("buddies", buddyIds)
                .get().await()
            Resource.Success(snap.toObjects(Buddy::class.java))
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

}