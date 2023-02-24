package de.bitb.buttonbuddy.core

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
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
import de.bitb.buttonbuddy.usecase.buddies.*
import de.bitb.buttonbuddy.usecase.info.InfoUseCases
import de.bitb.buttonbuddy.usecase.info.LoginUC
import de.bitb.buttonbuddy.usecase.info.UpdateTokenUC
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

const val DATABASE_NAME = "buddy_db"
const val PREF_NAME = "buddy_pref"

@HiltAndroidApp
class BuddyApp : Application()

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideBuddyRepository(
        remoteService: RemoteService,
        localDB: LocalDatabase
    ): BuddyRepository = BuddyRepositoryImpl(remoteService, localDB)

    @Provides
    @Singleton
    fun provideInfoRepository(
        remoteService: RemoteService,
        localDB: LocalDatabase,
    ): InfoRepository = InfoRepositoryImpl(remoteService, localDB)

    //USE CASES
    @Provides
    @Singleton
    fun provideBuddyUseCases(
        infoRepo: InfoRepository,
        buddyRepo: BuddyRepository,
        remoteService: RemoteService,
    ): BuddyUseCases = BuddyUseCases(
        scanBuddy = ScanBuddyUC(infoRepo, buddyRepo),
        loadBuddies = LoadBuddiesUC(infoRepo, buddyRepo),
        sendMessage = SendMessageUC(remoteService),
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