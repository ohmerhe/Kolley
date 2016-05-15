package com.ohmerhe.kolley.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.ohmerhe.kolley.request.OkHttpStack;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by ohmer on 1/12/16.
 */
public class EImageLoader {
    private ImageLoaderConfig mImageLoaderConfig = new ImageLoaderConfig.Builder().build();
    private ImageDisplayOption mDefaultDisplayOption = new ImageDisplayOption.Builder().build();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private ImageLoader.ImageCache mImageCache;
    private static EImageLoader sInstance = new EImageLoader();

    public static EImageLoader getInstance() {
        return sInstance;
    }

    public void init(Context context, ImageLoaderConfig imageLoaderConfig) {
        mImageLoaderConfig = imageLoaderConfig;
        mRequestQueue = getRequestQueue(context.getApplicationContext());
        mImageCache = new LRUCache(mImageLoaderConfig);
        mImageLoader = new ImageLoader(mRequestQueue, mImageCache);
    }

    private EImageLoader() {
    }

    private RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext(), new OkHttpStack(new OkHttpClient()));
        }
        return mRequestQueue;
    }

    public void displayImage(String url, ImageView imageView) {
        displayImage(url, imageView, mDefaultDisplayOption);
    }

    public void displayImage(String url, ImageView imageView, ImageDisplayOption displayOption) {
        if (TextUtils.isEmpty(url) || imageView == null){
            return;
        }
        if (displayOption == null) {
            displayOption = mDefaultDisplayOption;
        }
        ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(imageView, displayOption.getImageResForEmptyUri()
                , displayOption.getImageResOnFail());
        mImageLoader.get(url, imageListener, displayOption.getMaxWidth(), displayOption.getMaxHeight(), displayOption
                .getScaleType());
    }

    /**
     * Returns an ImageContainer for the requested URL.
     *
     * The ImageContainer will contain either the specified default bitmap or the loaded bitmap.
     * If the default was returned, the {@link ImageLoader} will be invoked when the
     * request is fulfilled.
     *
     * @param requestUrl The URL of the image to be loaded.
     */
    public ImageLoader.ImageContainer loadImage(String requestUrl, ImageListener imageListener) {
        return loadImage(requestUrl, imageListener, 0, 0);
    }

    /**
     * Equivalent to calling {@link #loadImage(String, ImageListener, int, int, ImageView.ScaleType)} with
     * {@code Scaletype == ScaleType.CENTER_CROP}.
     */
    public ImageLoader.ImageContainer loadImage(String requestUrl, ImageListener imageListener,
                                                int maxWidth, int maxHeight) {
        return loadImage(requestUrl, imageListener, maxWidth, maxHeight, ImageView.ScaleType.CENTER_CROP);
    }

    /**
     * Issues a bitmap request with the given URL if that image is not available
     * in the cache, and returns a bitmap container that contains all of the data
     * relating to the request (as well as the default image if the requested
     * image is not available).
     * @param requestUrl The url of the remote image
     * @param imageListener The listener to call when the remote image is loaded
     * @param maxWidth The maximum width of the returned image.
     * @param maxHeight The maximum height of the returned image.
     * @param scaleType The ImageViews ScaleType used to calculate the needed image size.
     * @return A container object that contains all of the properties of the request, as well as
     *     the currently available image (default if remote is not loaded).
     */
    public ImageLoader.ImageContainer loadImage(String requestUrl, final ImageListener imageListener,
                                                int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        return mImageLoader.get(requestUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    imageListener.onLoadSuccess(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                imageListener.onLoadFailed();
            }
        }, maxWidth, maxHeight, scaleType);
    }


    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public interface ImageListener{
        public void onLoadSuccess(Bitmap bitmap);
        public void onLoadFailed();
    }
}
