package com.vbrawl.rnfccardreader

import android.nfc.NdefMessage
import android.nfc.tech.Ndef

interface RNFCAction {
    var success: Boolean
    fun perform(ndef: Ndef) : NdefMessage?
}