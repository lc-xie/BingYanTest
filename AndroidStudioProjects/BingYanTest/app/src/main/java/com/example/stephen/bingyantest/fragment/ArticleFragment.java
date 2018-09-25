package com.example.stephen.bingyantest.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.stephen.bingyantest.HttpUtil.ArticleUtil;
import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.bean.Article;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by stephen on 17-7-2.
 */

public class ArticleFragment extends Fragment {

    private static final String TAG = ArticleFragment.class.getSimpleName();

    private static final String NET_WORK_ERROR = "网络请求出错";

    private ScrollView scrollView;
    public TextView title, author, content;
    private String todayArticleName;            // 今日文章的标题（文件名）

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artical, container, false);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView_artical);

        title = (TextView) view.findViewById(R.id.artical_title);
        author = (TextView) view.findViewById(R.id.artical_author);
        content = (TextView) view.findViewById(R.id.artical_content);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("test dialog")
                        .create();
                alertDialog.show();
            }
        });

        showTodayArticle();

        return view;
    }

    // 获取今日的文章
    public void showTodayArticle() {
        scrollView.scrollTo(0, 0);//回到顶部
        Article article = ArticleUtil.getArticleFromFile(todayArticleName);
        if (article != null) {
            showArticleOnUI(article);
        } else {
            ArticleUtil.getTodayArticle(new ShowArticleCallBack());
        }
    }

    // 获取随机文章
    public void showRandomArticle() {
        ArticleUtil.getRandomArticle(new ShowArticleCallBack());
        scrollView.scrollTo(0, 0);//回到顶部
    }

    // okHttp显示文章的回调
    private class ShowArticleCallBack implements Callback {
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Article article = ArticleUtil.getArticleFromResponse(response);
            showArticleOnUI(article);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            //author.setText(NET_WORK_ERROR);
            Log.e(TAG, "网络请求获取文章失败");
        }
    }

    // 主线程将文章更新到UI
    public void showArticleOnUI(final Article article) {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText(article.getArticalTitle());
                author.setText(article.getArticalAuthor());
                content.setText(article.getArticalContent());
                scrollView.scrollTo(0, 0);//回到顶部
            }
        });
    }

    // 设置今日文章的标题
    public void setTodayArticleName(String name) {
        this.todayArticleName = name;
    }

}
