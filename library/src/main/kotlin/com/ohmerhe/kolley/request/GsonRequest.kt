package com.ohmerhe.kolley.request

import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import java.io.UnsupportedEncodingException
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * Created by ohmer on 9/20/15.
 */
open class GsonRequest<D>(method: Int, url: String, private val type: Type
                          , errorListener: Response.ErrorListener? = Response.ErrorListener {}) : BaseRequest<D>(method, url,
        errorListener) {
    private val gson = GsonBuilder().create()

    override fun parseNetworkResponse(response: NetworkResponse): Response<D> {
        try {
            val json = response.data.toString(Charset.defaultCharset())
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
}