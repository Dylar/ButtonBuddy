package de.bitb.buttonbuddy.ui.scan

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.budiyev.android.codescanner.*
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.composable.LifecycleComp
import de.bitb.buttonbuddy.ui.base.openAppSettings
import de.bitb.buttonbuddy.ui.base.permission.CameraPermissionTextProvider
import de.bitb.buttonbuddy.ui.base.permission.PermissionDialog

@AndroidEntryPoint
class ScanFragment : BaseFragment<ScanViewModel>() {
    companion object {
        const val APPBAR_TAG = "ScanAppbar"
        const val SCANNER_TAG = "ScanScanner"
    }

    override val viewModel: ScanViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                viewModel.onPermissionResult(
                    permission = Manifest.permission.CAMERA,
                    isGranted = isGranted
                )
            }
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(stringResource(R.string.scan_title)) }
                )
            },
        ) { innerPadding ->
            ScannerPreview(innerPadding)
            cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
        }

        viewModel.visiblePermissionDialogQueue
            .reversed()
            .forEach { permission ->
                PermissionDialog(
                    permissionTextProvider = when (permission) {
                        Manifest.permission.CAMERA -> CameraPermissionTextProvider()
                        else -> return@forEach
                    },
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission),
                    onDismiss = viewModel::dismissPermissionDialog,
                    onOkClick = viewModel::dismissPermissionDialog,
                    onGoToAppSettingsClick = { activity?.openAppSettings() },
                )
            }
    }

    @Composable
    fun ScannerPreview(innerPadding: PaddingValues) {
        var codeScanner: CodeScanner? = null
        AndroidView(
            modifier = Modifier.padding(innerPadding).testTag(SCANNER_TAG),
            factory = { context ->
                CodeScannerView(context).apply {
                    isMaskVisible = true
                    CodeScanner(context, this).apply {
                        codeScanner = this
                        camera =
                            CodeScanner.CAMERA_BACK
                        formats = CodeScanner.ALL_FORMATS
                        autoFocusMode = AutoFocusMode.SAFE
                        scanMode = ScanMode.SINGLE
                        isAutoFocusEnabled = true
                        isFlashEnabled = false
                        errorCallback =
                            ErrorCallback { Log.e(this@ScanFragment.tag, it.toString()) }
                        decodeCallback = DecodeCallback { viewModel.onScan(it.text) }
                        setOnClickListener { startPreview() }
                        startPreview()
                    }
                }
            },
        )
        LifecycleComp { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> codeScanner?.startPreview()
                Lifecycle.Event.ON_PAUSE -> codeScanner?.releaseResources()
                else -> {}
            }
        }
    }
}