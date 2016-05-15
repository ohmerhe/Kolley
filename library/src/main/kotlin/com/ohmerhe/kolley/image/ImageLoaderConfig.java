package com.ohmerhe.kolley.image;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by ohmer on 1/12/16.
 */
public class ImageLoaderConfig {
    public final static int DETAULT_DISK_CACHE_SIZE = 200 * 1024 * 1024;
    public final static int DETAULT_MEM_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 8);
    private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
    private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
    private static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    private static final int DEFAULT_COMPRESS_QUALITY = 80;
    final File diskCacheDir;
    final boolean memoryCacheEnabled;
    final boolean diskCacheEnabled;
    final int memoryCacheSize;
    final int diskCacheSize;
    final int compressQuality;
    final Bitmap.CompressFormat compressFormat;

    private ImageLoaderConfig(Builder builder) {
        diskCacheDir = builder.diskCacheDir;
        memoryCacheEnabled = builder.memoryCacheEnabled;
        diskCacheEnabled = builder.diskCacheEnabled;
        memoryCacheSize = builder.memoryCacheSize;
        diskCacheSize = builder.diskCacheSize;
        compressQuality = builder.compressQuality;
        compressFormat = builder.compressFormat;
    }


    public static final class Builder {
        private int memoryCacheSize = DETAULT_MEM_CACHE_SIZE;
        private int diskCacheSize = DETAULT_DISK_CACHE_SIZE;
        private int compressQuality = DEFAULT_COMPRESS_QUALITY;
        private Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
        private boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
        private boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
        private File diskCacheDir;

        public Builder() {
        }

        public Builder memoryCacheSize(int val) {
            memoryCacheSize = val;
            return this;
        }

        public Builder diskCacheSize(int val) {
            diskCacheSize = val;
            return this;
        }

        public Builder compressQuality(int val) {
            compressQuality = val;
            return this;
        }

        public Builder compressFormat(Bitmap.CompressFormat val) {
            compressFormat = val;
            return this;
        }

        public Builder diskCacheDir(File val) {
            diskCacheDir = val;
            return this;
        }

        public Builder memoryCacheEnabled(boolean val) {
            memoryCacheEnabled = val;
            return this;
        }

        public Builder diskCacheEnabled(boolean val) {
            diskCacheEnabled = val;
            return this;
        }

        public ImageLoaderConfig build() {
            return new ImageLoaderConfig(this);
        }
    }
}
