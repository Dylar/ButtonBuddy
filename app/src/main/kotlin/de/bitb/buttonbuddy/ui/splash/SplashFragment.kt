package de.bitb.buttonbuddy.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.KEY_BUDDY_UUID
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.composable.LoadingIndicator
import de.bitb.buttonbuddy.ui.base.styles.createComposeView

@AndroidEntryPoint
class SplashFragment : BaseFragment<IntroViewModel>() {
    override val viewModel: IntroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleNotificationIntent(activity?.intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        // TODO check das xD
        if (intent?.hasExtra(KEY_BUDDY_UUID) == true) {
            val uuid = intent.getStringExtra(KEY_BUDDY_UUID)
            navController.navigate(R.id.buddiesFragment)
            navController.navigate(R.id.buddyFragment, bundleOf(KEY_BUDDY_UUID to uuid))
        }else{
            viewModel.loadData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView { LoadingIndicator() }

}