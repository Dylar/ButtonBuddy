package de.bitb.buttonbuddy.ui.base.permission

import androidx.compose.runtime.mutableStateListOf

class PermissionHandlerImpl(
    override val visiblePermissionDialogQueue: MutableList<String> = mutableStateListOf()
) : PermissionHandler

interface PermissionHandler {
    val visiblePermissionDialogQueue: MutableList<String>

    fun dismissPermissionDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }
}