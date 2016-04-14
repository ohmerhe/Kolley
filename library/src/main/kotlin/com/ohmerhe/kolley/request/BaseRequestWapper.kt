package com.ohmerhe.kolley.request

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.ohmerhe.kolley.BuildConfig
import org.funktionale.partials.partially1
import java.lang.reflect.Type
import java.util.*

/**
 * Created by ohmer on 4/12/16.
 */
open class BaseRequestWapper(val context: Context) {
    internal lateinit  var _request: ByteRequest
    private var _method: Int = Request.Method.GET
    private var _start: (() -> Unit) = {}
    private var _success: (ByteArray) -> Unit = {}
    private var _fail: (VolleyError) -> Unit = {}
    private var _finish: (() -> Unit) = {}
    private var _url: String = ""
    private var _type: Type = String::class.java
    protected val _params: MutableMap<String, String> = HashMap() // used for a POST or PUT request.

    protected val _headers: MutableMap<String, String> = HashMap()
    private var _tag: Any? = null

    fun start(onStart: () -> Unit) {
        _start = onStart
    }

    fun fail(onError: (VolleyError) -> Unit) {
        _fail = onError
    }

    fun success(onSuccess: (ByteArray) -> Unit) {
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

    fun tag(tag: Any){
        _tag = tag
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
        _request = ByteRequest(_method, url, Response.ErrorListener {
            _fail(it)
            _finish()
        })
        _request._listener = Response.Listener {
            _success(it)
            _finish()
        }
        if(_tag != null) {
            _request.tag = _tag
        }
        RequestManager.getRequestQueue(context).add(_request)
        _start()
    }

    private fun getGetUrl(url: String, params: MutableMap<String, String>, toQueryString: (map: Map<String, String>) ->
    String): String {
        return if (params == null || params.isEmpty()) url else "$url?${toQueryString(params)}"
    }

    private fun <K, V> Map<K, V>.toQueryString(): String = this.map { "${it.key}=${it.value}" }.joinToString("&")
}

class RequestPairs {
    var pairs: MutableMap<String, String> = HashMap()
    operator fun String.minus(value: String) {
        pairs.put(this, value)
    }
}

val request: (Int, Context, BaseRequestWapper.() -> Unit) -> Request<ByteArray> = { method, context, request ->
    val baseRequest = BaseRequestWapper(context)
    baseRequest.method(method)
    baseRequest.request()
    baseRequest.excute()
    baseRequest._request
}

val get = request.partially1(Request.Method.GET)
val post = request.partially1(Request.Method.POST)
val put = request.partially1(Request.Method.PUT)
val delete = request.partially1(Request.Method.DELETE)
val head = request.partially1(Request.Method.HEAD)
val options = request.partially1(Request.Method.OPTIONS)
val trace = request.partially1(Request.Method.TRACE)
val patch = request.partially1(Request.Method.PATCH)

//fun <D> post(context: Context, request: BaseRequestWapper<D>.() -> Unit): Request<D> = request(Request.Method.POST, context,
//        request)
//
//fun <D> put(context: Context, request: BaseRequestWapper<D>.() -> Unit): Request<D> = request(Request.Method.PUT, context,
//        request)
//
//fun <D> delete(context: Context, request: BaseRequestWapper<D>.() -> Unit): Request<D> = request(Request.Method.DELETE, context,
//        request)
//
//fun <D> head(context: Context, request: BaseRequestWapper<D>.() -> Unit): Request<D> = request(Request.Method.HEAD, context,
//        request)
//
//fun <D> options(context: Context, request: BaseRequestWapper<D>.() -> Unit): Request<D> = request(Request.Method.OPTIONS,
//        context,
//        request)
//
//fun <D> trace(context: Context, request: BaseRequestWapper<D>.() -> Unit): Request<D> = request(Request.Method.TRACE, context,
//        request)
//
//fun <D> patch(context: Context, request: BaseRequestWapper<D>.() -> Unit): Request<D> = request(Request.Method.PATCH, context,
//        request)
