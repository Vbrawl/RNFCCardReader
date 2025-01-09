package com.vbrawl.rnfccardreader

import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.Ndef

class ReadAction : RNFCAction {
    override var success = true
    override fun perform(tag: Tag): NdefMessage? {
        success = true
        try {
            val ndef = Ndef.get(tag)
            ndef.connect()
            val message = ndef.ndefMessage
            ndef.close()
            return message
        } catch (e: Exception) {
            success = false
            e.printStackTrace()
        }

        return null
    }
}