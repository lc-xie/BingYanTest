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
 * 从html源码中匹配出与book相关的部分，添加到一个list中
 */

public class GetBookSpider {

    //获取和Book相关的html,一个book放入一个list item中
    public static List<String> getBookHtmlList(String html){
        List<String> bookHtmlList=new ArrayList<>();
        Pattern p=Pattern.compile("<ul class=\"book-list\">(.*?)</ul>");
        Matcher m=p.matcher(html);
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
