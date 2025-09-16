package com.savesage.base.callbacks

interface ListDialogCallback {
    fun onItemSelected(position: Int, item: String, dialogId: Int)
    fun onDismiss(dialogId: Int)
}