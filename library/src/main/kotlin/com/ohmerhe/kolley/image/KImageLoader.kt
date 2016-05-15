package com.ohmerhe.kolley.image

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.widget.ImageView

import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.ohmerhe.kolley.request.OkHttpStack
import com.squareup.okhttp.OkHttpClient

/**
 * Created by ohmer on 1/12/16.
 */
object Image{
    private var mImageLoaderConfig = ImageLoaderConfig.Builder().build()
    private val mDefaultDisplayOption = ImageDisplayOption.Builder().build()
    private var mRequestQueue: RequestQueue? = null
    var imageLoader: ImageLoader? = null
        private set
    private var mImageCache: ImageLoader.ImageCache? = null

    fun config(context: Context, config: ImageLoaderConfig.Builder.() -> Unit = {}) {
        val builder = ImageLoaderConfig.Builder()
        builder.config()
        mImageLoaderConfig = builder.build()
        mRequestQueue = getRequestQueue(context.applicationContext)
        mImageCache = LRUCache(mImageLoaderConfig)
        imageLoader = ImageLoader(mRequestQueue, mImageCache)
    }

    private fun getRequestQueue(context: Context): RequestQueue {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context.applicationContext, OkHttpStack(OkHttpClient()))
        }
        return mRequestQueue!!
    }

    @JvmOverloads fun displayImage(url: String, imageView: ImageView?, displayOption: ImageDisplayOption? = mDefaultDisplayOption) {
        var displayOption = displayOption
        if (TextUtils.isEmpty(url) || imageView == null) {
            return
        }
        if (displayOption == null) {
            displayOption = mDefaultDisplayOption
        }
        val imageListener = ImageLoader.getImageListener(imageView, displayOption.imageResForEmptyUri, displayOption.imageResOnFail)
        imageLoader!!.get(url, imageListener, displayOption.maxWidth, displayOption.maxHeight, displayOption.scaleType)
    }

    /**
     * Issues a bitmap request with the given URL if that image is not available
     * in the cache, and returns a bitmap container that contains all of the data
     * relating to the request (as well as the default image if the requested
     * image is not available).
     * @param requestUrl The url of the remote image
     * *
     * @param imageListener The listener to call when the remote image is loaded
     * *
     * @param maxWidth The maximum width of the returned image.
     * *
     * @param maxHeight The maximum height of the returned image.
     * *
     * @param scaleType The ImageViews ScaleType used to calculate the needed image size.
     * *
     * @return A container object that contains all of the properties of the request, as well as
     * *     the currently available image (default if remote is not loaded).
     */
    @JvmOverloads fun loadImage(requestUrl: String, imageListener: ImageListener,
                                maxWidth: Int = 0, maxHeight: Int = 0, scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP): ImageLoader.ImageContainer {
        return imageLoader!!.get(requestUrl, object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer, isImmediate: Boolean) {
                if (response.bitmap != null) {
                    imageListener.onLoadSuccess(response.bitmap)
                }
            }

            override fun onErrorResponse(error: VolleyError) {
                imageListener.onLoadFailed()
            }
        }, maxWidth, maxHeight, scaleType)
    }

    interface ImageListener {
        fun onLoadSuccess(bitmap: Bitmap)
        fun onLoadFailed()
    }
}