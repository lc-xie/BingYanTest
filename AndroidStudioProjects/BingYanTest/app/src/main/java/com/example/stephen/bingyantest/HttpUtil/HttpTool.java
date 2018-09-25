package com.example.stephen.bingyantest.HttpUtil;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.stephen.bingyantest.bean.Book;
import com.example.stephen.bingyantest.imageThreeCache.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stephen on 17-7-15.
 * 根据url，获取网页源码
 */

@Deprecated
public class HttpTool {

    private static final String TAG = HttpTool.class.getSimpleName();

    /**
     * 发送网络请求，获取源码，by HttpUrlConnection
     *
     * @param url 链接
     * @return 网页源码
     */
    public static String getHtmlByHttpUrlConnection(String url) {
        //定义一个缓冲字符输入流
        BufferedReader in = null;
        try {
            URL resultUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) resultUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "application/x-java-serialized-object");
            InputStream inputStream = connection.getInputStream();//获取网页源码
            return OkHttpUtil.inputStreamToString(inputStream);
        } catch (Exception e) {
            Log.e(TAG, "发送GET请求出现异常！" + e);
            e.printStackTrace();
            // 网络请求不在主线程，不能打Toast
            // Toast.makeText(MyApplication.getContext(), "网络请求出错！", Toast.LENGTH_SHORT).show();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    // 同步发送网络请求，获取源码， by OkHttp
    public static String getHtmlByOkHttp(String url) {
        return OkHttpUtil.sendGETRequest(url);
    }

}
