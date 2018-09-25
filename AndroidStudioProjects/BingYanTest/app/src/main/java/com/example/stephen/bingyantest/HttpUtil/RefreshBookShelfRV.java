package com.example.stephen.bingyantest.HttpUtil;

import android.util.Log;

import com.example.stephen.bingyantest.bean.Book;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.stephen.bingyantest.HttpUtil.OkHttpUtil.inputStreamToString;

/**
 * 异步获取书架信息的callback
 */

public class RefreshBookShelfRV implements Callback {

    private final static String TAG = RefreshBookShelfRV.class.getSimpleName();

    private RefreshRVListener listener;

    /**
     * 构造函数传入一个Listener，用于更新recyclerView
     * @param listener 回调更新接口
     */
    public RefreshBookShelfRV(RefreshRVListener listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String html = inputStreamToString(response.body().byteStream());
        List<String> booksHtmlList = BookHttpUtil.getBookHtmlList(html);
        List<Book> list = BookHttpUtil.getBooksFromHtml(booksHtmlList);
        listener.refresh(list);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.e(TAG, "获取书架信息出错");
    }

}
