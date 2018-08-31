package com.example.stephen.bingyantest.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by stephen on 18-8-28.
 * Volley网络请求工具类
 */

public class VolleyHelper {

    public final static String REQUEST_TAG_ARTICLE = "stringRequestOfArticle";

    private RequestQueue requestQueue;
    private static volatile VolleyHelper volleyInstance;
    private ImageLoader imageLoader;

    private class BookImageCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> lruCache;

        public BookImageCache() {
            int maxSize = 10 * 1024 * 1024;
            lruCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            lruCache.put(url, bitmap);
        }

        @Override
        public Bitmap getBitmap(String url) {
            return lruCache.get(url);
        }
    }

    private VolleyHelper(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(requestQueue, new BookImageCache());
    }

    public static VolleyHelper getVolleyInstance(Context context) {
        if (volleyInstance == null) {
            synchronized (VolleyHelper.class) {
                if (volleyInstance == null) {
                    volleyInstance = new VolleyHelper(context);
                }
            }
        }
        return volleyInstance;
    }

    public void addRequest(Request request) {
        requestQueue.add(request);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}
