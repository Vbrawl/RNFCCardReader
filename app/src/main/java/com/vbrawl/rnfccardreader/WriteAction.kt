package com.vbrawl.rnfccardreader

import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.Ndef

class WriteAction(private val payload: NdefMessage) : RNFCAction {
    override var success = true
    override fun perform(tag: Tag): NdefMessage? {
        success = true

        try {
            val ndef = Ndef.get(tag)
            ndef.connect()
            ndef.writeNdefMessage(payload)
            ndef.close()
        } catch (e: Exception) {
            success = false
            e.printStackTrace()
        }
        return null
    }
}