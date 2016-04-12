package com.ohmerhe.kolley.request

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.ohmerhe.kolley.request.OkHttpStack
import com.squareup.okhttp.OkHttpClient

/**
 * Created by ohmer on 3/26/16.
 */
object RequestManager {
    private var mRequestQueue: RequestQueue? = null

    fun getRequestQueue(context: Context): RequestQueue {
        if (mRequestQueue == null) {
            // Set up the network to use OKHttpURLConnection as the HTTP client.
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.applicationContext, OkHttpStack(OkHttpClient()))
        }
        return mRequestQueue as RequestQueue
    }
}