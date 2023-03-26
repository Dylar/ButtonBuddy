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
import de.bitb.buttonbuddy.usecase.BuddyUseCases
import de.bitb.buttonbuddy.usecase.UserUseCases
import de.bitb.buttonbuddy.usecase.MessageUseCases
import de.bitb.buttonbuddy.usecase.buddies.LoadBuddiesUC
import de.bitb.buttonbuddy.usecase.buddies.ScanBuddyUC
import de.bitb.buttonbuddy.usecase.user.LoginUC
import de.bitb.buttonbuddy.usecase.user.UpdateTokenUC
import de.bitb.buttonbuddy.usecase.message.ReceivingMessageUC
import de.bitb.buttonbuddy.usecase.message.SendMessageUC
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
    fun provideRemoteDatabase(): RemoteService = mockk(relaxed = true)

    // REPO
    @Provides
    @Singleton
    fun provideInfoRepository(
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
    fun provideInfoUseCases(
        infoRepo: UserRepository,
        buddyRepo: BuddyRepository,
    ): UserUseCases = UserUseCases(
        loginUC = LoginUC(infoRepo, buddyRepo),
    )

    @Provides
    @Singleton
    fun provideBuddyUseCases(
        infoRepo: UserRepository,
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
        infoRepo: UserRepository,
        msgRepo: MessageRepository,
    ): MessageUseCases = MessageUseCases(
        updateTokenUC = UpdateTokenUC(infoRepo),
        sendMessageUC = SendMessageUC(remoteService, localDB, infoRepo),
        receivingMessageUC = ReceivingMessageUC(msgRepo, NotifyManager(app)),
    )
}