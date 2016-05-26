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

package com.ohmerhe.kolley.image

import android.graphics.Bitmap
import android.widget.ImageView

/**
 * Created by ohmer on 1/12/16.
 */
class ImageDisplayOption private constructor(builder: ImageDisplayOption.Builder) {
    val imageResOnLoading: Int
    val imageResForEmptyUri: Int
    val imageResOnFail: Int
    val decodeConfig: Bitmap.Config
    val scaleType: ImageView.ScaleType
    val maxWidth: Int
    val maxHeight: Int

    init {
        imageResOnLoading = builder.imageResOnLoading
        imageResForEmptyUri = builder.imageResForEmptyUri
        imageResOnFail = builder.imageResOnFail
        decodeConfig = builder.decodeConfig
        scaleType = builder.scaleType
        maxWidth = builder.maxWidth
        maxHeight = builder.maxHeight
    }


    class Builder {
        var imageResOnLoading: Int = 0
        var imageResForEmptyUri: Int = 0
        var imageResOnFail: Int = 0
        var decodeConfig = Bitmap.Config.RGB_565
        var scaleType = ImageView.ScaleType.CENTER_CROP
        var maxWidth = DETAULT_IMAGE_WIDTH_MAX
        var maxHeight = DETAULT_IMAGE_HEIGHT_MAX

        fun build(): ImageDisplayOption {
            return ImageDisplayOption(this)
        }
    }

    companion object {
        val DETAULT_IMAGE_WIDTH_MAX = 1080
        val DETAULT_IMAGE_HEIGHT_MAX = 1960
    }
}
