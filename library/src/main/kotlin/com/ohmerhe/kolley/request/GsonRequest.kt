package com.ohmerhe.kolley.request

import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.ohmerhe.kolley.BuildConfig
import java.io.UnsupportedEncodingException
import java.lang.reflect.Type
import java.util.*

/**
 * Created by ohmer on 9/20/15.
 */
open class GsonRequest<D>
/**
 * Make a GET request and return a parsed object from JSON.

 * @param url URL of the request to make
 * *
 * @param typeOfT Relevant class object, for Gson's reflection
 * *
 * @param headers Map of request headers
 */
(method: Int
 , url: String
 , private val type: Type
 , errorListener: Response.ErrorListener? = Response.ErrorListener {}) : Request<D>(method, url, errorListener) {
    private val gson = GsonBuilder().create()
    internal var _listener: Response.Listener<D>? = null
    protected val _params: MutableMap<String, String> = HashMap() // used for a POST or PUT request.

    internal var _start: () -> Unit = {}
    internal var _finish: () -> Unit = {}

    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        return super.getHeaders()
    }

    override fun deliverResponse(response: D) {
        _listener?.onResponse(response)
    }

    /**
     * Returns a Map of parameters to be used for a POST or PUT request.
     * @return
     */
    public override fun getParams(): MutableMap<String, String> {
        return _params
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<D> {
        try {
            val json = response.data.toString()
            log("" + "response data = " + json)
            return Response.success(
                    gson.fromJson<D>(json, type),
                    HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Log.e(this.javaClass.simpleName, e.toString())
            return Response.error<D>(ParseError(e))
        } catch (e: JsonSyntaxException) {
            Log.e(this.javaClass.simpleName, e.toString())
            return Response.error<D>(ParseError(e))
        }

    }

    private fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(this.javaClass.simpleName, msg)
        }
    }
}