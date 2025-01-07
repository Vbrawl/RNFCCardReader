package com.vbrawl.rnfccardreader

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import android.util.Base64

fun NdefMessage.toJson() : JsonArray {
    val msgArray = JsonArray()

    for(record in this.records) {
        val recObject = JsonObject()

        recObject.addProperty("tnf", record.tnf)
        recObject.addProperty("type", Base64.encodeToString(record.type, Base64.NO_WRAP))
        recObject.addProperty("id", Base64.encodeToString(record.id, Base64.NO_WRAP))
        recObject.addProperty("payload", Base64.encodeToString(record.payload, Base64.NO_WRAP))

        msgArray.add(recObject)
    }

    return msgArray
}

fun NdefMessage.toJsonString() : String {
    return this.toJson().toString()
}

fun JsonArray.toNdefMessage() : NdefMessage {
    val records = ArrayList<NdefRecord>()
    for(data in this) {
        val dataobj = data.asJsonObject
        val record = NdefRecord(
            dataobj["tnf"].asShort,
            Base64.decode(dataobj["type"].asString, Base64.NO_WRAP),
            Base64.decode(dataobj["id"].asString, Base64.NO_WRAP),
            Base64.decode(dataobj["payload"].asString, Base64.NO_WRAP))
        records.add(record)
    }

    return NdefMessage(records.toTypedArray())
}