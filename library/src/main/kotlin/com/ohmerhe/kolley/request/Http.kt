/*
 * Copyright (c) 2016  Ohmer.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ohmerhe.kolley.request

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.ohmerhe.kolley.BuildConfig
import com.squareup.okhttp.OkHttpClient
import org.funktionale.partials.partially1
import java.lang.reflect.Type
import java.util.*

/**
 * Created by ohmer on 4/12/16.
 */
open class BaseRequestWapper() {
    internal lateinit var _request: ByteRequest
    var url: String = ""
    var method: Int = Request.Method.GET
    private var _start: (() -> Unit) = {}
    private var _success: (ByteArray) -> Unit = {}
    private var _fail: (VolleyError) -> Unit = {}
    private var _finish: (() -> Unit) = {}
    protected val _params: MutableMap<String, String> = HashMap() // used for a POST or PUT request.

    protected val _headers: MutableMap<String, String> = HashMap()
    var tag: Any? = null

    fun onStart(onStart: () -> Unit) {
        _start = onStart
    }

    fun onFail(onError: (VolleyError) -> Unit) {
        _fail = onError
    }

    fun onSuccess(onSuccess: (ByteArray) -> Unit) {
        _success = onSuccess
    }

    fun onFinish(onFinish: () -> Unit) {
        _finish = onFinish
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

    fun excute() {
        var url = url
        if (Request.Method.GET == method) {
            url = getGetUrl(url, _params) { it.toQueryString() }
        }
        _request = ByteRequest(method, url, Response.ErrorListener {
            _fail(it)
            _finish()
        })
        _request._listener = Response.Listener {
            _success(it)
            _finish()
        }
        if (tag != null) {
            _request.tag = tag
        }
        Http.getRequestQueue().add(_request)
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

object Http {
    private var mRequestQueue: RequestQueue? = null
    fun init(context: Context) {
        // Set up the network to use OKHttpURLConnection as the HTTP client.
        // getApplicationContext() is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        mRequestQueue = Volley.newRequestQueue(context.applicationContext, OkHttpStack(OkHttpClient()))
    }

    fun getRequestQueue(): RequestQueue {
        return mRequestQueue!!
    }

    val request: (Int, BaseRequestWapper.() -> Unit) -> Request<ByteArray> = { method, request ->
        val baseRequest = BaseRequestWapper()
        baseRequest.method = method
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
}

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
