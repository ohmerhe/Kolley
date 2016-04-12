package com.ohmerhe.kolley.http

import android.util.Log

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.ohmerhe.kolley.BuildConfig

import org.apache.http.protocol.HTTP

import java.io.UnsupportedEncodingException
import java.lang.reflect.Type

/**
 * Created by ohmer on 9/20/15.
 */
class GsonRequest<T>
/**
 * Make a GET request and return a parsed object from JSON.

 * @param url URL of the request to make
 * *
 * @param typeOfT Relevant class object, for Gson's reflection
 * *
 * @param headers Map of request headers
 */
(mothod: Int, url: String, private val mType: Type, private val headers: Map<String, String>?, private val mParams: Map<String, String> // used for a POST or PUT request.
 ,
 private var listener: Response.Listener<T>?, errorListener: Response.ErrorListener) : Request<T>(mothod, url, errorListener) {
    private val gson = GsonBuilder().create()

    init {
        log("request url = " + url + " , params =  " + mParams.toString())
    }

    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        return headers ?: super.getHeaders()
    }

    override fun deliverResponse(response: T) {
        listener!!.onResponse(response)
    }

    /**
     * Returns a Map of parameters to be used for a POST or PUT request.
     * @return
     */
    public override fun getParams(): Map<String, String> {
        return mParams
    }

    fun setListener(listener: Response.Listener<T>) {
        this.listener = listener
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<T> {
        try {
            val json = response.data.toString()
            log("" + "response data = " + json)
            return Response.success(
                    gson.fromJson<Any>(json, mType) as T,
                    HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Log.e(this.javaClass.simpleName, e.toString())
            return Response.error<T>(ParseError(e))
        } catch (e: JsonSyntaxException) {
            Log.e(this.javaClass.simpleName, e.toString())
            return Response.error<T>(ParseError(e))
        }

    }

    private fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(this.javaClass.simpleName, msg)
        }
    }
}
