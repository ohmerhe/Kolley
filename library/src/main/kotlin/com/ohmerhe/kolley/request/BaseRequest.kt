package com.ohmerhe.kolley.request

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.ohmerhe.kolley.BuildConfig
import java.lang.reflect.Type
import java.util.*

/**
 * Created by ohmer on 4/12/16.
 */
class BaseRequest<D>(val context: Context) {
    private var _method: Int = Request.Method.GET
    private var _start: (() -> Unit) = {}
    private var _finish: (() -> Unit) = {}
    private var _url: String = ""
    private var _type: Type = String::class.java
    private var _errorlistener: Response.ErrorListener? = null
    private var _listener: Response.Listener<D>? = null
    protected val _params: MutableMap<String, String> = HashMap() // used for a POST or PUT request.
    protected val _headers: MutableMap<String, String> = HashMap()

    fun start(onStart: () -> Unit) {
        //todo:
    }

    fun fail(onError: (VolleyError) -> Unit) {
        _errorlistener = Response.ErrorListener(onError)
    }

    fun success(onSuccess: (D) -> Unit) {
        _listener = Response.Listener(onSuccess)
    }

    fun finish(onFinished: () -> Unit) {
        //todo:
    }

    fun url(url: String){
        _url = url
    }

    fun params(makeParam: RequestPairs.() -> Unit) {
        val requestPair = RequestPairs()
        requestPair.makeParam()
        _params.put(requestPair.pair!!)
    }

    fun headers(makeHeader: RequestPairs.() -> Unit) {
        val requestPair = RequestPairs()
        requestPair.makeHeader()
        _headers.put(requestPair.pair!!)
    }

    fun excute(){
        val request = GsonRequest<D>(_method,_url,_type,_errorlistener)
        request._start = _start
        request._finish = _finish
        request._listener = _listener
        RequestManager.getRequestQueue(context).add(request)
    }

    private fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(this.javaClass.simpleName, msg)
        }
    }
}

fun <K,V> MutableMap<K,V>.put(pair: Pair<K, V>){
    this.put(pair.first, pair.second)
}

class RequestPairs {
    var pair: Pair<String, String>? = null
    operator fun String.minus(value: String) {
        pair = Pair(this, value)
    }
}

fun <D> request(context: Context, request: BaseRequest<D>.() -> Unit): BaseRequest<D> {
    val baseRequest = BaseRequest<D>(context)
    baseRequest.request()
    baseRequest.excute()
    return baseRequest
}