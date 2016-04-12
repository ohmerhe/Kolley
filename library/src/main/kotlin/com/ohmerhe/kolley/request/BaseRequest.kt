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
    internal lateinit  var _request: GsonRequest<D>
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
        _start = onStart
    }

    fun fail(onError: (VolleyError) -> Unit) {
        _errorlistener = Response.ErrorListener(onError)
    }

    fun success(onSuccess: (D) -> Unit) {
        _listener = Response.Listener(onSuccess)
    }

    fun finish(onFinish: () -> Unit) {
        _finish = onFinish
    }

    fun url(url: String){
        _url = url
    }

    fun method(method: Int){
        _method = method
    }

    fun params(makeParam: RequestPairs.() -> Unit) {
        val requestPair = RequestPairs()
        requestPair.makeParam()
        _params.putAll(requestPair.pairs)
    }

    fun headers(makeHeader: RequestPairs.() -> Unit) {
        val requestPair = RequestPairs()
        requestPair.makeHeader()
        _headers.putAll(requestPair.pairs)
    }

    fun excute(){
        var url = _url
        if (Request.Method.GET == _method) {
            url = getGetUrl(_url, _params) { it.toQueryString() }
        }
        _request = GsonRequest<D>(_method, url, _type, _errorlistener)
        _request._start = _start
        _request._finish = _finish
        _request._listener = _listener
        RequestManager.getRequestQueue(context).add(_request)
    }

    private fun getGetUrl(url: String, params: MutableMap<String, String>, toQueryString: (map: Map<String, String>) ->
    String): String {
        return if (params == null || params.isEmpty()) url else "$url?${toQueryString(params)}"
    }

    private fun <K, V> Map<K, V>.toQueryString(): String = this.map { "${it.key}=${it.value}" }.joinToString("&")

    private fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(this.javaClass.simpleName, msg)
        }
    }
}

class RequestPairs {
    var pairs: MutableMap<String, String> = HashMap()
    operator fun String.minus(value: String) {
        pairs.put(this, value)
    }
}

fun <D> request(context: Context, method: Int = Request.Method.GET, request: BaseRequest<D>.() -> Unit): Request<D> {
    val baseRequest = BaseRequest<D>(context)
    baseRequest.method(method)
    baseRequest.request()
    baseRequest.excute()
    return baseRequest._request
}