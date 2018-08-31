package com.example.stephen.bingyantest.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stephen.bingyantest.HttpRequest.GetArticleTask;
import com.example.stephen.bingyantest.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by stephen on 17-7-2.
 */

public class ArticleFragment extends Fragment {
    private View view;
    private ScrollView scrollView;
    public TextView title, author, content;
    private boolean ifDown = false;                         // 是否已经从网络下载了文章
    private String mainUrl = "https://meiriyiwen.com";
    private String randomUrl = mainUrl + "/random";         // 获取随机文章的链接
    private String todayArticleName;                        // 今日文章的标题（文件名）

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_artical, container, false);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView_artical);

        title = (TextView) view.findViewById(R.id.artical_title);
        author = (TextView) view.findViewById(R.id.artical_author);
        content = (TextView) view.findViewById(R.id.artical_content);

        new GetArticleTask(this).execute(mainUrl);

        return view;
    }

    //从本地读取今日的文章
    public void getArticleFromFile(String name) {
        File file = new File(Environment.getExternalStorageDirectory(), "/meiriyiwen/Article/" + name + ".txt");
        if (!file.exists()) {
            new GetArticleTask(this).execute(mainUrl);
        } else {
            try {
                InputStream inputStream = new FileInputStream(file);
                BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                String line = "";
                line = buff.readLine();
                title.setText(line);
                while (true) {
                    line = buff.readLine();
                    if (line != null && line.length() > 0) {
                        author.setText(line);
                        break;
                    }
                }
                StringBuilder res = new StringBuilder();
                while ((line = buff.readLine()) != null) {
                    res.append(line);
                }
                content.setText(res.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showTodayArticle() {
        getArticleFromFile(todayArticleName);
        scrollView.scrollTo(0, 0);//回到顶部
    }

    public void showRandomArticle() {
        new GetArticleTask(this).execute(randomUrl);
        scrollView.scrollTo(0, 0);//回到顶部
    }

    //获取今日文章的标题
    public void setTodayArticleName(String name) {
        this.todayArticleName = name;
    }

}
