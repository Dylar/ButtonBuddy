package de.bitb.buttonbuddy.core

import android.app.Application
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.BuddyRepositoryImpl
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.InfoRepositoryImpl
import de.bitb.buttonbuddy.data.source.BuddyDatabase
import de.bitb.buttonbuddy.data.source.FirestoreDatabase
import de.bitb.buttonbuddy.data.source.RemoteDatabase
import de.bitb.buttonbuddy.usecase.buddies.*
import de.bitb.buttonbuddy.usecase.info.InfoUseCases
import de.bitb.buttonbuddy.usecase.info.UpdateToken
import javax.inject.Singleton


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
    fun provideRoomDatabase(app: Application): BuddyDatabase =
        Room.databaseBuilder(
            app,
            BuddyDatabase::class.java,
            BuddyDatabase.DATABASE_NAME
        ).build()

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
    fun provideRemoteDatabase(firestore: FirebaseFirestore): RemoteDatabase =
        FirestoreDatabase(firestore)

    // REPO
    @Provides
    @Singleton
    fun provideBuddyRepository(
        remoteDB: RemoteDatabase,
        localDB: BuddyDatabase
    ): BuddyRepository = BuddyRepositoryImpl(remoteDB, localDB.buddyDao)

    @Provides
    @Singleton
    fun provideInfoRepository(
        remote: RemoteDatabase,
        local: BuddyDatabase,
    ): InfoRepository = InfoRepositoryImpl(remote, local.infoDao)

    //USE CASES
    @Provides
    @Singleton
    fun provideBuddyUseCases(
        infoRepo: InfoRepository,
        buddyRepo: BuddyRepository,
    ): BuddyUseCases = BuddyUseCases(
        login = LoginUC(infoRepo, buddyRepo),
        scanBuddy = ScanBuddyUC(infoRepo, buddyRepo),
        loadBuddies = LoadBuddiesUC(infoRepo, buddyRepo),
        sendMessage = SendMessageUC(),
    )

    @Provides
    @Singleton
    fun provideInfoUseCases(
        infoRepo: InfoRepository,
    ): InfoUseCases = InfoUseCases(
        updateToken = UpdateToken(infoRepo),
    )
}

//@Module
//@InstallIn(FragmentComponent::class)
//object FragmentModule {
//
//}