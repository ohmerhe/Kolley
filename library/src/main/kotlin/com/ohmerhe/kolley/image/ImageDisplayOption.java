package com.ohmerhe.kolley.image;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by ohmer on 1/12/16.
 */
public class ImageDisplayOption {
    public final static int DETAULT_IMAGE_WIDTH_MAX = 1080;
    public final static int DETAULT_IMAGE_HEIGHT_MAX = 1290;
    private final int imageResOnLoading;
    private final int imageResForEmptyUri;
    private final int imageResOnFail;
    private final Bitmap.Config decodeConfig;
    private final ImageView.ScaleType scaleType;
    private final int maxWidth;
    private final int maxHeight;

    private ImageDisplayOption(Builder builder) {
        imageResOnLoading = builder.imageResOnLoading;
        imageResForEmptyUri = builder.imageResForEmptyUri;
        imageResOnFail = builder.imageResOnFail;
        decodeConfig = builder.decodeConfig;
        scaleType = builder.scaleType;
        maxWidth = builder.maxWidth;
        maxHeight = builder.maxHeight;
    }


    public static final class Builder {
        private int imageResOnLoading;
        private int imageResForEmptyUri;
        private int imageResOnFail;
        private Bitmap.Config decodeConfig = Bitmap.Config.RGB_565;
        private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_CROP;
        private int maxWidth = DETAULT_IMAGE_WIDTH_MAX;
        private int maxHeight = DETAULT_IMAGE_HEIGHT_MAX;

        public Builder() {
        }

        public Builder imageResOnLoading(int val) {
            imageResOnLoading = val;
            return this;
        }

        public Builder imageResForEmptyUri(int val) {
            imageResForEmptyUri = val;
            return this;
        }

        public Builder imageResOnFail(int val) {
            imageResOnFail = val;
            return this;
        }

        public Builder decodeConfig(Bitmap.Config val) {
            decodeConfig = val;
            return this;
        }

        public Builder scaleType(ImageView.ScaleType val) {
            scaleType = val;
            return this;
        }

        public Builder maxWidth(int val) {
            maxWidth = val;
            return this;
        }

        public Builder maxHeight(int val) {
            maxHeight = val;
            return this;
        }

        public ImageDisplayOption build() {
            return new ImageDisplayOption(this);
        }
    }

    public int getImageResOnLoading() {
        return imageResOnLoading;
    }

    public int getImageResForEmptyUri() {
        return imageResForEmptyUri;
    }

    public int getImageResOnFail() {
        return imageResOnFail;
    }

    public Bitmap.Config getDecodeConfig() {
        return decodeConfig;
    }

    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}
