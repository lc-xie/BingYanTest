package com.example.stephen.bingyantest.HttpUtil;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by stephen on 18-9-1.
 * OkHttp 包装类
 */

public class OkHttpUtil {

    private static final String TAG = OkHttpUtil.class.getSimpleName();

    //全局Client
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    // 同步 执行一个Request
    public static Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    // 异步 执行一个Request
    public static void enqueue(Request request, Callback callback) {
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    // 同步 发送GET请求，获取网络请求响应体
    public static String sendGETRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "application/x-java-serialized-object")
                .build();
        Response response = null;
        try {
            response = execute(request);
            if (response.isSuccessful()) {
                InputStream inputStream = response.body().byteStream();
                return inputStreamToString(inputStream);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 异步 发送GET请求，获取网络请求响应体
    public static void sendGETRequestEnqueue(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "application/x-java-serialized-object")
                .build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    // 同步 发送POST请求，获取响应体
    public static String sendPOSTRequest(String url, RequestBody body) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "application/x-java-serialized-object")
                .post(body)
                .build();
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return inputStreamToString(response.body().byteStream());
            }
        } catch (IOException e) {
            Log.e(TAG, "POST 网络请求出错");
            e.printStackTrace();
        }
        Log.e(TAG, "请求失败，返回值：" + response.code());
        return null;
    }

    // 异步 发送POST请求，获取响应体
    public static void sendPOSTRequestEnqueue(String url, RequestBody body, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "application/x-java-serialized-object")
                .post(body)
                .build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    // InputStream 转换成String
    public static String inputStreamToString(InputStream inputStream) throws IOException {
        //将网页源码转换成String
        BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        StringBuffer res = new StringBuffer();
        String line = "";
        while ((line = buff.readLine()) != null) {
            res.append(line);
        }
        return res.toString();
    }

}
