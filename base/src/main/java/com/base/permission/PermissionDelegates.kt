package com.savesage.base.permission

interface PermissionDelegates {

    fun onPermissionGranted(permissions: List<String>)

    fun onPermissionDenied(permissions: List<String>)
}