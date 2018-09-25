package com.example.stephen.bingyantest.HttpUtil;

import com.example.stephen.bingyantest.bean.Book;

import java.util.List;

/**
 * Created by stephen on 18-9-2.
 * 刷新书架页面
 */

public interface RefreshRVListener {

    void refresh(List<Book> newBookList);

}
