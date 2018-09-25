package com.example.stephen.bingyantest.HttpUtil;

import com.example.stephen.bingyantest.bean.Book;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.FormBody;

/**
 * Created by stephen on 17-7-8.
 * 从html源码中匹配出与book相关的部分，添加到一个list中
 * by okHttp
 */

public class BookHttpUtil {

    private final static String TAG = BookHttpUtil.class.getSimpleName();
    private final static String BOOK_MAIN_URL = "http://book.meiriyiwen.com/book";

    // 加载用于在bookShelfFragment显示的book列表，只需要name，author，image
    public static void getNextPageBook(int page, Callback call) {
        FormBody formBody = new FormBody.Builder()
                .add("page", String.valueOf(page))
                .build();
        //OkHttpUtil.sendPOSTRequestEnqueue(BOOK_MAIN_URL, formBody, call);
        OkHttpUtil.sendGETRequestEnqueue(BOOK_MAIN_URL + "?page=" + String.valueOf(page), call);
    }

    public static List<Book> getBooksFromHtml(List<String> booksHtml) {
        List<Book> list = new ArrayList<>();
        for (String html : booksHtml) {
            Book book = new Book(getBookMainUrl(html), getName(html), getAuthor(html), getImageUrl(html));
            list.add(book);
        }
        return list;
    }

    //获取和Book相关的html,一个book放入一个list item中
    public static List<String> getBookHtmlList(String html){
        List<String> bookHtmlList=new ArrayList<>();
        Pattern p=Pattern.compile("<ul class=\"book-list\">(.*?)</ul>");
        Matcher m=p.matcher(html);
        String bookUrl;
        if (m.find()) {
            bookUrl = m.group();
            //Log.d("BookHttpUtil:","bookUrl:"+bookUrl);
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

    //获取完整的图片链接的url
    public static String getImageUrl(String html) {
        //获取图片的url
        Pattern pattern = Pattern.compile("src=\"(.*?)\"/></a>");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return  "http://book.meiriyiwen.com" + matcher.group(1);
        }
        return null;
    }

    //获取书本的url  <http://book.meiriyiwen.com/book/chapter_list/?bid=84>
    public static String getBookMainUrl(String html) {
        Pattern pattern = Pattern.compile("<div class=\"book-name\"><a href=\"(.*?)\"");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return  "http://book.meiriyiwen.com" + matcher.group(1);
        }
        return null;
    }

    //获取书名
    public static String getName(String html) {
        String fault="获取书名失败";
        Pattern pattern = Pattern.compile(" title=\"(.*?)\">");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return fault;
        }
    }

    //获取作者名字
    public static String getAuthor(String url) {
        String fault="获取作者失败";
        Pattern pattern = Pattern.compile("<div class=\"book-author\">(.*?)</div>");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return fault;
        }
    }

}
