package de.bitb.buttonbuddy.ui.buddies

import android.util.Log
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.buildBuddy
import de.bitb.buttonbuddy.buildInfo
import de.bitb.buttonbuddy.core.*
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.ui.buddy.BuddyFragment
import de.bitb.buttonbuddy.ui.scan.ScanFragment
import de.bitb.buttonbuddy.usecase.InfoUseCases
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import launchFragment
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
    lateinit var infoRepository: InfoRepository

    @Inject
    lateinit var remoteService: RemoteService

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun render_buddiesFragment() {
        launchFragment<BuddiesFragment>()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(BuddiesFragment.APPBAR_TAG)
            .assertIsDisplayed()
            .assertTextOnChildren(getString(R.string.buddies_title))
        composeRule.onNodeWithTag(BuddiesFragment.PROFILE_BUTTON_TAG)
            .assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.no_buddies))
            .assertIsDisplayed()
        composeRule.onNodeWithTag(BuddiesFragment.SCAN_BUTTON_TAG)
            .assertIsDisplayed()
//        composeRule.onNodeWithTag(BuddiesFragment.REFRESH_INDICATOR_TAG)
//            .assertExists()
//            .assertIsNotDisplayed()
    }

    @Test
    fun render_buddiesList() = runTest {
        val info = buildInfo()
        val buddy = buildBuddy()
        infoRepository.saveInfo(info)
        remoteService.mockRemoteService(info, listOf(buddy))

        launchFragment<BuddiesFragment>()
        composeRule.waitForIdle()
        composeRule.onNodeWithText(getString(R.string.no_buddies))
            .assertDoesNotExist()
        composeRule.onNodeWithText(BuddiesFragment.LIST_TAG)
            .onChildren()
            .assertCountEquals(3)
    }

    @Test
    fun clickProfileButton_ShowNoUUIDSnackbar() {
        launchFragment<BuddiesFragment>()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(getString(R.string.no_uuid))
            .assertDoesNotExist()
        composeRule.onNodeWithTag(BuddiesFragment.PROFILE_BUTTON_TAG)
            .performClick()
        composeRule.onNodeWithText(getString(R.string.no_uuid))
            .assertIsDisplayed()
        composeRule.onNodeWithTag(BuddyFragment.APPBAR_TAG)
            .assertDoesNotExist()
    }

    @Test
    fun clickProfileButton_navigateToProfile() = runTest {
        val info = buildInfo()
        infoRepository.saveInfo(info)
        remoteService.mockRemoteService(info)

        launchFragment<BuddiesFragment>()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(getString(R.string.no_uuid))
            .assertDoesNotExist()
        composeRule.onAllNodesWithTag(BuddiesFragment.PROFILE_BUTTON_TAG)
            .onFirst() // but why multiple?
            .performClick()
        composeRule.onNodeWithText(getString(R.string.no_uuid))
            .assertDoesNotExist()

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(BuddyFragment.APPBAR_TAG)
            .assertIsDisplayed()
    }

    @Test
    fun clickScanButton_navigateToScan() = runTest {
        val info = buildInfo()
        infoRepository.saveInfo(info)
        remoteService.mockRemoteService(info)

        launchFragment<BuddiesFragment>()
        composeRule.waitForIdle()

        composeRule.onAllNodesWithTag(BuddiesFragment.SCAN_BUTTON_TAG)
            .onFirst() // but why multiple?
            .performClick()

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(ScanFragment.APPBAR_TAG)
            .assertIsDisplayed()
    }

}
