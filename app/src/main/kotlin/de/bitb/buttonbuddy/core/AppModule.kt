package de.bitb.buttonbuddy.core

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.bitb.buttonbuddy.data.*
import de.bitb.buttonbuddy.data.source.*
import de.bitb.buttonbuddy.data.source.migrations.Migration1To2
import de.bitb.buttonbuddy.usecase.BuddyUseCases
import de.bitb.buttonbuddy.usecase.InfoUseCases
import de.bitb.buttonbuddy.usecase.MessageUseCases
import de.bitb.buttonbuddy.usecase.buddies.*
import de.bitb.buttonbuddy.usecase.info.LoginUC
import de.bitb.buttonbuddy.usecase.message.ReceivingMessageUC
import de.bitb.buttonbuddy.usecase.info.UpdateTokenUC
import de.bitb.buttonbuddy.usecase.message.SendMessageUC
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

const val DATABASE_NAME = "buddy_db"
const val PREF_NAME = "buddy_pref"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // DATABASE
    @Provides
    @Singleton
    fun provideLocalDatabase(app: Application): LocalDatabase {
        val db = Room.databaseBuilder(app, RoomDatabaseImpl::class.java, DATABASE_NAME)
            .addMigrations(Migration1To2())
            .build()
        val pref = app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return BuddyLocalDatabase(db, PreferenceDatabaseImpl(pref))
    }

    @Provides
    @Singleton
    fun provideRemoteDatabase(app: Application): RemoteService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = RetrofitService(retrofit.create(RetrofitApi::class.java))

        FirebaseApp.initializeApp(app)
        val firestore = FirebaseFirestore.getInstance()
        val fireService = FirestoreService(firestore)

        return BuddyRemoteService(fireService, retrofitService)
    }

    // REPO
    @Provides
    @Singleton
    fun provideInfoRepository(
        remoteService: RemoteService,
        localDB: LocalDatabase,
    ): InfoRepository = InfoRepositoryImpl(remoteService, localDB)

    @Provides
    @Singleton
    fun provideBuddyRepository(
        remoteService: RemoteService,
        localDB: LocalDatabase
    ): BuddyRepository = BuddyRepositoryImpl(remoteService, localDB)

    @Provides
    @Singleton
    fun provideMessageRepository(
        remoteService: RemoteService,
        localDB: LocalDatabase,
    ): MessageRepository = MessageRepositoryImpl(remoteService, localDB)

    //USE CASES
    @Provides
    @Singleton
    fun provideInfoUseCases(
        infoRepo: InfoRepository,
        buddyRepo: BuddyRepository,
    ): InfoUseCases = InfoUseCases(
        loginUC = LoginUC(infoRepo, buddyRepo),
    )

    @Provides
    @Singleton
    fun provideBuddyUseCases(
        infoRepo: InfoRepository,
        buddyRepo: BuddyRepository,
    ): BuddyUseCases {
        return BuddyUseCases(
            scanBuddyUC = ScanBuddyUC(infoRepo, buddyRepo),
            loadBuddiesUC = LoadBuddiesUC(infoRepo, buddyRepo),
        )
    }

    @Provides
    @Singleton
    fun provideMessageUseCases(
        app: Application,
        remoteService: RemoteService,
        localDB: LocalDatabase,
        infoRepo: InfoRepository,
        msgRepo: MessageRepository,
    ): MessageUseCases = MessageUseCases(
        updateTokenUC = UpdateTokenUC(infoRepo),
        sendMessageUC = SendMessageUC(remoteService, localDB, infoRepo),
        receivingMessageUC = ReceivingMessageUC(msgRepo, NotifyManager(app)),
    )
}

//@Module
//@InstallIn(FragmentComponent::class)
//object FragmentModule {
//
//}