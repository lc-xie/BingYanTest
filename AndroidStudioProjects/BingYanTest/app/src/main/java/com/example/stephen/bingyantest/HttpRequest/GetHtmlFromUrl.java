package com.example.stephen.bingyantest.HttpRequest;

import android.widget.Toast;

import com.example.stephen.bingyantest.activity.MyApplication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stephen on 17-7-15.
 */

public class GetHtmlFromUrl {


    public static String sendRequest(String url){
        //定义一个缓冲字符输入流
        BufferedReader in = null;
        try {
            URL resultUrl = new URL(url);
            HttpURLConnection connection=null;
            connection=(HttpURLConnection)resultUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "application/x-java-serialized-object");
            InputStream inputStream = connection.getInputStream();//获取网页源码
            //将网页源码转换成String
            BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuffer res = new StringBuffer();
            String line = "";
            while((line = buff.readLine()) != null){
                res.append(line);
            }
            return res.toString();
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
            Toast.makeText(MyApplication.getContext(),"网络请求出错！",Toast.LENGTH_SHORT).show();
            return null;
        }
        finally {
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
