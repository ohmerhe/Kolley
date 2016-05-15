package com.ohmerhe.kolley.image

import android.graphics.Bitmap

import java.io.File

/**
 * Created by ohmer on 1/12/16.
 */
class ImageLoaderConfig private constructor(builder: ImageLoaderConfig.Builder) {
    internal val diskCacheDir: File?
    internal val memoryCacheEnabled: Boolean
    internal val diskCacheEnabled: Boolean
    internal val memoryCacheSize: Int
    internal val diskCacheSize: Int
    internal val compressQuality: Int
    internal val compressFormat: Bitmap.CompressFormat

    init {
        diskCacheDir = builder.diskCacheDir
        memoryCacheEnabled = builder.memoryCacheEnabled
        diskCacheEnabled = builder.diskCacheEnabled
        memoryCacheSize = builder.memoryCacheSize
        diskCacheSize = builder.diskCacheSize
        compressQuality = builder.compressQuality
        compressFormat = builder.compressFormat
    }


    class Builder {
         var memoryCacheSize = DETAULT_MEM_CACHE_SIZE
         var diskCacheSize = DETAULT_DISK_CACHE_SIZE
         var compressQuality = DEFAULT_COMPRESS_QUALITY
         var compressFormat = DEFAULT_COMPRESS_FORMAT
         var memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED
         var diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED
         var diskCacheDir: File? = null

        fun build(): ImageLoaderConfig {
            return ImageLoaderConfig(this)
        }
    }

    companion object {
        val DETAULT_DISK_CACHE_SIZE = 200 * 1024 * 1024
        val DETAULT_MEM_CACHE_SIZE = (Runtime.getRuntime().maxMemory() / 8).toInt()
        private val DEFAULT_MEM_CACHE_ENABLED = true
        private val DEFAULT_DISK_CACHE_ENABLED = true
        private val DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG
        private val DEFAULT_COMPRESS_QUALITY = 80
    }
}
