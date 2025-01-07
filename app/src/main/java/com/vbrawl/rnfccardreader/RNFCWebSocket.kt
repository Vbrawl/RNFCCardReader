package com.vbrawl.rnfccardreader

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class RNFCWebSocket(val url: String, val reconnectionInterval: Int = 3, val onmsg: (msg: String) -> Unit = {}) : WebSocketListener() {
    val httpClient = OkHttpClient()
    var sock: WebSocket? = null

    init {
        connect()
    }

    private fun connect() {
        val request = Request.Builder().url(url).build()

        sock = httpClient.newWebSocket(request, this)
    }

    private fun attemptReconnect() {
        Thread.sleep(1000 * reconnectionInterval.toLong())
        connect()
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
        attemptReconnect()
    }
}