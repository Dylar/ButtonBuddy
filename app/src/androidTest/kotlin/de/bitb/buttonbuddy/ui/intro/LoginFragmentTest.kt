package de.bitb.buttonbuddy.ui.intro

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.*
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.shared.buildUser
import de.bitb.buttonbuddy.ui.buddies.BuddiesFragment
import de.bitb.buttonbuddy.usecase.user.LoginResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
class LoginFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var infoRepository: UserRepository

    @Inject
    lateinit var remoteService: RemoteService

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    private fun LoginResponse.asString(): String = message.asString(::getString)

    @Test
    fun render_loginFragment() = runTest {
        composeRule.apply {
            launchActivity(TestNavigation.Login)
            waitForIdle()

            onNodeWithTag(LoginFragment.APPBAR_TAG)
                .assertIsDisplayed()
                .onChildren()
                .assertAny(hasText(getString(R.string.login_title)))
            onNodeWithTag(LoginFragment.INFO_BUTTON_TAG)
                .assertIsDisplayed()
            onNodeWithTag(LoginFragment.LOGIN_BUTTON_TAG)
                .assertIsDisplayed()
            onNodeWithTag(LoginFragment.EMAIL_TAG)
                .assertIsDisplayed()
//                .onChildren() TODO why not?
//                .assertAny(hasText(getString(R.string.email)))
            onNodeWithTag(LoginFragment.PW_TAG)
                .assertIsDisplayed()
//                .onChildren()
//                .assertAny(hasText(getString(R.string.pw1_label)))
            onNodeWithTag(LoginFragment.LOGIN_BUTTON_TAG)
                .assertIsDisplayed()
        }
    }

    @Test
    fun test_login_errors() = runTest {
        composeRule.apply {
            remoteService.mockUserService(buildUser())

            launchActivity(TestNavigation.Login)
            waitForIdle()

            onNodeWithTag(LoginFragment.LOGIN_BUTTON_TAG).performClick()
            waitForIdle()
            onNodeWithTag(LoginFragment.ERROR_TAG)
                .onChildren()
                .assertAny(hasText(LoginResponse.EmailEmpty().asString()))

            onNodeWithTag(LoginFragment.EMAIL_TAG).performTextInput("email@gmx.de")
            onNodeWithTag(LoginFragment.LOGIN_BUTTON_TAG).performClick()
            waitForIdle()
            onNodeWithTag(LoginFragment.ERROR_TAG)
                .onChildren()
                .assertAny(hasText(LoginResponse.PwEmpty().asString()))

            onNodeWithTag(LoginFragment.PW_TAG).performTextInput("password")
            onNodeWithTag(LoginFragment.LOGIN_BUTTON_TAG).performClick()
            waitForIdle()
            runBlocking { delay(10000) }
            onNodeWithTag(BuddiesFragment.APPBAR_TAG)
                .assertIsDisplayed()
        }
    }
}
