package de.bitb.buttonbuddy.ui.buddy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.composable.LoadingIndicator
import de.bitb.buttonbuddy.ui.styles.createComposeView

@AndroidEntryPoint
class BuddyFragment : BaseFragment<BuddyViewModel>() {

    override val viewModel: BuddyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uuid = arguments?.getString("uuid") ?: throw Exception()
        viewModel.initBuddyState(uuid)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView {
        val buddy by viewModel.buddy.observeAsState(null)
        BuddyScreen(buddy)
    }

    @Composable
    fun BuddyScreen(buddy: Buddy?) {
        val info by viewModel.info.observeAsState(null)
        Scaffold(
            topBar = { TopAppBar(title = { Text("Buddy") }) },
            content = {
                when (buddy) {
                    null -> LoadingIndicator()
                    else -> BuddyDetails(it, buddy, info)
                }
            },
        )
    }

    @Composable
    fun BuddyDetails(padding: PaddingValues, buddy: Buddy, info: Info?) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Vorname: ${buddy.firstName}")
            Text("Nachname: ${buddy.lastName}")
            if (buddy.uuid == info?.uuid) Text("Uuid: ${buddy.uuid}")
        }
    }
}
