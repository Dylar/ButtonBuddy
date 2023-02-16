package de.bitb.buttonbuddy.data.source

import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.buttonbuddy.data.model.Buddy
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface RemoteDatabase {
    suspend fun loadBuddies(buddyIds: List<String>): List<Buddy>
}

class FirestoreDatabase @Inject constructor(
    private val firestore: FirebaseFirestore
) : RemoteDatabase {

    override suspend fun loadBuddies(buddyIds: List<String>): List<Buddy> {
        val snapshot = firestore.collection("Buddies")
            .whereArrayContains("buddies", buddyIds).get().await()
        return snapshot.toObjects(Buddy::class.java)
    }
}