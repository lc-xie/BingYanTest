package com.example.stephen.bingyantest.imageThreeCache;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.activity.MyApplication;
import com.example.stephen.bingyantest.HttpUtil.VolleyUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by stephen on 17-7-11.
 * 图片工具类，三级缓存逻辑体现处（内存-本地-网络）
 * 此处应使用单例模式(待修改)
 */

public class ImageTool {
    private LruCacheHelper lruCacheHelper;      // 缓存类lruCache
    private FileUtil fileUtilHelper;            // 文件管理辅助类
    private static ImageTool imageTool = null;

    private ImageTool(){
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        lruCacheHelper = new LruCacheHelper(mCacheSize);
        this.fileUtilHelper = new FileUtil();
    }

    public static ImageTool getInstance() {
        if (imageTool == null) {
            imageTool = new ImageTool();
        }
        return imageTool;
    }

    // 获取图片，三级加载（缓存-本地-网络）
    public void getBitmap(ImageView imageView, String url) {
        Bitmap bitmap = getBitmapFromFileOrCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        getBitmapByVolley(url, imageView);
    }

    // 缓存和本地加载
    public Bitmap getBitmapFromFileOrCache(String url) {
        Bitmap bitmap;
        String imageName=url.substring(url.lastIndexOf(File.separator)+1);
        if (lruCacheHelper.get(imageName)!=null){
            return lruCacheHelper.get(imageName);           // 缓存命中
        }
        // 缓存未命中
        try {
            if(fileUtilHelper.getImageFromFile(imageName)!=null){
                bitmap =fileUtilHelper.getImageFromFile(imageName);
                lruCacheHelper.put(imageName, bitmap);      // 加入内存缓存
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 网络加载图片 Volley
    public void getBitmapByVolley(String url, ImageView imageView) {
        ImageLoader imageLoader = VolleyUtil.getVolleyInstance(MyApplication.getContext()).getImageLoader();
        imageLoader.get(url, new BookImageListener(imageView, url));
    }

    public class BookImageListener implements ImageLoader.ImageListener {

        private ImageView imageView;
        private String url;

        public BookImageListener(ImageView imageView, String url) {
            super();
            this.imageView = imageView;
            this.url = url;
        }

        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
            Bitmap bitmap = response.getBitmap();
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                String imageName=url.substring(url.lastIndexOf(File.separator)+1);
                //保存到缓存和本地
                lruCacheHelper.put(imageName, bitmap);
                fileUtilHelper.saveImage(imageName, bitmap);
            } else {
                imageView.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.drawable.book_default,null));
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            imageView.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.drawable.book_error,null));
        }
    }

}
