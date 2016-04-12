package com.ohmerhe.kolley.http

import com.android.volley.toolbox.HurlStack
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.OkUrlFactory

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * copy from https://gist.github.com/JakeWharton/5616899
 * An [HttpStack][com.android.volley.toolbox.HttpStack] implementation which
 * uses OkHttp as its transport.
 */
class OkHttpStack @JvmOverloads constructor(client: OkHttpClient? = OkHttpClient()) : HurlStack() {
    private val mFactory: OkUrlFactory

    init {
        if (client == null) {
            throw NullPointerException("Client must not be null.")
        }
        mFactory = OkUrlFactory(client)
    }

    @Throws(IOException::class)
    override fun createConnection(url: URL): HttpURLConnection {
        return mFactory.open(url)
    }
}