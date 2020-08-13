package xyz.ariefbayu.xyzbarcodescanner.ui.main

import android.arch.lifecycle.ViewModel

class ScanViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private var scantext: String? = ""
    fun getScanText(): String? {
        return scantext
    }

    fun setScanText(text: String) {
        scantext = text
    }
}