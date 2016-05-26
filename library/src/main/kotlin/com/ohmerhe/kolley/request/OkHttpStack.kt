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

import com.android.volley.toolbox.HurlStack
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.OkUrlFactory

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * copy from https://gist.github.com/JakeWharton/5616899
 * An [HttpStack][com.android.volley.toolbox.HttpStack] implementation which
 * uses OkHttp as its transport.
 */
class OkHttpStack @JvmOverloads constructor(client: OkHttpClient = OkHttpClient()) : HurlStack() {
    private val mFactory: OkUrlFactory

    init {
        mFactory = OkUrlFactory(client)
    }

    @Throws(IOException::class)
    override fun createConnection(url: URL): HttpURLConnection {
        return mFactory.open(url)
    }
}