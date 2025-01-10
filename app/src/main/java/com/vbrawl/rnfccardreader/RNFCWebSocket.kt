package com.vbrawl.rnfccardreader

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class RNFCWebSocket(var url: String, val reconnectionInterval: Int = 1, val onmsg: (msg: String) -> Unit = {}) : WebSocketListener() {
    var httpClient: OkHttpClient
    var sock: WebSocket? = null
    var reconnect = false

    init {
        httpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .pingInterval(1, TimeUnit.SECONDS)
            .build()

        connect()
    }

    fun connect(new_url: String? = null) {
        if(new_url != null) { url = new_url }
        val request = Request.Builder().url(url).build()

        sock = httpClient.newWebSocket(request, this)
        reconnect = true
    }

    fun attemptReconnect() {
        if(!reconnect) { return }
        Thread.sleep(1000 * reconnectionInterval.toLong())
        if(!reconnect) { return }
        connect()
    }

    fun disconnect() {
        reconnect = false
        sock?.close(1000, "Closing")
        sock = null
    }

    fun send(text: String) {
        sock?.send(text)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        t.printStackTrace()
        attemptReconnect()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        onmsg(text)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        reconnect = false
    }
}