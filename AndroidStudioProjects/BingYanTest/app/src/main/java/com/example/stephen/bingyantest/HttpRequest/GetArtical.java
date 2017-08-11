package com.example.stephen.bingyantest.HttpRequest;

import android.content.Intent;
import android.os.Environment;

import com.example.stephen.bingyantest.activity.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stephen on 17-7-6.
 */

public class GetArtical {
    private String title;
    private String author;
    private String content;
    private String randomArticalUrl;

    public static final String TODAY_ARTICAL="com.example.stephen.bingyantest.type.TODAY_ARTICAL";
    public static final String RANDOM_ARTICAL="com.example.stephen.bingyantest.type.RANDOM_ARTICAL";
    public static final String NET_FAULT="com.example.stephen.bingyantest.action.NET_FAULT";

    public String getRandomArticalUrl() {
        return randomArticalUrl;
    }

    public void setRandomArticalUrl(String randomArticalUrl) {
        this.randomArticalUrl = randomArticalUrl;
    }

    public GetArtical(String httpUrl,String articalType) {
        try{
            getData(httpUrl,articalType);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void getData(String httpUrl,String articalType)throws Exception{
        try {
            String html_all= GetHtmlFromUrl.sendRequest(httpUrl);
            if (html_all!=null){
                setTitle(getTitle(html_all));
                setAuthor(getAuthor(html_all));
                setContent(getContent(html_all));
                setRandomArticalUrl(getRandomArticalUrl(html_all));
                File dir=null;
                if (articalType==TODAY_ARTICAL){
                    dir=new File(Environment.getExternalStorageDirectory(),"/meiriyiwen/Artical/TodayArtical/");
                }else if (articalType==RANDOM_ARTICAL){
                    dir=new File(Environment.getExternalStorageDirectory(),"/meiriyiwen/Artical/RandomArtical/");
                }
                if (!dir.exists()) dir.mkdirs();
                String name=title+".txt";
                File file=new File(dir.getPath(),name);
                if (!file.exists()){
                    saveHtml(title,author,content,file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent netFaultIntent=new Intent(NET_FAULT);
            MyApplication.getContext().sendBroadcast(netFaultIntent);
            return;
        }
    }

    /**
     * 保存文本到文件夹
     */
    public void saveHtml(String title, String author,String content,File file){
        try {
            file.createNewFile();
            FileOutputStream fos=new FileOutputStream(file);
            byte[] data=(title+"\t\n").getBytes();
            fos.write(data);
            byte[] data1=(author+"\t\n").getBytes();
            fos.write(data1);
            byte[] data2=(content+"\t\n").getBytes();
            fos.write(data2);
            fos.close();
        } catch (IOException e) {
            System.out.println("Error at save artical...");
            e.printStackTrace();
        }
    }

    /*
  *获取文章内容
  */
    public String getContent(String html){
        Pattern p1=Pattern.compile("<p>(.*?)</p>");
        Matcher m1=p1.matcher(html);
        boolean ifFind=m1.find();
        String articalContent="";
        while(ifFind){
            String temp=m1.group(1);
            if (!temp.equals("")){
                articalContent=articalContent+"\t\t\t"+temp+"\n\n";
            }
            ifFind=m1.find();
        }
        return articalContent;
    }
    /*
        *获取文章标题
        */
    public String getTitle(String html){
        Pattern p=Pattern.compile("<h1>(.*?)</h1>");
        Matcher m=p.matcher(html);
        if(m.find()){
            return m.group(1);
        }
        return "获取标题失败！";
    }
    /*
    *获取文章作者
    */
    public String getAuthor(String html){
        Pattern p=Pattern.compile("\"article_author\"><span>(.*?)</span>");
        Matcher m=p.matcher(html);
        if(m.find()){
            return m.group(1);
        }
        return "获取作者失败！";
    }

    /*
    *获取随机文章的链接
    */
    public String getRandomArticalUrl(String html){
        Pattern p=Pattern.compile("class=\"random_article\" href=\"(.*?)\">随机文章</a>");
        Matcher m=p.matcher(html);
        String randomArticalUrl=null;
        if (m.find()) {
            randomArticalUrl = m.group(1);
        }
        return randomArticalUrl;
    }

}
