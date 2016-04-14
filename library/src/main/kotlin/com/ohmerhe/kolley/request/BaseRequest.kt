package com.ohmerhe.kolley.request

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.ohmerhe.kolley.BuildConfig
import java.util.*

/**
 * Created by ohmer on 4/14/16.
 */
class ByteRequest(method: Int, url: String, errorListener: Response.ErrorListener? = Response.ErrorListener {})
: BaseRequest<ByteArray>(method, url, errorListener) {
    override fun parseNetworkResponse(response: NetworkResponse?): Response<ByteArray>? {
        return Response.success(response?.data, HttpHeaderParser.parseCacheHeaders(response))
    }
}

abstract class BaseRequest<D>(method: Int, url: String, errorListener: Response.ErrorListener? = Response.ErrorListener {})
: Request<D>(method, url, errorListener) {
    protected val DEFAULT_CHARSET = "UTF-8"

    internal var _listener: Response.Listener<D>? = null
    protected val _params: MutableMap<String, String> = HashMap() // used for a POST or PUT request.

    /**
     * Returns a Map of parameters to be used for a POST or PUT request.
     * @return
     */
    public override fun getParams(): MutableMap<String, String> {
        return _params
    }

    override fun deliverResponse(response: D?) {
        _listener?.onResponse(response)
    }

    protected fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(this.javaClass.simpleName, msg)
        }
    }
}

