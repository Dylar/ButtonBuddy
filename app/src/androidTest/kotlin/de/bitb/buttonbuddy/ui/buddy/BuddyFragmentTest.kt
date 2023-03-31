package de.bitb.buttonbuddy.ui.buddy

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import de.bitb.buttonbuddy.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.bitb.buttonbuddy.core.*
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.shared.buildBuddy
import de.bitb.buttonbuddy.shared.buildUser
import de.bitb.buttonbuddy.shared.buildMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(AppModule::class)
@OptIn(ExperimentalCoroutinesApi::class)
class BuddyFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var remoteService: RemoteService

    @Inject
    lateinit var localDatabase: LocalDatabase

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun render_buddyFragment() = runTest {
        composeRule.apply {
            val buddy = buildBuddy()
            val info = buildUser(mutableListOf(buddy.uuid))
            remoteService.mockRemoteService(info, listOf(buddy))

            launchActivity(TestNavigation.BuddyDetail(info, buddy))
            waitForIdle()
            onNodeWithTag(BuddyFragment.APPBAR_TAG)
                .assertIsDisplayed()
                .onChildren()
                .assertAny(hasText(getString(R.string.buddy_title, ": ${buddy.fullName}")))
            onNodeWithTag(BuddyFragment.SEND_BUTTON_TAG)
                .assertIsDisplayed()
            onNodeWithText(getString(R.string.buddy_no_messages)).assertIsDisplayed()
            onNodeWithTag(BuddyFragment.SEND_BUTTON_TAG)
                .assertIsDisplayed()
        }
    }

    @Test
    fun renderMessages() = runTest {
        composeRule.apply {
            val buddy = buildBuddy()
            val info = buildUser(mutableListOf(buddy.uuid))
            val message1 = buildMessage(uuid = "msgUuid1", date = Date(200000000))
            val message2 = buildMessage(uuid = "msgUuid2", "uuid2", "uuid1", Date(100000000))
            val messages = listOf(message1, message2)
            remoteService.mockRemoteService(info, listOf(buddy))
            localDatabase.mockLocalDatabase(messages)

            launchActivity(TestNavigation.BuddyDetail(info, buddy))
            waitForIdle()

            onNodeWithTag(BuddyFragment.LIST_TAG)
                .apply {
                    onChildren().assertCountEquals(messages.size)
                    onChildAt(0)
                        .onChildren()
                        .assertAny(hasText(messages[1].formatDate))
                    onChildAt(1)
                        .onChildren()
                        .assertAny(hasText(messages[0].formatDate))
                }
        }
    }

}
