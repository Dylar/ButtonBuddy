package de.bitb.buttonbuddy.ui.buddies

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.shared.buildBuddy
import de.bitb.buttonbuddy.shared.buildInfo
import de.bitb.buttonbuddy.core.*
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.ui.buddy.BuddyFragment
import de.bitb.buttonbuddy.ui.profile.ProfileFragment
import de.bitb.buttonbuddy.ui.scan.ScanFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(AppModule::class)
@OptIn(ExperimentalCoroutinesApi::class)
class BuddiesFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var remoteService: RemoteService

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun render_buddiesFragment() = runTest {
        composeRule.apply {
            val info = buildInfo()
            remoteService.mockRemoteService(info)

            launchActivity(TestNavigation.Buddies(info))
            waitForIdle()

            onNodeWithTag(BuddiesFragment.APPBAR_TAG)
                .assertIsDisplayed()
                .onChildren()
                .assertAny(hasText(getString(R.string.buddies_title)))
            onNodeWithTag(BuddiesFragment.PROFILE_BUTTON_TAG)
                .assertIsDisplayed()
            onNodeWithText(getString(R.string.no_buddies))
                .assertIsDisplayed()
            onNodeWithTag(BuddiesFragment.SCAN_BUTTON_TAG)
                .assertIsDisplayed()
        }
    }

    @Test
    fun renderAndRefreshBuddies() = runTest {
        composeRule.apply {
            val buddy = buildBuddy()
            val buddies =
                mutableListOf(
                    buddy.copy(uuid = "uuid1", firstName = "first1"),
                    buddy.copy(uuid = "uuid2", firstName = "first2"),
                )

            val info = buildInfo(mutableListOf(buddies.first().uuid, buddies.last().uuid))
            remoteService.mockRemoteService(info, buddies)

            launchActivity(TestNavigation.Buddies(info))
            waitForIdle()

            onNodeWithTag(BuddiesFragment.LIST_TAG)
                .apply {
                    onChildren().assertCountEquals(buddies.size)
                    onChildAt(0).assert(hasText(buddies[0].fullName))
                    onChildAt(1).assert(hasText(buddies[1].fullName))
                }

            buddies.add(buddy.copy(uuid = "uuid3", firstName = "first3"))
            remoteService.mockRemoteService(info, buddies)

            onNodeWithTag(BuddiesFragment.LIST_TAG)
                .assertExists()
                .performTouchInput { swipeDown() }
            waitForIdle()

            onNodeWithTag(BuddiesFragment.LIST_TAG)
                .apply {
                    onChildren().assertCountEquals(buddies.size)
                    onChildAt(0).assert(hasText(buddies[0].fullName))
                    onChildAt(1).assert(hasText(buddies[1].fullName))
                    onChildAt(2).assert(hasText(buddies[2].fullName))
                }
        }
    }

    @Test
    fun clickSendButton_success() = runTest {
        composeRule.apply {
            val buddy = buildBuddy()
            val info = buildInfo(mutableListOf(buddy.uuid))
            remoteService.mockRemoteService(info, listOf(buddy))

            launchActivity(TestNavigation.Buddies(info))
            waitForIdle()

            val snackMsg = getString(R.string.message_sent_toast, buddy.fullName)
            onNodeWithText(snackMsg)
                .assertDoesNotExist()
            onNodeWithTag(BuddiesFragment.buddySendButtonTag(buddy))
                .performClick()
            onNodeWithText(snackMsg)
                .assertIsDisplayed()
        }
    }

    @Test
    fun clickSendButton_error() = runTest {
        composeRule.apply {
            val buddy = buildBuddy()
            val info = buildInfo(mutableListOf(buddy.uuid))
            val error = "ERROR"
            remoteService.mockRemoteService(info, listOf(buddy), sendMessageError = error)

            launchActivity(TestNavigation.Buddies(info))
            waitForIdle()

            onNodeWithText(error)
                .assertDoesNotExist()
            onNodeWithTag(BuddiesFragment.buddySendButtonTag(buddy))
                .performClick()
            onNodeWithText(error)
                .assertIsDisplayed()
        }
    }

    @Test
    fun clickBuddy_navigateToBuddy() = runTest {
        composeRule.apply {
            val buddy = buildBuddy()
            val info = buildInfo(mutableListOf(buddy.uuid))
            remoteService.mockRemoteService(info, listOf(buddy))

            launchActivity(TestNavigation.Buddies(info))
            waitForIdle()

            onNodeWithTag(BuddiesFragment.LIST_TAG).onChildAt(0).performClick()
            waitForIdle()

            onNodeWithTag(BuddyFragment.APPBAR_TAG)
                .assertExists()
                .assertIsDisplayed()
        }
    }

    @Test
    fun clickProfileButton_navigateToProfile() = runTest {
        composeRule.apply {
            val info = buildInfo()
            remoteService.mockRemoteService(info)

            launchActivity(TestNavigation.Buddies(info))
            waitForIdle()

            onNodeWithTag(BuddiesFragment.PROFILE_BUTTON_TAG)
                .assertExists()
                .performClick()
            waitForIdle()

            onNodeWithTag(ProfileFragment.APPBAR_TAG)
                .assertExists()
                .assertIsDisplayed()
        }
    }

    @Test
    fun clickScanButton_navigateToScan() = runTest {
        composeRule.apply {
            val info = buildInfo()
            remoteService.mockRemoteService(info)

            launchActivity(TestNavigation.Buddies(info))
            waitForIdle()

            onNodeWithTag(BuddiesFragment.SCAN_BUTTON_TAG)
                .performClick()

            waitForIdle()
            onNodeWithTag(ScanFragment.APPBAR_TAG)
                .assertIsDisplayed()
        }
    }

}
