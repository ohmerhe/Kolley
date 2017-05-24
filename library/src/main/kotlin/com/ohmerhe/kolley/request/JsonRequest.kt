/*
 * Copyright (c) 2017  Ohmer.
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

import com.android.volley.Response
import com.android.volley.VolleyLog
import java.io.UnsupportedEncodingException

/**
 * Created by ohmer on 5/23/17.
 */
class JsonRequest(method: Int, url: String,val requestBody: String, errorListener: Response.ErrorListener?)
    : ByteRequest(method, url, errorListener) {

    /** Default charset for JSON request.  */
    private val PROTOCOL_CHARSET = "utf-8"

    /** Content type for request.  */
    private val PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET)

    /**
     * Returns the raw POST body to be sent.
     *
     * @throws AuthFailureError In the event of auth failure
     *
     * @deprecated Use {@link #getBody()} instead.
     */
    @Deprecated("", ReplaceWith("getBody()"))
    override fun getPostBody(): ByteArray? {
        return getBody()
    }

    override fun getBodyContentType(): String {
        return PROTOCOL_CONTENT_TYPE
    }

    override fun getBody(): ByteArray? {
        try {
            return requestBody.toByteArray(charset(PROTOCOL_CHARSET))
        } catch (uee: UnsupportedEncodingException) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    requestBody, PROTOCOL_CHARSET)
            return null
        }

    }
}