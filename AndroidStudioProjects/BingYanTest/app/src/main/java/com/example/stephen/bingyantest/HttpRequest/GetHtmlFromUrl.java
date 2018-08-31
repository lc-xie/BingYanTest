package com.example.stephen.bingyantest.HttpRequest;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.stephen.bingyantest.activity.MyApplication;
import com.example.stephen.bingyantest.bean.Article;
import com.example.stephen.bingyantest.volley.VolleyHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stephen on 17-7-15.
 * 根据url，获取网页源码
 */

public class GetHtmlFromUrl {

    private static final String TAG = GetHtmlFromUrl.class.getSimpleName();
    public static final String NET_FAULT = "com.example.stephen.bingyantest.action.NET_FAULT";

    /**
     * 发送网络请求，获取源码，by HttpUrlConnection
     *
     * @param url 链接
     * @return 网页源码
     */
    public static String getHtmlByUrl(String url) {
        //定义一个缓冲字符输入流
        BufferedReader in = null;
        try {
            URL resultUrl = new URL(url);
            Log.e(TAG, url.toString());
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) resultUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "application/x-java-serialized-object");
            InputStream inputStream = connection.getInputStream();//获取网页源码
            //将网页源码转换成String
            BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuffer res = new StringBuffer();
            String line = "";
            while ((line = buff.readLine()) != null) {
                res.append(line);
            }
            //Log.e("----", res.toString());
            return res.toString();
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

}
