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

package com.ohmerhe.kolley.upload

import org.apache.http.util.EncodingUtils

import java.io.File

/**
 * Created by HaKr on 08/02/15.
 * Copy from https://github.com/DWorkS/VolleyPlus
 */
object MultipartUtils {

    val CRLF = "\r\n"
    val HEADER_CONTENT_TYPE = "Content-Type"
    val HEADER_USER_AGENT = "User-Agent"
    val HEADER_CONTENT_DISPOSITION = "Content-Disposition"
    val HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding"
    val CONTENT_TYPE_MULTIPART = "multipart/form-data; charset=%s; boundary=%s"
    val BINARY = "binary"
    val EIGHT_BIT = "8bit"
    val FORM_DATA = "form-data; name=\"%s\""
    val BOUNDARY_PREFIX = "--"
    val CONTENT_TYPE_OCTET_STREAM = "application/octet-stream"
    val FILENAME = "filename=\"%s\""
    val COLON_SPACE = ": "
    val SEMICOLON_SPACE = "; "
    val CONTENT_TYPE_TEXT_PLAIN = "text/plain"

    val CRLF_LENGTH = CRLF.toByteArray().size
    val HEADER_CONTENT_DISPOSITION_LENGTH = HEADER_CONTENT_DISPOSITION.toByteArray().size
    val COLON_SPACE_LENGTH = COLON_SPACE.toByteArray().size
    val HEADER_CONTENT_TYPE_LENGTH = HEADER_CONTENT_TYPE.toByteArray().size
    val CONTENT_TYPE_OCTET_STREAM_LENGTH = CONTENT_TYPE_OCTET_STREAM.toByteArray().size
    val HEADER_CONTENT_TRANSFER_ENCODING_LENGTH = HEADER_CONTENT_TRANSFER_ENCODING.toByteArray().size
    val BINARY_LENGTH = BINARY.toByteArray().size
    val BOUNDARY_PREFIX_LENGTH = BOUNDARY_PREFIX.toByteArray().size

    val CRLF_BYTES = EncodingUtils.getAsciiBytes(CRLF)

    fun getContentLengthForMultipartRequest(boundary: String, params: Map<String, String>, filesToUpload: Map<String,
            String>): Int {
        val boundaryLength = boundary.toByteArray().size
        var contentLength = 0
        for ((key, value) in params) {
            val size = boundaryLength +
                    CRLF_LENGTH + HEADER_CONTENT_DISPOSITION_LENGTH + COLON_SPACE_LENGTH + String.format(FORM_DATA, key).toByteArray().size +
                    CRLF_LENGTH + HEADER_CONTENT_TYPE_LENGTH + COLON_SPACE_LENGTH + CONTENT_TYPE_TEXT_PLAIN.toByteArray().size +
                    CRLF_LENGTH + CRLF_LENGTH + value.toByteArray().size + CRLF_LENGTH

            contentLength += size
        }

        for ((key, value) in filesToUpload) {
            val file = File(value)
            var size = boundaryLength +
                    CRLF_LENGTH + HEADER_CONTENT_DISPOSITION_LENGTH + COLON_SPACE_LENGTH + String.format(FORM_DATA + SEMICOLON_SPACE + FILENAME, key, file.name).toByteArray().size +
                    CRLF_LENGTH + HEADER_CONTENT_TYPE_LENGTH + COLON_SPACE_LENGTH + CONTENT_TYPE_OCTET_STREAM_LENGTH +
                    CRLF_LENGTH + HEADER_CONTENT_TRANSFER_ENCODING_LENGTH + COLON_SPACE_LENGTH + BINARY_LENGTH + CRLF_LENGTH + CRLF_LENGTH

            size += file.length().toInt()
            size += CRLF_LENGTH
            contentLength += size
        }

        val size = boundaryLength + BOUNDARY_PREFIX_LENGTH + CRLF_LENGTH
        contentLength += size
        return contentLength
    }

}
