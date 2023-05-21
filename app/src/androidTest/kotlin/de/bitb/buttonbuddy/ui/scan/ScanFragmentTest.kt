package de.bitb.buttonbuddy.ui.scan

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.*
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.shared.buildBuddy
import de.bitb.buttonbuddy.shared.buildUser
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
class ScanFragmentTest {

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
            val user = buildUser(mutableListOf(buddy.uuid))
            remoteService.mockWholeService(user, buddies = listOf(buddy))

            navigateTo(TestNavigation.Scan(user))
            waitForIdle()
            onNodeWithTag(ScanFragment.APPBAR_TAG)
                .assertIsDisplayed()
                .onChildren()
                .assertAny(hasText(getString(R.string.scan_title)))
            onNodeWithTag(ScanFragment.SCANNER_TAG)
                .assertIsDisplayed()
        }
    }

    @Test
    fun scanError() = runTest {
        composeRule.apply {
            val buddy = buildBuddy()
            val user = buildUser(mutableListOf(buddy.uuid))
            remoteService.mockWholeService(user, buddies = listOf(buddy))
            navigateTo(TestNavigation.Scan(user))

            val errorMessage = "ERROR"
            onNodeWithText(errorMessage).assertDoesNotExist()
            remoteService.mockBuddyDao(loadBuddiesError = Resource.Error(errorMessage))
            getFragment<ScanFragment>().viewModel.onScan("Wrong scan")
            onNodeWithText(errorMessage).assertIsDisplayed()
        }
    }
}
