package com.example.stephen.bingyantest.imageThreeCache;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.activity.MyApplication;
import com.example.stephen.bingyantest.volley.VolleyHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by stephen on 17-7-11.
 * 图片工具类，三级缓存逻辑体现处（内存-本地-网络）
 * 此处应使用单例模式(待修改)
 */

public class ImageTool {

    private static LruCacheHelper lruCacheHelper;//缓存根据类
    private static FileUtil fileUtilHelper;//文贾管理辅助类
    private VolleyHelper volleyHelper;
    //private ImageCallBack imageCallBack;

    public ImageTool(LruCacheHelper lruCacheHelper,FileUtil fileUtilHelper){
        this.lruCacheHelper=lruCacheHelper;
        this.fileUtilHelper=fileUtilHelper;
        volleyHelper = VolleyHelper.getVolleyInstance(MyApplication.getContext());
    }

    /**
     * 从缓存和文件中拿图片
     */
    public Bitmap getBitmapFromFileOrCache(String url) {
        Bitmap bitmap;
        String imageName=url.substring(url.lastIndexOf(File.separator)+1);
        if (lruCacheHelper.get(imageName)!=null){
            return lruCacheHelper.get(imageName);           // 缓存命中
        }
        try {
            if(fileUtilHelper.getImageFromStorage(imageName)!=null){
                bitmap =fileUtilHelper.getImageFromStorage(imageName);
                lruCacheHelper.put(imageName, bitmap);      // 加入内存缓存
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过ImageRequest加载图片
     * @param imageCallBack 加载成功回调
     * @throws IOException
     */
    public void downloadBitmapByImageRequest(final String url, final ImageCallBack imageCallBack)throws IOException {
        final String imageName=url.substring(url.lastIndexOf(File.separator)+1);
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageCallBack.imageLoadded(response, url);
                fileUtilHelper.putImageToStorage(imageName, response);
                lruCacheHelper.put(imageName, response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_4444, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MyApplication.getContext(), "图片加载失败！", Toast.LENGTH_SHORT).show();
            }
        });
        volleyHelper.addRequest(imageRequest);
    }

    /**
     * 通过ImageLoader加载图片
     * @param listener 回调，这里由于要存储到本地文件夹，故自定义一个Listener
     */
    public void downloadBitmapByImageLoader(String url, ImageLoader.ImageListener listener) {
        ImageLoader imageLoader = volleyHelper.getImageLoader();
        imageLoader.get(url, listener);
    }

    public static class bookImageListener implements ImageLoader.ImageListener {

        private ImageView imageView;
        private String url;

        public bookImageListener(ImageView imageView, String url) {
            super();
            this.imageView = imageView;
            this.url = url;
        }

        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
            Bitmap bitmap1 = response.getBitmap();
            if (bitmap1 != null) {
                imageView.setImageBitmap(bitmap1);
                //保存到本地,不需要保存到缓存，imageLoader自带缓存功能
                String imageName=url.substring(url.lastIndexOf(File.separator)+1);
                fileUtilHelper.putImageToStorage(imageName, bitmap1);
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
