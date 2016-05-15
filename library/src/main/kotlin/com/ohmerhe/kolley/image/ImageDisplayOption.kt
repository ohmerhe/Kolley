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
        var decodeConfig: Bitmap.Config = Bitmap.Config.RGB_565
        var scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP
        var maxWidth = DETAULT_IMAGE_WIDTH_MAX
        var maxHeight = DETAULT_IMAGE_HEIGHT_MAX

        fun build(): ImageDisplayOption {
            return ImageDisplayOption(this)
        }
    }

    companion object {
        val DETAULT_IMAGE_WIDTH_MAX = 1080
        val DETAULT_IMAGE_HEIGHT_MAX = 1260
    }
}
