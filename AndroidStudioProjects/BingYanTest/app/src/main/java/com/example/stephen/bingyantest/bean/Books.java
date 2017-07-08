package com.example.stephen.bingyantest.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.stephen.bingyantest.HttpRequest.GetBookSpider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.stephen.bingyantest.MainActivity.photoCache;

/**
 * Created by stephen on 17-7-5.
 */

public class Books {

    private String bookName;
    private String bookAuthor;
    private int imageId;
    private String bookImageUrl;
    private String bookChapterUrl;
    private List<String> chapterList;

    public Books(String url) {
        bookAuthor="";
        bookName="";
        bookImageUrl="";
        bookChapterUrl="";
        chapterList=new ArrayList<>();

        bookName=getName(url);
        bookAuthor=getAuthor(url);
        chapterList=getChapterList(url);
        getImage(url);

    }

    //获取完整的章节链接/url
    List<String> getChapterList(String url) {
        //先获取章节的链接
        Pattern p = Pattern.compile("<a class=\"book-bg\" href=\"(.*?)\" title");
        Matcher m = p.matcher(url);
        if (m.find()) {
            bookChapterUrl = "http://book.meiriyiwen.com/" + m.group(1);
            //获取章节的html
            String chapterHtml= GetBookSpider.sendRequest(bookChapterUrl);
            //根据章节链接获取章节list
            List<String> chapterList = new ArrayList<>();
            Pattern p1 = Pattern.compile("<li>.*?href.*?>(.*?)</a>.*?</li>");
            Matcher m1 = p1.matcher(chapterHtml);
            Boolean isFind = m1.find();
            while (isFind) {
                String temp = m1.group(1);
                //添加成功匹配的成果
                chapterList.add(temp);
                //继续查找下一个对象
                isFind = m1.find();
            }
        }else System.out.print("Failed!");
        return chapterList;
    }
    //获取完整的图片链接/url
    void getImage(String url) {
        String fault="获取图片url失败";
        //获取图片的url
        Pattern pattern = Pattern.compile("src=\"(.*?)\"/></a>");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            bookImageUrl = "http://book.meiriyiwen.com" + matcher.group(1);
            //爬取图片
            getPhoto(bookImageUrl,bookName);
        }
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
    //根据url爬取图片
    public void getPhoto(String url,String name){
        Bitmap bitmap = null;
        try{
            URL photoUrl=new URL(url);
            URLConnection connection = photoUrl.openConnection();
            connection.connect();

            InputStream inputStream=connection.getInputStream();


           /*
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            byte [] buf = new byte[1024];
            int len = 0;
            //读取图片数据
            while((len=inputStream.read(buf))!=-1){
                //System.out.println(len);
                outputStream.write(buf,0,len);
            }
            inputStream.close();
            outputStream.close();
            File file=new File(photoCache,name+".jpg");
            //boolean ifExet=file.exists();
            if (!file.exists()){
                boolean ifCreate = file.mkdir();
                if (ifCreate){
                    FileOutputStream op = new FileOutputStream(file);
                    op.write(outputStream.toByteArray());
                    op.close();
                }else {
                    Log.d("Books:","创建文件失败！");
                }
            }else {
                FileOutputStream op = new FileOutputStream(file);
                op.write(outputStream.toByteArray());
                op.close();
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getImageId() {
        return imageId;
    }
    public String getBookAuthor() {

        return bookAuthor;
    }
    public String getBookName() {
        return bookName;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

}
