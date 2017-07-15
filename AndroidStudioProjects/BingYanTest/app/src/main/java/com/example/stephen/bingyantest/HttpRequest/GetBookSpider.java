package com.example.stephen.bingyantest.HttpRequest;

import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephen on 17-7-8.
 */

public class GetBookSpider {
    public static String sendRequest(String url){
        //定义一个字符串用来存储网页内容
        String result = "";
        //定义一个缓冲字符输入流
        BufferedReader in = null;
        try {
           /* URL resultUrl = new URL(url);
            URLConnection connection = resultUrl.openConnection();
            connection.connect();
            //初始化BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }*/
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
        return result;
    }

    //获取和Book相关的html,一个book放入一个list item中
    public static List<String> getBookHtmlList(String url){
        List<String> bookHtmlList=new ArrayList<>();
        Pattern p=Pattern.compile("<ul class=\"book-list\">(.*?)</ul>");
        Matcher m=p.matcher(url);
        String bookUrl;
        if (m.find()) {
            bookUrl = m.group();
            //Log.d("GetBookSpider:","bookUrl:"+bookUrl);
            Pattern p1 = Pattern.compile("<li>(.+?)</li>");
            Matcher m1 = p1.matcher(bookUrl);

            Boolean isFind = m1.find();
            while (isFind) {
                String temp = m1.group();
                //添加成功匹配的成果
                bookHtmlList.add(temp);
                //继续查找下一个对象
                isFind = m1.find();
            }
        }
        return bookHtmlList;
    }

}
