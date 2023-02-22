package de.bitb.buttonbuddy.core

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.BuddyRepositoryImpl
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.InfoRepositoryImpl
import de.bitb.buttonbuddy.data.source.*
import de.bitb.buttonbuddy.data.source.FirestoreDatabase
import de.bitb.buttonbuddy.usecase.buddies.*
import de.bitb.buttonbuddy.usecase.info.InfoUseCases
import de.bitb.buttonbuddy.usecase.info.LoginUC
import de.bitb.buttonbuddy.usecase.info.UpdateTokenUC
import javax.inject.Singleton

const val DATABASE_NAME = "buddy_db"
const val PREF_NAME = "buddy_pref"

@HiltAndroidApp
class BuddyApp : Application()

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // CORE
    @Provides
    @Singleton
    fun provideNotifyManager(app: Application): NotifyManager =
        NotifyManager(app)

    // DATABASE
    @Provides
    @Singleton
    fun provideLocalDatabase(app: Application): LocalDatabase {
        val db = Room.databaseBuilder(app, RoomDatabaseImpl::class.java, DATABASE_NAME).build()
        val pref = app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return BuddyDatabase(db,pref)
    }

    @Provides
    @Singleton
    fun provideRemoteDatabase(firestore: FirebaseFirestore): RemoteDatabase =
        FirestoreDatabase(firestore)

    @Provides
    @Singleton
    fun provideFirebase(app: Application): FirebaseFirestore {
//        val options = FirebaseOptions.Builder()
//            .setApplicationId("de.bitb.buttonbuddy")
//            .setProjectId("buttonbuddy-51c67")
//            .setApiKey("your_api_key")
//            .setDatabaseUrl("your_database_url")
//            .setStorageBucket("your_storage_bucket")
//            .build()
//        FirebaseApp.initializeApp(app, options)
        FirebaseApp.initializeApp(app)
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFireMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

    // REPO
    @Provides
    @Singleton
    fun provideBuddyRepository(
        remoteDB: RemoteDatabase,
        localDB: LocalDatabase
    ): BuddyRepository = BuddyRepositoryImpl(remoteDB, localDB)

    @Provides
    @Singleton
    fun provideInfoRepository(
        remoteDB: RemoteDatabase,
        localDB: LocalDatabase,
    ): InfoRepository = InfoRepositoryImpl(remoteDB, localDB)

    //USE CASES
    @Provides
    @Singleton
    fun provideBuddyUseCases(
        infoRepo: InfoRepository,
        buddyRepo: BuddyRepository,
        fireMessaging: FirebaseMessaging,
    ): BuddyUseCases = BuddyUseCases(
        scanBuddy = ScanBuddyUC(infoRepo, buddyRepo),
        loadBuddies = LoadBuddiesUC(infoRepo, buddyRepo),
        sendMessage = SendMessageUC(fireMessaging),
    )

    @Provides
    @Singleton
    fun provideInfoUseCases(
        infoRepo: InfoRepository,
        buddyRepo: BuddyRepository,
    ): InfoUseCases = InfoUseCases(
        login = LoginUC(infoRepo, buddyRepo),
        updateTokenUC = UpdateTokenUC(infoRepo),
    )
}

//@Module
//@InstallIn(FragmentComponent::class)
//object FragmentModule {
//
//}