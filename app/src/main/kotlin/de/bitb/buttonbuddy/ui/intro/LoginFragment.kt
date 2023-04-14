package de.bitb.buttonbuddy.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.naviToRegister
import de.bitb.buttonbuddy.ui.base.styles.BaseColors
import de.bitb.buttonbuddy.ui.base.styles.createComposeView
import de.bitb.buttonbuddy.ui.info.InfoDialog

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel>() {
    companion object {
        const val APPBAR_TAG = "LoginAppbar"
        const val INFO_BUTTON_TAG = "LoginInfoButton"
        const val USER_NAME_TAG = "LoginUserName"
        const val PW_TAG = "LoginPW"
        const val REGISTER_BUTTON_TAG = "LoginRegisterButton"
        const val LOGIN_BUTTON_TAG = "LoginButton"
        const val ERROR_TAG = "LoginError"
    }

    override val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView {
        val showDialog = remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(getString(R.string.login_title)) },
                    actions = {
                        IconButton(
                            modifier = Modifier.testTag(INFO_BUTTON_TAG),
                            onClick = { showDialog.value = !showDialog.value }
                        ) { Icon(Icons.Default.Info, contentDescription = "Info") }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(all = 32.dp)
                        .testTag(LOGIN_BUTTON_TAG),
                    onClick = { viewModel.login() }
                ) { Icon(Icons.Default.ArrowForward, contentDescription = "Login") }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                        .testTag(USER_NAME_TAG),
                    value = viewModel.userName,
                    onValueChange = { viewModel.userName = it },
                    label = { Text(getString(R.string.user_name)) },
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(PW_TAG),
                    value = viewModel.pw,
                    onValueChange = { viewModel.pw = it },
                    label = { Text(getString(R.string.pw1_label)) }
                )
                Spacer(modifier = Modifier.padding(top = 8.dp))
                viewModel.error?.let {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .testTag(ERROR_TAG),
                        contentAlignment = Alignment.TopCenter,
                    ) { Text(it.asString(), color = BaseColors.FireRed) }
                }
                Button(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(REGISTER_BUTTON_TAG),
                    onClick = ::naviToRegister,
                    content = {
                        Text(
                            text = getString(R.string.login_register_account),
                            textAlign = TextAlign.Center,
                        )
                    },
                )
            }
        }

        if (showDialog.value) {
            InfoDialog { showDialog.value = false }
        }
    }
}