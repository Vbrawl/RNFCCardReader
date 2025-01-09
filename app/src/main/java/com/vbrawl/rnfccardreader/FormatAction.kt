package com.vbrawl.rnfccardreader

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA

open class FormatAction : RNFCAction {
    override var success = true
    override fun perform(tag: Tag): NdefMessage? {
        success = androidFormat(tag)
        if(!success) success = manualFormat(tag)

        return null
    }

    private fun androidFormat(tag: Tag): Boolean {
        var ret = true
        val ndef = NdefFormatable.get(tag)
        try {
            ndef?.connect()
            ndef?.format(null)
        } catch(e: Exception) {
            e.printStackTrace()
            ret = false
        } finally {
            ndef?.close()
        }
        return ret
    }

    /**
     * WARNING: Manual format creates a capability container with static
     * size of 128 bytes.
     */
    private fun manualFormat(tag: Tag): Boolean {
        try {
            val nfcA = NfcA.get(tag)
            nfcA.connect()

            nfcA.maxTransceiveLength

            val payloadErase = byteArrayOf(
                0xA2.toByte(), // WRITE
                0x03.toByte(), // Page 3
                // Capability container (mapping version 1.0, 128 bytes for data, read/write)
                0xE1.toByte(), 0x10.toByte(), 0x10.toByte(), 0x00.toByte(),
            )

            val payloadFormat = byteArrayOf(
                0xA2.toByte(), // WRITE
                0x04.toByte(), // Page 4
                // empty NDEF TLV and Terminator TLV
                0x03.toByte(), 0x00.toByte(), 0xFE.toByte(), 0x00.toByte()
            )

            nfcA.transceive(payloadErase)
            nfcA.transceive(payloadFormat)
            nfcA.close()
        } catch(e2: Exception) {
            e2.printStackTrace()
            return false
        }
        return true
    }
}