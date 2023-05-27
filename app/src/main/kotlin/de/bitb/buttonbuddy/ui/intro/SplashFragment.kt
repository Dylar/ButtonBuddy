package de.bitb.buttonbuddy.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.KEY_BUDDY_UUID
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.composable.LoadingIndicator

@AndroidEntryPoint
class SplashFragment : BaseFragment<SplashViewModel>() {
    override val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleNotificationIntent(activity?.intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        // TODO check das xD
        intent?.getStringExtra(KEY_BUDDY_UUID)?.let { uuid ->
            navController.apply {
                popBackStack(destinationId = R.id.splashFragment, inclusive = false)
                navigate(R.id.buddiesFragment)
                navigate(R.id.buddyFragment, bundleOf(KEY_BUDDY_UUID to uuid))
            }
        } ?: viewModel.loadData()
    }

    @Composable
    override fun ScreenContent() = LoadingIndicator()

}