package com.ohmerhe.kolley.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.util.LruCache

import com.android.volley.toolbox.ImageLoader
import com.jakewharton.disklrucache.DiskLruCache
import com.ohmerhe.kolley.BuildConfig

import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.ref.SoftReference
import java.util.Collections
import java.util.HashSet

/**
 * Created by ohmer on 1/12/16.
 */
class LRUCache
/**
 * @param imageLoaderConfig
 */
(private val mImageLoaderConfig: ImageLoaderConfig) : ImageLoader.ImageCache {
    private val mCache: LruCache<String, Bitmap>
    private var mDiskLruCache: DiskLruCache? = null
    private val mDiskCacheLock = Object()
    private var mDiskCacheStarting = true
    private var mReusableBitmaps: MutableSet<SoftReference<Bitmap>>? = null

    init {

        // If we're running on Honeycomb or newer, create a set of reusable bitmaps that can be
        // populated into the inBitmap field of BitmapFactory.Options. Note that the set is
        // of SoftReferences which will actually not be very effective due to the garbage
        // collector being aggressive clearing Soft/WeakReferences. A better approach
        // would be to use a strongly references bitmaps, however this would require some
        // balancing of memory usage between this set and the bitmap LruCache. It would also
        // require knowledge of the expected size of the bitmaps. From Honeycomb to JellyBean
        // the size would need to be precise, from KitKat onward the size would just need to
        // be the upper bound (due to changes in how inBitmap can re-use bitmaps).
        if (Utils.hasHoneycomb()) {
            mReusableBitmaps = Collections.synchronizedSet(HashSet<SoftReference<Bitmap>>())
        }

        // Stored in kilobytes as LruCache takes an int in LruCache constructor.
        mCache = object : LruCache<String, Bitmap>(mImageLoaderConfig.memoryCacheSize / 1024) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than number of items.
                val bitmapSize = Utils.getBitmapSize(bitmap) / 1024
                return if (bitmapSize == 0) 1 else bitmapSize
            }
        }
        InitDiskCacheTask().execute()
    }

    /**
     * Initializes the disk cache.  Note that this includes disk access so this should not be
     * executed on the main/UI thread. By default an ImageCache does not initialize the disk
     * cache when it is created, instead you should call initDiskCache() to initialize it on a
     * background thread.
     */
    private fun initDiskCache() {
        // Set up disk cache
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache == null || mDiskLruCache!!.isClosed) {
                val diskCacheDir = mImageLoaderConfig.diskCacheDir
                if (mImageLoaderConfig.diskCacheEnabled && diskCacheDir != null) {
                    if (!diskCacheDir.exists()) {
                        diskCacheDir.mkdirs()
                    }
                    if (Utils.getUsableSpace(diskCacheDir) > mImageLoaderConfig.diskCacheSize) {
                        try {
                            mDiskLruCache = DiskLruCache.open(
                                    diskCacheDir, 1, 1, mImageLoaderConfig.diskCacheSize.toLong())
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "Disk cache initialized")
                            }
                        } catch (e: IOException) {
                            Log.e(TAG, "initDiskCache - " + e)
                        }

                    }
                }
            }
            mDiskCacheStarting = false
            mDiskCacheLock.notifyAll()
        }
    }

    internal inner class InitDiskCacheTask : AsyncTask<File, Void, Void>() {
        override fun doInBackground(vararg params: File): Void? {
            initDiskCache()
            return null
        }
    }

    override fun getBitmap(url: String): Bitmap? {
        var bitmap = getBitmapFromMemCache(url)
        if (bitmap == null) {
            bitmap = getBitmapFromDiskCache(url)
        }
        return bitmap
    }

    override fun putBitmap(url: String?, bitmap: Bitmap?) {
        if (url == null || bitmap == null) {
            return
        }
        // Add to memory cache as before
        if (getBitmapFromMemCache(url) == null) {
            mCache.put(url, bitmap)
        }
        putBitmapToDiskCache(url, bitmap)
    }

    private fun putBitmapToDiskCache(url: String, bitmap: Bitmap) {
        //start add to disk cache
        synchronized (mDiskCacheLock) {
            // Add to disk cache
            if (mDiskLruCache != null) {
                val key = Utils.hashKeyForDisk(url)
                var out: OutputStream? = null
                try {
                    val snapshot = mDiskLruCache!!.get(key)
                    if (snapshot == null) {
                        val editor = mDiskLruCache!!.edit(key)
                        if (editor != null) {
                            out = editor.newOutputStream(DISK_CACHE_INDEX)
                            bitmap.compress(
                                    mImageLoaderConfig.compressFormat, mImageLoaderConfig.compressQuality, out)
                            editor.commit()
                            out!!.close()
                        }
                    } else {
                        snapshot.getInputStream(DISK_CACHE_INDEX).close()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "addBitmapToCache - " + e)
                } catch (e: Exception) {
                    Log.e(TAG, "addBitmapToCache - " + e)
                } finally {
                    try {
                        if (out != null) {
                            out.close()
                        }
                    } catch (e: IOException) {
                    }

                }
            }
        }
    }

    private fun getBitmapFromMemCache(key: String): Bitmap? {
        return mCache.get(key)
    }

    private fun getBitmapFromDiskCache(url: String): Bitmap? {
        val key = Utils.hashKeyForDisk(url)
        var bitmap: Bitmap? = null

        synchronized (mDiskCacheLock) {
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait()
                } catch (e: InterruptedException) {
                }

            }
            if (mDiskLruCache != null) {
                var inputStream: InputStream? = null
                try {
                    val snapshot = mDiskLruCache!!.get(key)
                    if (snapshot != null) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Disk cache hit")
                        }
                        inputStream = snapshot.getInputStream(DISK_CACHE_INDEX)
                        if (inputStream != null) {
                            val fd = (inputStream as FileInputStream).fd

                            // Decode bitmap, but we don't want to sample so give
                            // MAX_VALUE as the target dimensions
                            bitmap = Utils.decodeSampledBitmapFromDescriptor(
                                    fd, Integer.MAX_VALUE, Integer.MAX_VALUE, this)
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "getBitmapFromDiskCache - " + e)
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close()
                        }
                    } catch (e: IOException) {
                    }

                }
            }
            return bitmap
        }
    }

    /**
     * @param options - BitmapFactory.Options with out* options populated
     * *
     * @return Bitmap that case be used for inBitmap
     */
    internal fun getBitmapFromReusableSet(options: BitmapFactory.Options): Bitmap? {
        //BEGIN_INCLUDE(get_bitmap_from_reusable_set)
        var bitmap: Bitmap? = null

        if (mReusableBitmaps != null && !mReusableBitmaps!!.isEmpty()) {
            synchronized (mReusableBitmaps!!) {
                val iterator = mReusableBitmaps!!.iterator()
                var item: Bitmap?

                while (iterator.hasNext()) {
                    item = iterator.next().get()

                    if (null != item && item.isMutable) {
                        // Check to see it the item can be used for inBitmap
                        if (Utils.canUseForInBitmap(item, options)) {
                            bitmap = item

                            // Remove from reusable set so it can't be used again
                            iterator.remove()
                            break
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove()
                    }
                }
            }
        }

        return bitmap
        //END_INCLUDE(get_bitmap_from_reusable_set)
    }

    companion object {
        private val TAG = "LRUCache"

        // Compression settings when writing images to disk cache
        private val DISK_CACHE_INDEX = 0
    }

}
