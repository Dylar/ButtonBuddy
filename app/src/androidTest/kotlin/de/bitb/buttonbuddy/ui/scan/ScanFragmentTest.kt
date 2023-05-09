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

            launchActivity(TestNavigation.Scan(user, pw = "pw"))
            waitForIdle()
            onNodeWithTag(ScanFragment.APPBAR_TAG)
                .assertIsDisplayed()
                .onChildren()
                .assertAny(hasText(getString(R.string.scan_title)))
        }
    }

    @Test
    fun askCameraPermission() = runTest {
        composeRule.apply {
            val buddy = buildBuddy()
            val user = buildUser(mutableListOf(buddy.uuid))
            launchActivity(TestNavigation.Scan(user, pw = "pw"))

            // Prüfen, ob die Kamera-Berechtigung angefordert wird
            activity.requestedPermissions.contains(Manifest.permission.CAMERA)

            // Berechtigung verweigern und prüfen, ob das Dialogfeld angezeigt wird
            val permissionDeniedEvent = viewModel.visiblePermissionDialogQueue.firstOrNull()
            assert(permissionDeniedEvent)
            assertEquals(Manifest.permission.CAMERA, permissionDeniedEvent.permission)

            // Bestätigen, dass das Dialogfeld angezeigt wird
            composeTestRule.onNodeWithText(R.string.camera_permission_dialog_title).assertIsDisplayed()
            composeTestRule.onNodeWithText(R.string.camera_permission_dialog_message).assertIsDisplayed()
            composeTestRule.onNodeWithText(R.string.camera_permission_dialog_button_text).assertIsDisplayed()
        }
    }
}
