package de.bitb.buttonbuddy.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.styles.FireRed
import de.bitb.buttonbuddy.ui.styles.ZergPurple
import de.bitb.buttonbuddy.ui.styles.createComposeView

@AndroidEntryPoint
class LoginFragment : BaseFragment<IntroViewModel>() {
    override val viewModel: IntroViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Login") }) },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(all = 32.dp),
                    onClick = { viewModel.login() }
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Login")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
                    value = viewModel.firstName,
                    onValueChange = { viewModel.firstName = it },
                    label = { Text("First name") },
                )
                OutlinedTextField(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    value = viewModel.lastName,
                    onValueChange = { viewModel.lastName = it },
                    label = { Text("Last name") }
                )
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    viewModel.error?.let { Text(it.asString(), color = FireRed) }
                }
            }
        }
    }
}