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
open class ByteRequest(method: Int, url: String, errorListener: Response.ErrorListener? = Response.ErrorListener {})
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

