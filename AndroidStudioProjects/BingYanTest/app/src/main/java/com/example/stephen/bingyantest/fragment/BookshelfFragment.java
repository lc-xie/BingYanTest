package com.example.stephen.bingyantest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stephen.bingyantest.HttpUtil.BookHttpUtil;
import com.example.stephen.bingyantest.HttpUtil.RefreshBookShelfRV;
import com.example.stephen.bingyantest.HttpUtil.RefreshRVListener;
import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.adapter.BookShelfAdapter;
import com.example.stephen.bingyantest.bean.Book;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by stephen on 17-7-2.
 */

public class BookshelfFragment extends Fragment implements RefreshRVListener {

    private final static String TAG = BookshelfFragment.class.getSimpleName();

    private View view;
    private static List<Book> booksList = new ArrayList<>();
    private BookShelfAdapter bookShelfAdapter;
    private RecyclerView recyclerView;
    private int currentPage = 1;//为 1时可以加载第一页的数据

    private boolean loading = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookshelf, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_book);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        bookShelfAdapter = new BookShelfAdapter(booksList);
        recyclerView.setAdapter(bookShelfAdapter);

        // 给recyclerVew添加滑动监听器
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager1 =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = linearLayoutManager1.getItemCount();
                int lastVisibleItem = linearLayoutManager1.findLastVisibleItemPosition();
                if (!loading && totalItemCount < (lastVisibleItem + 3) && currentPage <= 8) {
                    //标记loading为true,不能再加载，直到LOAD_BOOK_DATA++
                    loading = true;
                    //下载新一页
                    BookHttpUtil.getNextPageBook(currentPage++,
                            new RefreshBookShelfRV(BookshelfFragment.this));
                }
            }
        });
        // 加载第一页的数据
        BookHttpUtil.getNextPageBook(currentPage++,
                new RefreshBookShelfRV(BookshelfFragment.this));
        return view;
    }

    @Override
    public void refresh(List<Book> newBookList) {
        booksList.addAll(newBookList);
        StringBuilder builder = new StringBuilder();
        for (Book book : booksList) {
            builder.append(book.getBookName() + "--");
        }
        Log.e(TAG, builder.toString());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bookShelfAdapter.notifyDataSetChanged();
                loading = false;
            }
        });
    }
}
