package com.vbrawl.rnfccardreader

import android.nfc.NdefMessage
import android.nfc.tech.Ndef

class ReadAction : RNFCAction {
    override var success = true
    override fun perform(ndef: Ndef): NdefMessage? {
        success = true
        try {
            ndef.connect()
            val message = ndef.cachedNdefMessage
            ndef.close()
            return message
        } catch (e: Exception) {
            success = false
            e.printStackTrace()
        }

        return null
    }
}