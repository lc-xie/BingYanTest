package com.example.stephen.bingyantest.imageThreeCache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stephen on 17-7-11.
 * 从网络获取图片类
 */

@Deprecated
public class ImageHttpClient {

    /**
     * get image from internet by image url
     *
     * @param urlStr
     * @return
     */
    public static Bitmap getImageByHttpUrlConnection(String urlStr) {
        Bitmap bitmap = null;
        byte[] data;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");   //设置请求方法为GET
            conn.setReadTimeout(5 * 1000);    //设置请求过时时间为5秒
            InputStream inputStream = conn.getInputStream();   //通过输入流获得图片数据
            data = readInputStream(inputStream);     //获得图片的二进制数据
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  //生成位图
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("DownloadImage", "获取网络数据失败！");
        }
        if (bitmap == null) Log.d("DownloadImage", "image 下载失败！");
        return bitmap;
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
