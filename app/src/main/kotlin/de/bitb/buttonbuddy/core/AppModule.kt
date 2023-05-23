package de.bitb.buttonbuddy.core

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.bitb.buttonbuddy.data.*
import de.bitb.buttonbuddy.data.source.*
import de.bitb.buttonbuddy.usecase.BuddyUseCases
import de.bitb.buttonbuddy.usecase.MessageUseCases
import de.bitb.buttonbuddy.usecase.UserUseCases
import de.bitb.buttonbuddy.usecase.buddies.ScanBuddyUC
import de.bitb.buttonbuddy.usecase.buddies.SetCooldownUC
import de.bitb.buttonbuddy.usecase.message.ReceivingMessageUC
import de.bitb.buttonbuddy.usecase.message.SendMessageUC
import de.bitb.buttonbuddy.usecase.user.LoadDataUC
import de.bitb.buttonbuddy.usecase.user.LoginUC
import de.bitb.buttonbuddy.usecase.user.RegisterUC
import de.bitb.buttonbuddy.usecase.user.UpdateTokenUC
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
            .fallbackToDestructiveMigration() //TODO made real migrations
            .build()
        val pref = app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return BuddyLocalDatabase(db, PreferenceDatabase(pref))
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
        val fireData = FirebaseFirestore.getInstance()
        val fireAuth = FirebaseAuth.getInstance()
        val fireService = FirestoreService(fireData, fireAuth)

        return BuddyRemoteService(fireService, retrofitService)
    }

    // REPO
    @Provides
    @Singleton
    fun provideSettingsRepository(
        remoteService: RemoteService,
        localDB: LocalDatabase,
    ): SettingsRepository = SettingsRepositoryImpl(remoteService, localDB)

    @Provides
    @Singleton
    fun provideUserRepository(
        remoteService: RemoteService,
        localDB: LocalDatabase,
    ): UserRepository = UserRepositoryImpl(remoteService, localDB)

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
    fun provideUserUseCases(
        settingsRepo: SettingsRepository,
        userRepo: UserRepository,
        buddyRepo: BuddyRepository,
        msgRepo: MessageRepository,
    ): UserUseCases = UserUseCases(
        loadDataUC = LoadDataUC(settingsRepo, userRepo, buddyRepo, msgRepo),
        loginUC = LoginUC(settingsRepo, userRepo, buddyRepo),
        registerUC = RegisterUC(userRepo),
    )

    @Provides
    @Singleton
    fun provideBuddyUseCases(
        userRepo: UserRepository,
        buddyRepo: BuddyRepository,
    ): BuddyUseCases {
        return BuddyUseCases(
            scanBuddyUC = ScanBuddyUC(userRepo, buddyRepo),
            setCooldownUC = SetCooldownUC(userRepo, buddyRepo),
        )
    }

    @Provides
    @Singleton
    fun provideMessageUseCases(
        app: Application,
        remoteService: RemoteService,
        settingsRepo: SettingsRepository,
        userRepo: UserRepository,
        msgRepo: MessageRepository,
    ): MessageUseCases = MessageUseCases(
        updateTokenUC = UpdateTokenUC(userRepo),
        sendMessageUC = SendMessageUC(remoteService, settingsRepo, userRepo, msgRepo),
        receivingMessageUC = ReceivingMessageUC(msgRepo, NotifyManager(app)),
    )
}

//@Module
//@InstallIn(FragmentComponent::class)
//object FragmentModule {
//
//}