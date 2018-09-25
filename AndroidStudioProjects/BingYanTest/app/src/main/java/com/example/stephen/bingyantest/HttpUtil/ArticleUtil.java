package com.example.stephen.bingyantest.HttpUtil;

import com.example.stephen.bingyantest.bean.Article;
import com.example.stephen.bingyantest.imageThreeCache.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by stephen on 18-9-2.
 * 解析文章
 */

public class ArticleUtil {

    private static final String TAG = ArticleUtil.class.getSimpleName();
    private static FileUtil fileUtil = new FileUtil();
    private static final String mainUrl = "https://meiriyiwen.com";
    private static final String randomUrl = mainUrl + "/random";         // 获取随机文章的链接

    public static Article getArticleFromResponse(Response response) throws IOException {
        InputStream is = response.body().byteStream();
        String html = inputStreamToString(is);
        return parseHtmlToArticle(html);
    }

    public static Article getArticleFromFile(String name) {
        try {
            Article article = new Article();
            File file = new File(FileUtil.ARTICLE_PATH, name + ".txt");
            if (!file.exists()) return null;
            InputStream inputStream = new FileInputStream(file);
            BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line = "";
            line = buff.readLine();
            article.setArticalTitle(line);
            while (true) {
                line = buff.readLine();
                if (line != null && line.length() > 0) {
                    article.setArticalAuthor(line);
                    break;
                }
            }
            StringBuilder res = new StringBuilder();
            while ((line = buff.readLine()) != null) {
                res.append(line);
            }
            article.setArticalContent(line);
            return article;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 今日文章，okHttp异步获取
    public static void getTodayArticle(Callback callback) {
        OkHttpUtil.sendGETRequestEnqueue(mainUrl, callback);
    }

    // 随机文章，okHttp异步获取
    public static void getRandomArticle(Callback callback) {
        OkHttpUtil.sendGETRequestEnqueue(randomUrl, callback);
    }

    // InputStream 转换成String
    private static String inputStreamToString(InputStream inputStream) throws IOException {
        //将网页源码转换成String
        BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        StringBuffer res = new StringBuffer();
        String line = "";
        while ((line = buff.readLine()) != null) {
            res.append(line);
        }
        return res.toString();
    }

    /**
     * 解析html源码，提取出文章信息
     *
     * @param html_all 源码
     */
    private static Article parseHtmlToArticle(String html_all) {
        try {
            Article article = new Article();
            if (html_all != null) {
                article.setArticalTitle(getTitle(html_all));                       //获取标题
                article.setArticalAuthor(getAuthor(html_all));                       //获取标题
                article.setArticalContent(getContent(html_all));                       //获取文章内容
                fileUtil.saveArticle(article);    //保存文件
                return article;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从源码获取文章内容
     *
     * @param html 网页源码
     * @return 文章内容
     */
    private static String getContent(String html) {
        Pattern p1 = Pattern.compile("<p>(.*?)</p>");
        Matcher m1 = p1.matcher(html);
        boolean ifFind = m1.find();
        StringBuilder articleContent = new StringBuilder();
        while (ifFind) {
            String temp = m1.group(1);
            if (!temp.equals("")) {
                articleContent.append("\t\t\t");
                articleContent.append(temp);
                articleContent.append("\n\n");
            }
            ifFind = m1.find();
        }
        return articleContent.toString();
    }

    /**
     * 从源码获取文章标题
     *
     * @return 标题
     */
    private static String getTitle(String html) {
        Pattern p = Pattern.compile("<h1>(.*?)</h1>");
        Matcher m = p.matcher(html);
        if (m.find()) {
            return m.group(1);
        }
        return "获取标题失败！";
    }

    /**
     * 从源码获取文章作者
     *
     * @return 作者
     */
    private static String getAuthor(String html) {
        Pattern p = Pattern.compile("\"article_author\"><span>(.*?)</span>");
        Matcher m = p.matcher(html);
        if (m.find()) {
            return m.group(1);
        }
        return "获取作者失败！";
    }

}
