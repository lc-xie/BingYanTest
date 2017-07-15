package com.example.stephen.bingyantest.bean;

import android.util.Log;

import com.example.stephen.bingyantest.HttpRequest.GetBookSpider;
import com.example.stephen.bingyantest.HttpRequest.GetHtmlFromUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by stephen on 17-7-5.
 *在fragment里面只需要显示image,name和author,
 * 所以构造函数初始化books时只需要加载这三项
 * 章节信息点击进入相应界面再加载
 */

public class Books {

    private String url;

    private String bookName;
    private String bookAuthor;
    private String bookImageUrl;
    private String bookChapterUrl;//章节url
    private List<String> chapterNameList;
    private List<String> chapterContentList;

    public Books(String url) {
        this.url=url;
        bookAuthor="";
        bookName="";
        bookImageUrl="";

        bookName=getName(url);
        bookAuthor=getAuthor(url);
        bookImageUrl=getImage(url);
    }

    //获取完整的章节名称
    public void intiChapter(String url) {
        chapterNameList = new ArrayList<>();
        chapterContentList=new ArrayList<>();
        String chapterHtml=null;
        //先获取章节的链接
        Pattern p = Pattern.compile("<a class=\"book-bg\" href=\"(.*?)\" title");
        Matcher m = p.matcher(url);
        if (m.find()) {
            bookChapterUrl = "http://book.meiriyiwen.com/" + m.group(1);
            //获取章节的html源码
            chapterHtml= GetHtmlFromUrl.sendRequest(bookChapterUrl);
            //根据章节链接获取章节名称list
            Pattern p1 = Pattern.compile("<li>.*?href.*?>(.*?)</a>.*?</li>");
            Matcher m1 = p1.matcher(chapterHtml);
            Boolean isFind = m1.find();
            while (isFind) {
                String temp = m1.group(1);
                //添加成功匹配的成果
                chapterNameList.add(temp);
                //继续查找下一个对象
                isFind = m1.find();
            }
            //根据章节链接获取章节链接list
            Pattern p2 = Pattern.compile("<li>.*?href=\"(.*?)\">.*?</li>");
            Matcher m2 = p2.matcher(chapterHtml);
            Boolean isFind2 = m2.find();
            while (isFind2) {
                String chapterUrl ="http://book.meiriyiwen.com/"+ m2.group(1);//获取类某一章节的url链接
                //获取章节内容
                String chapterItemHtml= GetHtmlFromUrl.sendRequest(chapterUrl);
                Pattern p3 = Pattern.compile("<div class=\"chapter-bg\">(.*?)</div>");
                Matcher m3 = p3.matcher(chapterItemHtml);
                Boolean isFind3 = m3.find();
                if (isFind3){
                    String tempTotalContent="";
                    String chapterHtmlContent=m3.group(1);
                    Pattern p4 = Pattern.compile("<p>(.*?)</p>");
                    Matcher m4 = p4.matcher(chapterHtmlContent);
                    Boolean isFind4 = m4.find();
                    while (isFind4){
                        tempTotalContent=tempTotalContent+"\t\t\t"+m4.group(1)+'\n'+'\n';
                        isFind4=m4.find();
                    }
                    //添加成功匹配的成果
                    chapterContentList.add(tempTotalContent);
                }
                //继续查找下一个对象
                isFind2 = m2.find();
            }
        }else System.out.print("get chapterNameList Failed!");
    }
    //获取完整的图片链接/url
    String getImage(String url) {
        String fault="获取图片url失败";
        //获取图片的url
        Pattern pattern = Pattern.compile("src=\"(.*?)\"/></a>");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return  "http://book.meiriyiwen.com" + matcher.group(1);
        }
        return null;
    }
    //获取书名l
    String getName(String url) {
        String fault="获取书名失败";
        Pattern pattern = Pattern.compile("<a class=\"book-bg\".*?title=\"(.*?)\"><img");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return fault;
        }
    }
    //获取作者名字
    String getAuthor(String url) {
        String fault="获取作者失败";
        Pattern pattern = Pattern.compile("<div class=\"book-author\">(.*?)</div>");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return fault;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getBookImageUrl() {
        return bookImageUrl;
    }
    public String getBookAuthor() {

        return bookAuthor;
    }
    public String getBookName() {
        return bookName;
    }

    public List<String> getChapterNameList() {
        return chapterNameList;
    }

    public List<String> getChapterContentList() {
        return chapterContentList;
    }

}
