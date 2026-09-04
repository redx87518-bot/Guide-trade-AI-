package com.guidetradeai.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

class PermissionHandler {
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        return androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }

    fun isAllPermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { isPermissionGranted(context, it) }
    }

    companion object {
        const val PERMISSION_RECORD_AUDIO = android.Manifest.permission.RECORD_AUDIO
        const val PERMISSION_POST_NOTIFICATIONS = android.Manifest.permission.POST_NOTIFICATIONS
        const val PERMISSION_INTERNET = android.Manifest.permission.INTERNET
        val VOICE_PERMISSIONS = arrayOf(PERMISSION_RECORD_AUDIO)
        val NOTIFICATION_PERMISSIONS = arrayOf(PERMISSION_POST_NOTIFICATIONS)
    }
}
