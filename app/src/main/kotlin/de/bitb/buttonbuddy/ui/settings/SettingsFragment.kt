package de.bitb.buttonbuddy.ui.settings

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.model.Settings
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.composable.LoadingIndicator
import de.bitb.buttonbuddy.ui.base.styles.createComposeView

data class PreferenceItem(val title: String, val subtitle: String)

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel>() {
    companion object {
        const val APPBAR_TAG = "SettingAppbar"
    }

    override val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView { SettingsScreen() }

    @Composable
    fun SettingsScreen() {
        val settings by viewModel.settings.observeAsState(null)
        val title = getString(R.string.settings_title)
//        Scaffold(
//            scaffoldState = scaffoldState,
//            topBar = {
//                TopAppBar(
//                    modifier = Modifier.testTag(APPBAR_TAG),
//                    title = { Text(title) },
//                )
//            },
//            content = {
//                when {
//                    settings != null -> SettingPage(it, settings!!)
//                    else -> LoadingIndicator()
//                }
//            },
//        )
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                Surface(elevation = 4.dp) {
                    TopAppBar(
                        title = { Text(text = title) },
                        contentColor = MaterialTheme.colors.onSurface,
                        backgroundColor = Color.Transparent,
                        elevation = 0.dp,
//                        navigationIcon = {
//                            IconButton(
//                                onClick = onNavigateBack,
//                                content = {
//                                    Icon(
//                                        imageVector = Icons.Default.ArrowBack,
//                                        contentDescription = null,
//                                    )
//                                },
//                            )
//                        },
                        modifier = Modifier.statusBarsPadding(),
                    )
                }
            },
        ) { innerPadding ->
            when {
                settings != null -> SettingPage(innerPadding, settings!!)
                else -> LoadingIndicator()
            }
        }
    }

    @Composable
    fun SettingPage(padding: PaddingValues, settings: Settings) {
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            PreferenceSwitch(
                PreferenceItem("DarkMode", "Ist der DarkMode aktiviert?"),
                checked = settings.isDarkMode,
                onChange = {
                    viewModel.saveSettings(settings.copy(isDarkMode = it))
                },
            )
            PreferenceEditText(
                PreferenceItem(
                    "Cooldown",
                    "Zeit bis der Button wieder gedrückt werden darf"
                ),
                value = settings.cooldown.toString(),
                onChange = {
                    viewModel.saveSettings(settings.copy(cooldown = it.toLong()))
                },
                keyboardType = KeyboardType.Number,
            )
        }
    }

}
