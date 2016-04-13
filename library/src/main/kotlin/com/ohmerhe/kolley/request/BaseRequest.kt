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
    private var _success: (D) -> Unit = {}
    private var _fail: (VolleyError) -> Unit = {}
    private var _finish: (() -> Unit) = {}
    private var _url: String = ""
    private var _type: Type = String::class.java
    protected val _params: MutableMap<String, String> = HashMap() // used for a POST or PUT request.

    protected val _headers: MutableMap<String, String> = HashMap()

    fun start(onStart: () -> Unit) {
        _start = onStart
    }

    fun fail(onError: (VolleyError) -> Unit) {
        _fail = onError
    }

    fun success(onSuccess: (D) -> Unit) {
        _success = onSuccess
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

    fun modelType(type: Type){
        _type = type
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
        _request = GsonRequest<D>(_method, url, _type, Response.ErrorListener {
            _fail(it)
            _finish()
        })
        _request._listener = Response.Listener {
            _success(it)
            _finish()
        }
        RequestManager.getRequestQueue(context).add(_request)
        _start()
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

private fun <D> request(method: Int, context: Context, request: BaseRequest<D>.() -> Unit): Request<D> {
    val baseRequest = BaseRequest<D>(context)
    baseRequest.method(method)
    baseRequest.request()
    baseRequest.excute()
    return baseRequest._request
}

fun <D> get(context: Context, request: BaseRequest<D>.() -> Unit): Request<D> = request(Request.Method.GET, context, request)

fun <D> post(context: Context, request: BaseRequest<D>.() -> Unit): Request<D> = request(Request.Method.POST, context,
        request)

fun <D> put(context: Context, request: BaseRequest<D>.() -> Unit): Request<D> = request(Request.Method.PUT, context,
        request)

fun <D> delete(context: Context, request: BaseRequest<D>.() -> Unit): Request<D> = request(Request.Method.DELETE, context,
        request)

fun <D> head(context: Context, request: BaseRequest<D>.() -> Unit): Request<D> = request(Request.Method.HEAD, context,
        request)

fun <D> options(context: Context, request: BaseRequest<D>.() -> Unit): Request<D> = request(Request.Method.OPTIONS,
        context,
        request)

fun <D> trace(context: Context, request: BaseRequest<D>.() -> Unit): Request<D> = request(Request.Method.TRACE, context,
        request)

fun <D> patch(context: Context, request: BaseRequest<D>.() -> Unit): Request<D> = request(Request.Method.PATCH, context,
        request)
