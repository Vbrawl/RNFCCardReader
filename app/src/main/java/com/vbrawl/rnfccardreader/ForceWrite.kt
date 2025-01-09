package com.vbrawl.rnfccardreader

import android.nfc.NdefMessage
import android.nfc.Tag

class ForceWrite(payload: NdefMessage) : WriteAction(payload) {
    override var success = true

    override fun perform(tag: Tag): NdefMessage? {
        super.perform(tag)
        if(!success) {
            val fa = FormatAction()
            fa.perform(tag)
            if(fa.success) {
                super.perform(tag)
            }
        }
        return null
    }

}