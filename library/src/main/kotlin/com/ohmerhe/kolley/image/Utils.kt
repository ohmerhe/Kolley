/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ohmerhe.kolley.image

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.os.StatFs
import android.os.StrictMode

import java.io.File
import java.io.FileDescriptor
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Class containing some static utility methods.
 */
object Utils {


    @TargetApi(VERSION_CODES.HONEYCOMB)
    fun enableStrictMode() {
        if (Utils.hasGingerbread()) {
            val threadPolicyBuilder = StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog()
            val vmPolicyBuilder = StrictMode.VmPolicy.Builder().detectAll().penaltyLog()

            if (Utils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen()
                //                vmPolicyBuilder
                //                        .setClassInstanceLimit(ImageGridActivity.class, 1)
                //                        .setClassInstanceLimit(ImageDetailActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build())
            StrictMode.setVmPolicy(vmPolicyBuilder.build())
        }
    }

    fun hasFroyo(): Boolean {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO
    }

    fun hasGingerbread(): Boolean {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD
    }

    fun hasHoneycomb(): Boolean {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB
    }

    fun hasHoneycombMR1(): Boolean {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1
    }

    fun hasJellyBean(): Boolean {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN
    }

    fun hasKitKat(): Boolean {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT
    }


    /**
     * Decode and sample down a bitmap from a file input stream to the requested width and height.

     * @param fileDescriptor The file descriptor to read from
     * *
     * @param reqWidth The requested width of the resulting bitmap
     * *
     * @param reqHeight The requested height of the resulting bitmap
     * *
     * @param cache The ImageCache used to find candidate bitmaps for use with inBitmap
     * *
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     * *         that are equal to or greater than the requested width and height
     */
    fun decodeSampledBitmapFromDescriptor(
            fileDescriptor: FileDescriptor, reqWidth: Int, reqHeight: Int, cache: LRUCache): Bitmap {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        // If we're running on Honeycomb or newer, try to use inBitmap
        if (Utils.hasHoneycomb()) {
            addInBitmapOptions(options, cache)
        }

        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    private fun addInBitmapOptions(options: BitmapFactory.Options, cache: LRUCache?) {
        // inBitmap only works with mutable bitmaps so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true

        if (cache != null) {
            // Try and find a bitmap to use for inBitmap
            val inBitmap = cache.getBitmapFromReusableSet(options)

            if (inBitmap != null) {
                options.inBitmap = inBitmap
            }
        }
    }

    /**
     * Calculate an inSampleSize for use in a [BitmapFactory.Options] object when decoding
     * bitmaps using the decode* methods from [BitmapFactory]. This implementation calculates
     * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
     * having a width and height equal to or larger than the requested width and height.

     * @param options An options object with out* params already populated (run through a decode*
     * *            method with inJustDecodeBounds==true
     * *
     * @param reqWidth The requested width of the resulting bitmap
     * *
     * @param reqHeight The requested height of the resulting bitmap
     * *
     * @return The value to be used for inSampleSize
     */
    fun calculateInSampleSize(options: BitmapFactory.Options,
                              reqWidth: Int, reqHeight: Int): Int {
        // BEGIN_INCLUDE (calculate_sample_size)
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            var totalPixels = (width * height / inSampleSize).toLong()

            // Anything more than 2x the requested pixels we'll sample down further
            val totalReqPixelsCap = reqWidth * reqHeight * 2.toLong()

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2
                totalPixels /= 2
            }
        }
        return inSampleSize
        // END_INCLUDE (calculate_sample_size)
    }

    /**
     * Get the size in bytes of a bitmap. Note that from Android 4.4 (KitKat)
     * onward this returns the allocated memory size of the bitmap which can be larger than the
     * actual bitmap data byte count (in the case it was re-used).

     * @param bitmap
     * *
     * @return size in bytes
     */
    @TargetApi(VERSION_CODES.KITKAT)
    fun getBitmapSize(bitmap: Bitmap): Int {
        // From KitKat onward use getAllocationByteCount() as allocated bytes can potentially be
        // larger than bitmap byte count.
        if (Utils.hasKitKat()) {
            return bitmap.allocationByteCount
        }

        if (Utils.hasHoneycombMR1()) {
            return bitmap.byteCount
        }

        // Pre HC-MR1
        return bitmap.rowBytes * bitmap.height
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
    // but if not mounted, falls back on internal storage.
    fun getDiskCacheDir(context: Context, uniqueName: String): File {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        val cachePath = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !isExternalStorageRemovable)
            getExternalCacheDir(context).path
        else
            context.cacheDir.path

        return File(cachePath + File.separator + uniqueName)
    }

