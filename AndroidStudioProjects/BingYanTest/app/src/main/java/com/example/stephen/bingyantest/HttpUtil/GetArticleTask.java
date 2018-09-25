package com.example.stephen.bingyantest.HttpUtil;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.example.stephen.bingyantest.activity.MyApplication;
import com.example.stephen.bingyantest.bean.Article;
import com.example.stephen.bingyantest.fragment.ArticleFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stephen on 17-7-6.
 * 下载文章task
 */

@Deprecated
public class GetArticleTask extends AsyncTask<String, String, Article> {
    private static final String TAG = GetArticleTask.class.getSimpleName();

    private WeakReference<ArticleFragment> articleFragmentWeakReference;
    private boolean isTodayArticle = false;

    public GetArticleTask(ArticleFragment articleFragment) {
        this.articleFragmentWeakReference = new WeakReference<>(articleFragment);
    }

    @Override
    protected Article doInBackground(String... strings) {
        if (!strings[0].contains("random")) {
            isTodayArticle = true;
        }
        String html_all = HttpTool.getHtmlByOkHttp(strings[0]);
        return parseHtmlToArticle(html_all);
    }

    @Override
    protected void onPostExecute(Article article) {
        ArticleFragment articleFragment = articleFragmentWeakReference.get();
        if (article != null) {
            articleFragment.title.setText(article.getArticalTitle());
            articleFragment.author.setText(article.getArticalAuthor());
            articleFragment.content.setText(article.getArticalContent());
            if (isTodayArticle) articleFragment.setTodayArticleName(article.getArticalTitle());
        } else {
            articleFragment.author.setText("网络出错");
            Toast.makeText(MyApplication.getContext(), "获取文章失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 解析html源码，提取出文章信息
     *
     * @param html_all 源码
     */
    private Article parseHtmlToArticle(String html_all) {
        try {
            Article article = new Article();
            if (html_all != null) {
                article.setArticalTitle(getTitle(html_all));                       //获取标题
                article.setArticalAuthor(getAuthor(html_all));                       //获取标题
                article.setArticalContent(getContent(html_all));                       //获取文章内容
                File dir = new File(Environment.getExternalStorageDirectory(), "/meiriyiwen/Article");
                if (!dir.exists()) dir.mkdirs();
                String name = article.getArticalTitle() + ".txt";
                File file = new File(dir.getPath(), name);//新建存储文章的文件
                if (!file.exists()) {
                    saveArticleToFile(article, file);//保存文件
                }
                return article;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存文本文章到文件夹
     *
     * @param file 存储文件
     */
    private void saveArticleToFile(Article article, File file) {
        try {
            if (file.exists()) file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] data = (article.getArticalTitle() + "\n").getBytes();
            fos.write(data);
            byte[] data1 = (article.getArticalAuthor() + "\t\n\n").getBytes();
            fos.write(data1);
            byte[] data2 = (article.getArticalContent() + "\t\n\n").getBytes();
            fos.write(data2);
            fos.close();
        } catch (IOException e) {
            System.out.println("Error at save article...");
            e.printStackTrace();
        }
    }

    /**
     * 从源码获取文章内容
     *
     * @param html 网页源码
     * @return 文章内容
     */
    private String getContent(String html) {
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
     * @param html
     * @return 标题
     */
    private String getTitle(String html) {
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
     * @param html
     * @return 作者
     */
    private String getAuthor(String html) {
        Pattern p = Pattern.compile("\"article_author\"><span>(.*?)</span>");
        Matcher m = p.matcher(html);
        if (m.find()) {
            return m.group(1);
        }
        return "获取作者失败！";
    }

}
