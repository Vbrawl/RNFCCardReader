package com.vbrawl.rnfccardreader

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.IntentCompat
import com.google.gson.JsonParser
import com.vbrawl.rnfccardreader.ui.theme.MainUI


class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var action: RNFCAction? = ReadAction()

    var sock: RNFCWebSocket? = null
    var url: String = "ws://127.0.0.1:8080"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainUI(this)
        }

        sock = RNFCWebSocket(url) { msg ->
            val obj = JsonParser.parseString(msg).asJsonObject
            val targetAction = obj.get("action").asString
            action = when(targetAction) {
                "READ" -> ReadAction()
                "WRITE" -> WriteAction(obj.get("message").asJsonArray.toNdefMessage())
                "FORMAT" -> FormatAction()
                else -> null
            }

            if(action == null) {
                Toast.makeText(this, "Invalid action! (${targetAction})", Toast.LENGTH_LONG).show()
            }
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if(nfcAdapter == null) {
            val toast = Toast.makeText(this, "Couldn't find an NFC adapter!", Toast.LENGTH_SHORT)
            toast.show()
        }
        else if(!nfcAdapter!!.isEnabled) {
            val toast = Toast.makeText(this, "NFC is turned off!", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    override fun onResume() {
        super.onResume()

        val intent = Intent(this, javaClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val tag = IntentCompat.getParcelableExtra(intent, NfcAdapter.EXTRA_TAG, Tag::class.java) ?: return

        if(action != null) {
            val ret = action!!.perform(tag)
            if(action!!.success) {
                sock?.send(ret?.toJsonString() ?: "{}")
                Toast.makeText(this,
                    when(ret) {
                        null -> "Success (No data)"
                        else -> ret.toJsonString()
                    },
                    Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show()
            }
        }
    }
}