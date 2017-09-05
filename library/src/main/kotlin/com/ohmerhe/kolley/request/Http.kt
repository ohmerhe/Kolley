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
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.ohmerhe.kolley.upload.UploadRequest
import okhttp3.OkHttpClient
import org.funktionale.partials.partially1
import java.util.*
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.ClearableCookieJar



/**
 * Created by ohmer on 4/12/16.
 */
open class RequestWrapper {
    internal lateinit var _request: ByteRequest
    var method: Int = Request.Method.GET
    var url: String = ""
    var raw: String? = null // used for a POST or PUT request.
    var tag: Any? = null
    private var _start: (() -> Unit) = {}
    private var _success: (ByteArray) -> Unit = {}
    private var _fail: (VolleyError) -> Unit = {}
    private var _finish: (() -> Unit) = {}
    protected val _params: MutableMap<String, String> = mutableMapOf() // used for a POST or PUT request.
    protected val _fileParams: MutableMap<String, String> = mutableMapOf() // used for a POST or PUT request.
    protected val _headers: MutableMap<String, String> = mutableMapOf()
    

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

    val pairs = fun (map: MutableMap<String,String>, makePairs: RequestPairs.() -> Unit){
        val requestPair = RequestPairs()
        requestPair.makePairs()
        map.putAll(requestPair.pairs)
    }

    val params = pairs.partially1(_params)
    val headers = pairs.partially1(_headers)
    val files = pairs.partially1(_fileParams)

    fun excute() {
        var url = url
        if (Request.Method.GET == method) {
            url = getGetUrl(url, _params) { it.toQueryString() }
        }
        _request = getRequest(method, url, Response.ErrorListener {
            _fail(it)
            _finish()
        })
        _request._listener = Response.Listener {
            _success(it)
            _finish()
        }
        fillRequest()
        Http.getRequestQueue().add(_request)
        _start()
    }

    open fun fillRequest() {
        val request = _request
        if (tag != null) {
            request.tag = tag
        }
        // 添加 headers
        request.headers = _headers
        // 设置 params
        request.params = _params

        if (request is UploadRequest){
            request.fileParams = _fileParams
        }

    }

    open fun getRequest(method: Int, url: String, errorListener: Response.ErrorListener? = Response
            .ErrorListener {}): ByteRequest {
        return if (!raw.isNullOrEmpty() && method in Request.Method.POST..Request.Method.PUT) {
            JsonRequest(method, url, raw!!, errorListener)
        } else if (method == Request.Method.POST && _fileParams.isNotEmpty()) {
            UploadRequest(url, errorListener)
        }else{
            ByteRequest(method, url, errorListener)
        }
    }

    private fun getGetUrl(url: String, params: MutableMap<String, String>, toQueryString: (map: Map<String, String>) ->
    String): String {
        return if (params.isEmpty()) url else "$url?${toQueryString(params)}"
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
        val cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
        val okHttpClient = OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build()
        mRequestQueue = Volley.newRequestQueue(context.applicationContext, OkHttpStack(okHttpClient))
    }

    fun getRequestQueue(): RequestQueue {
        return mRequestQueue!!
    }

    val request: (Int, RequestWrapper.() -> Unit) -> Request<ByteArray> = { method, init ->
        val baseRequest =RequestWrapper()
        baseRequest.method = method
        baseRequest.init() // 执行闭包，完成数据填充
        baseRequest.excute() // 添加到执行队列，自动执行
        baseRequest._request // 用于返回
    }

    val get = request.partially1(Request.Method.GET)
    val post = request.partially1(Request.Method.POST)
    val put = request.partially1(Request.Method.PUT)
    val delete = request.partially1(Request.Method.DELETE)
    val head = request.partially1(Request.Method.HEAD)
    val options = request.partially1(Request.Method.OPTIONS)
    val trace = request.partially1(Request.Method.TRACE)
    val patch = request.partially1(Request.Method.PATCH)
    val upload = request.partially1(Request.Method.POST)
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
