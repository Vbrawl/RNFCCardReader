package com.vbrawl.rnfccardreader

import android.nfc.NdefMessage
import android.nfc.Tag

interface RNFCAction {
    var success: Boolean
    fun perform(tag: Tag) : NdefMessage?
}