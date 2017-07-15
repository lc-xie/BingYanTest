package com.example.stephen.bingyantest.HttpRequest;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stephen on 17-7-6.
 */

public class GetArtical {
    private String title;
    private String author;
    private String content;

    public GetArtical() {
        try{
            getData();
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

    public void getData()throws Exception{
                String filepath = "/home/stephen/intelli/b.html";
                String url_str = "https://meiriyiwen.com/";
                URL url = null;
                try {
                    url = new URL(url_str);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                String charset = "utf-8";
                int sec_cont = 1000;
                try {
                    HttpURLConnection connection=null;
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setRequestProperty("User-Agent", "application/x-java-serialized-object");
                    InputStream htm_in = connection.getInputStream();//获取网页源码
                    String htm_str = InputStream2String(htm_in,charset);//截取文章部分源码
                    setTitle(getTitle(htm_str));
                    setAuthor(getAuthor(htm_str));
                    setContent(getContent(htm_str));
                } catch (IOException e) {
                    e.printStackTrace();
                }

    }

    //转换成String
    public static String InputStream2String(InputStream in_st, String charset) throws IOException{
        BufferedReader buff = new BufferedReader(new InputStreamReader(in_st, charset));
        StringBuffer res = new StringBuffer();
        String line = "";
        String lableFirst,lableLast;
        int lable=0;
        lableFirst="        <div id=\"article_show\">";
        lableLast="            </div>";
        while((line = buff.readLine()) != null){
            if(!line.equals(lableFirst)&&lable==0)continue;
            res.append(line);
            lable=1;
            if(line.equals(lableLast))lable=0;
        }
        return res.toString();
    }
    /**
     * 保存str到文件b.html
     */
    public void saveHtml(String filepath, String str){
        try {
            OutputStreamWriter outs = new OutputStreamWriter(new FileOutputStream(filepath, true), "utf-8");
            outs.write(str);
            //   System.out.print(str);
            outs.close();
        } catch (IOException e) {
            System.out.println("Error at save html...");
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
            articalContent=articalContent+"\t\t\t"+temp+"\n\n";
            ifFind=m1.find();
        }
        return articalContent;
    }
    /*
        *获取文章标题
        */
    public String getTitle(String html){
        Pattern p=Pattern.compile("<h1>");
        Matcher m=p.matcher(html);
        //System.out.print(htm_str+'\n');
        int addressFirst;
        boolean a=m.find();
        if(a){
            addressFirst=m.start();
            //System.out.print(addressFirst);
            int addressLast;
            Pattern p1=Pattern.compile("</h1>");
            Matcher m1=p1.matcher(html);
            boolean b=m1.find();
            if(b){
                addressLast=m1.start();
                //System.out.print(addressFirst);
                return  html.substring(addressFirst+4,addressLast);
                //System.out.print(title);
            }
        }
        return "获取标题失败！";
    }
    /*
    *获取文章标题
    */
    public String getAuthor(String html){
        Pattern p=Pattern.compile("<span>");
        Matcher m=p.matcher(html);
        //System.out.print(htm_str+'\n');
        int addressFirst;
        boolean a=m.find();
        if(a){
            addressFirst=m.start();
            //System.out.print(addressFirst);
            int addressLast;
            Pattern p1=Pattern.compile("</span>");
            Matcher m1=p1.matcher(html);
            boolean b=m1.find();
            if(b){
                addressLast=m1.start();
                //System.out.print(addressFirst);
                return html.substring(addressFirst+6,addressLast);
                //System.out.print(title);
            }
        }
        return "获取作者失败！";
    }

}