    /**
     * Check if external storage is built-in or removable.

     * @return True if external storage is removable (like an SD card), false
     * * otherwise.
     */
    val isExternalStorageRemovable: Boolean
        @TargetApi(VERSION_CODES.GINGERBREAD)
        get() {
            if (hasGingerbread()) {
                return Environment.isExternalStorageRemovable()
            }
            return true
        }

    /**
     * Get the external app cache directory.

     * @param context The context to use
     * *
     * @return The external cache dir
     */
    @TargetApi(VERSION_CODES.FROYO)
    fun getExternalCacheDir(context: Context): File {
        if (hasFroyo()) {
            return context.externalCacheDir
        }

        // Before Froyo we need to construct the external cache dir ourselves
        val cacheDir = "/Android/data/" + context.packageName + "/cache/"
        return File(Environment.getExternalStorageDirectory().path + cacheDir)
    }

    /**
     * Check how much usable space is available at a given path.

     * @param path The path to check
     * *
     * @return The space available in bytes
     */
    @TargetApi(VERSION_CODES.GINGERBREAD)
    fun getUsableSpace(path: File): Long {
        if (hasGingerbread()) {
            return path.usableSpace
        }
        val stats = StatFs(path.path)
        return stats.blockSize.toLong() * stats.availableBlocks.toLong()
    }

    /**
     * @param candidate     - Bitmap to check
     * *
     * @param targetOptions - Options that have the out* value populated
     * *
     * @return true if `candidate` can be used for inBitmap re-use with
     * * `targetOptions`
     */
    @TargetApi(VERSION_CODES.KITKAT)
    internal fun canUseForInBitmap(
            candidate: Bitmap, targetOptions: BitmapFactory.Options): Boolean {
        //BEGIN_INCLUDE(can_use_for_inbitmap)
        if (!hasKitKat()) {
            // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
            return candidate.width == targetOptions.outWidth
                    && candidate.height == targetOptions.outHeight
                    && targetOptions.inSampleSize == 1
        }

        // From Android 4.4 (KitKat) onward we can re-use if the byte size of the new bitmap
        // is smaller than the reusable bitmap candidate allocation byte count.
        val width = targetOptions.outWidth / targetOptions.inSampleSize
        val height = targetOptions.outHeight / targetOptions.inSampleSize
        val byteCount = width * height * getBytesPerPixel(candidate.config)
        return byteCount <= candidate.allocationByteCount
        //END_INCLUDE(can_use_for_inbitmap)
    }

    /**
     * Return the byte usage per pixel of a bitmap based on its configuration.

     * @param config The bitmap configuration.
     * *
     * @return The byte usage per pixel.
     */
    internal fun getBytesPerPixel(config: Bitmap.Config): Int {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4
        } else if (config == Bitmap.Config.RGB_565) {
            return 2
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1
        }
        return 1
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     */
    fun hashKeyForDisk(key: String): String {
        val cacheKey: String
        try {
            val mDigest = MessageDigest.getInstance("MD5")
            mDigest.update(key.toByteArray())
            cacheKey = bytesToHexString(mDigest.digest())
        } catch (e: NoSuchAlgorithmException) {
            cacheKey = key.hashCode().toString()
        }

        return cacheKey
    }

    internal fun bytesToHexString(bytes: ByteArray): String {
        // http://stackoverflow.com/questions/332079
        val sb = StringBuilder()
        for (i in bytes.indices) {
            val hex = Integer.toHexString(0xFF and bytes[i].toInt())
            if (hex.length == 1) {
                sb.append('0')
            }
            sb.append(hex)
        }
        return sb.toString()
    }
}
