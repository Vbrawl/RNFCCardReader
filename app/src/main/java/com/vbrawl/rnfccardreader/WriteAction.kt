package com.vbrawl.rnfccardreader

import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.Ndef

open class WriteAction(private val payload: NdefMessage) : RNFCAction {
    override var success = true
    override fun perform(tag: Tag): NdefMessage? {
        success = true

        val ndef: Ndef? = Ndef.get(tag)
        try {
            ndef?.connect()
            ndef?.writeNdefMessage(payload)
        } catch (e: Exception) {
            success = false
            e.printStackTrace()
        } finally {
            ndef?.close()
        }
        return null
    }
}