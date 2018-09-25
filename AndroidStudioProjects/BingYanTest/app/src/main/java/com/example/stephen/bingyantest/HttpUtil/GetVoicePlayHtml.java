package com.example.stephen.bingyantest.HttpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stephen on 17-7-21.
 */

public class GetVoicePlayHtml {
    public static String sendHttpRequest(String url) {
        //定义一个缓冲字符输入流
        HttpURLConnection connection=null;
        BufferedReader buff=null;
        try {
            URL resultUrl = new URL(url);
            connection = null;
            connection = (HttpURLConnection) resultUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            connection.setRequestProperty("Content-Type", "audio/mpeg");
            //connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            //connection.setRequestProperty("User-Agent", "application/x-java-serialized-object");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36");
            //connection.setRequestProperty("User-Agent","Chrome/59.0.3071.115");
            InputStream inputStream = connection.getInputStream();

            buff = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuffer res = new StringBuffer();
            String line = "";
            while ((line = buff.readLine()) != null) {
                res.append(line);
            }
            return res.toString();
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
            return "failed!";
        }finally {
            try{
                buff.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            if (connection!=null){
                connection.disconnect();
            }
        }
    }
}
