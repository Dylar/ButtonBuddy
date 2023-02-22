package de.bitb.buttonbuddy.ui.buddy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
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
        val isMyself = viewModel.uuid == info?.uuid
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { TopAppBar(title = { Text("Buddy") }) },
            content = {
                when {
                    isMyself && info != null -> InfoDetails(it, info!!)
                    buddy != null -> BuddyDetails(it, buddy)
                    else -> LoadingIndicator()
                }
            },
        )
    }

    @Composable
    fun BuddyDetails(padding: PaddingValues, buddy: Buddy) {
        return DetailScreen(padding, buddy.firstName, buddy.lastName)
    }

    @Composable
    fun InfoDetails(padding: PaddingValues, info: Info) {
        DetailScreen(padding, info.firstName, info.lastName, info.uuid)
    }

    @Composable
    private fun DetailScreen(
        padding: PaddingValues,
        firstName: String,
        lastName: String,
        uuid: String? = null,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Vorname: $firstName")
                Text("Nachname: $lastName")
                if (uuid != null) Text("Uuid: $uuid")
            }
        }
    }
}
