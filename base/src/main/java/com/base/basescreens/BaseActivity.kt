package com.base.basescreens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.savesage.base.callbacks.AlertDialogCallback
import com.savesage.base.permission.PermissionDelegates

abstract class BaseActivity<DB : ViewDataBinding> : AppCompatActivity() {

    abstract fun getLayoutRes(): Int

    abstract fun onViewReady(savedInstanceState: Bundle?)

    private lateinit var permissionDelegate: PermissionDelegates
    protected lateinit var binding: DB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = DataBindingUtil.setContentView(this, getLayoutRes())
        } catch (e: Exception) {
            if (e is Resources.NotFoundException) {
                throw RuntimeException("Invalid Layout Resource Id")
            }
        }
        onViewReady(savedInstanceState)
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val permissionPair = bifurcatePermissions(permissions)
            when {
                permissionPair.second.isNullOrEmpty() -> {
                    permissionDelegate.onPermissionGranted(permissionPair.first)
                }
                permissionPair.first.isNullOrEmpty() -> {
                    permissionDelegate.onPermissionDenied(permissionPair.second)
                }
                else -> {
                    permissionDelegate.onPermissionGranted(permissionPair.first)
                    permissionDelegate.onPermissionDenied(permissionPair.second)
                }
            }
        }

    private fun bifurcatePermissions(permissions: Map<String, @JvmSuppressWildcards Boolean>): Pair<List<String>, List<String>> {
        val grantedPermissions = ArrayList<String>()
        val deniedPermissions = ArrayList<String>()
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            if (!isGranted) {
                deniedPermissions.add(permissionName)
            } else {
                grantedPermissions.add(permissionName)
            }
        }
        return Pair(grantedPermissions, deniedPermissions)
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    fun askForPermissions(delegates: PermissionDelegates, permissions: Array<String>) {
        permissionDelegate = delegates
        isPermissionDeclaredInManifest(permissions)

        val allGranted = isAllPermissionsGranted(permissions)

        if (!allGranted)
            requestPermissions(permissions)
        else
            delegates.onPermissionGranted(permissions.toList())
    }

    fun askForPermissionsWithRationale(
        delegates: PermissionDelegates,
        permissions: Array<String>,
        message: String
    ) {
        permissionDelegate = delegates
        isPermissionDeclaredInManifest(permissions)

        val allGranted = isAllPermissionsGranted(permissions)

//        if (!allGranted)
//            showAlert("Permission Required!!", message, 1001, object : AlertDialogCallback {
//                override fun onPositiveButton(dialogId: Int) {
//                    requestPermissions(permissions)
//                }
//
//                override fun onNegativeButton(dialogId: Int) {
//                    delegates.onPermissionDenied(permissions.toList())
//                }
//
//            })
    }

    private fun requestPermissions(permissions: Array<String>) {
        requestPermissionsLauncher.launch(
            permissions
        )
    }

    private fun isAllPermissionsGranted(permissions: Array<String>): Boolean {
        var allGranted = true
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                allGranted = false
            }
        }
        return allGranted
    }

    private fun isPermissionDeclaredInManifest(permissions: Array<String>) {
        val packageInfo: PackageInfo =
            packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        val requestedPermissions = packageInfo.requestedPermissions

        permissions.forEach {
            if (requestedPermissions != null) {
                if (!requestedPermissions.contains(it)) {
                    throw RuntimeException("$it is not declared in app manifest")
                }
            }
        }
    }



}