package de.bitb.buttonbuddy.core

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.bitb.buttonbuddy.data.*
import de.bitb.buttonbuddy.data.source.*
import de.bitb.buttonbuddy.shared.buildUser
import de.bitb.buttonbuddy.usecase.BuddyUseCases
import de.bitb.buttonbuddy.usecase.MessageUseCases
import de.bitb.buttonbuddy.usecase.UserUseCases
import de.bitb.buttonbuddy.usecase.buddies.ScanBuddyUC
import de.bitb.buttonbuddy.usecase.buddies.SetCooldownUC
import de.bitb.buttonbuddy.usecase.message.ReceivingMessageUC
import de.bitb.buttonbuddy.usecase.message.SendMessageUC
import de.bitb.buttonbuddy.usecase.user.*
import io.mockk.mockk
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    // DATABASE
    @Provides
    @Singleton
    fun provideLocalDatabase(app: Application): LocalDatabase {
        val db = Room.inMemoryDatabaseBuilder(app, RoomDatabaseImpl::class.java).build()
        val pref = app.getSharedPreferences("test_pref", Context.MODE_PRIVATE)
        return BuddyLocalDatabase(db, PreferenceDatabase(pref))
    }

    @Provides
    @Singleton
    fun provideRemoteDatabase(): RemoteService = mockk<RemoteService>()
        .apply { mockUserDao(buildUser(), isLoggedIn = true) }

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
        logoutUC = LogoutUC(userRepo, buddyRepo, msgRepo),
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