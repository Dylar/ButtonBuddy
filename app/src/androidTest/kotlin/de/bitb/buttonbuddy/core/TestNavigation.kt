package de.bitb.buttonbuddy.core

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.misc.Logger
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.ui.buddies.BuddiesFragment
import de.bitb.buttonbuddy.ui.intro.LoginFragment
import kotlinx.coroutines.*

sealed class TestNavigation {
    object Splash : TestNavigation()
    object Login : TestNavigation()
    object Register : TestNavigation()
    data class Buddies(val user: User, val pw: String = "pw") : TestNavigation()
    data class BuddyDetail(val user: User, val pw: String = "pw", val buddy: Buddy) :
        TestNavigation()

    data class Profile(val user: User, val pw: String = "pw") : TestNavigation()
    data class Scan(val user: User, val pw: String = "pw") : TestNavigation()
    object Settings : TestNavigation()
}

inline fun <reified T : Fragment> AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.getFragment(): T {
    val navFrag = activity.supportFragmentManager.fragments.first() as NavHostFragment
    val frag = navFrag.childFragmentManager.fragments.firstOrNull { it is T } as? T
    return frag ?: throw Exception("Fragment ${T::class.java.name} not found")
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.navigateTo(
    naviTo: TestNavigation,
) {
    when (naviTo) {
        TestNavigation.Splash -> TODO()
        TestNavigation.Register -> tapRegister()
        TestNavigation.Login -> doNothing()
        is TestNavigation.Buddies -> doLogin(naviTo.user, naviTo.pw)
        is TestNavigation.BuddyDetail -> {
            doLogin(naviTo.user, naviTo.pw)
            tapBuddy(naviTo.buddy)
        }
        is TestNavigation.Profile -> {
            doLogin(naviTo.user, naviTo.pw)
            tapProfile()
        }
        is TestNavigation.Scan -> {
            doLogin(naviTo.user, naviTo.pw)
            tapScan()
        }
        TestNavigation.Settings -> TODO()
    }
    waitForIdle()
}

fun doNothing() {}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.tapRegister() {
    onNodeWithTag(LoginFragment.REGISTER_BUTTON_TAG).performClick()
    waitForIdle()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.doLogin(
    user: User,
    pw: String
) {
    onNodeWithTag(LoginFragment.EMAIL_TAG).performTextInput(user.email)
    onNodeWithTag(LoginFragment.PW_TAG).performTextInput(pw)
    onNodeWithTag(LoginFragment.LOGIN_BUTTON_TAG).performClick()
    waitForIdle()
//    runBlocking { delay(5000) }
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.tapBuddy(buddy: Buddy) {
    onNodeWithTag(BuddiesFragment.LIST_TAG)
        .onChildren()
        .filterToOne(hasText(buddy.fullName))
        .performClick()
    waitForIdle()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.tapProfile() {
    onNodeWithTag(BuddiesFragment.PROFILE_BUTTON_TAG).performClick()
    waitForIdle()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.tapScan() {
    onNodeWithTag(BuddiesFragment.SCAN_BUTTON_TAG).performClick()
    waitForIdle()
}
