package com.example.stephen.bingyantest.imageThreeCache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by stephen on 17-7-10.
 * 缓存
 */

public class LruCacheHelper extends LruCache<String, Bitmap> {

    /*
     * key = imageName, not imageUrl!
     */

    public LruCacheHelper(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getHeight() * value.getRowBytes();
    }
}
