package de.bitb.buttonbuddy.ui.buddies

import androidx.compose.ui.test.*
import androidx.lifecycle.MutableLiveData
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.bitb.buttonbuddy.buildBuddy
import de.bitb.buttonbuddy.buildInfo
import de.bitb.buttonbuddy.core.AppModule
import de.bitb.buttonbuddy.core.MainActivity
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.getString
import de.bitb.buttonbuddy.ui.buddies.BuddiesFragment.Companion.APPBAR_TAG
import io.mockk.every
import io.mockk.mockk
import launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(AppModule::class)
class BuddiesFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

//    private val mockNavController = mockk<NavController>(relaxed = true)

    @Before
    fun setUp() {
//        clearAllMocks()
        hiltRule.inject()
//        every { mockNavController.navigate(any<Int>()) } returns Unit
//        composeRule.setContent {
//            val navController = rememberNavController()
//            CleanArchitectureNoteAppTheme {
//                NavHost(
//                    navController = navController,
//                    startDestination = Screen.NotesScreen.route
//                ) {
//                    composable(route = Screen.NotesScreen.route) {
//                        NotesScreen(navController = navController)
//                    }
//                }
//            }
//        }
    }

//     private fun launchFrag(viewModel: BuddiesViewModel) {
//        val args = Bundle().apply {
////            putString(KEY_BUDDY_UUID, uuid)
//        }
//
//         launchFragmentInHiltContainer<BuddiesFragment>(args).onFragment { fragment ->
//            fragment.viewModelStore.clear()
//            fragment.viewModelStore.put(BuddiesFragment::class.java.name, viewModel)
//        }
//    }

    private fun createModelView(
        infoInput: Info? = null,
        buddyInput: List<Buddy>? = null
    ): BuddiesViewModel {
        val infoObj = infoInput ?: buildInfo()
        val buddyObj = buddyInput ?: listOf(buildBuddy())
        return mockk<BuddiesViewModel>().apply {
            every { buddies } returns MutableLiveData(buddyObj)
            every { info } returns MutableLiveData(infoObj)
            every { refreshData() } answers { nothing }
            every { sendMessage(any()) } answers { nothing }
//            every { isRefreshing } returns mutableStateOf(false)
        }
    }

//    @Test
//    fun clickToggleOrderSection_isVisible() {
//        composeRule.onNodeWithTag(TestTags.ORDER_SECTION).assertDoesNotExist()
//        composeRule.onNodeWithContentDescription("Sort").performClick()
//        composeRule.onNodeWithTag(TestTags.ORDER_SECTION).assertIsDisplayed()
//    }

    @Test
    fun buddiesList_isDisplayed() {

        val string = getString(R.string.buddies_title)
        launchFragmentInHiltContainer<BuddiesFragment> {

        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(APPBAR_TAG).assertIsDisplayed()
//            .fetchSemanticsNode().config.forEach { (key,value) ->
//                key.
//            }
//            .assertTextEquals(string)
    }

}
