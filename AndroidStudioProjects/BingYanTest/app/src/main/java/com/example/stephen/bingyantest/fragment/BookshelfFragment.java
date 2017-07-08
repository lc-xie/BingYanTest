package com.example.stephen.bingyantest.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stephen.bingyantest.HttpRequest.GetArtical;
import com.example.stephen.bingyantest.HttpRequest.GetBookSpider;
import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.adapter.BookShelfAdapter;
import com.example.stephen.bingyantest.bean.Books;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephen on 17-7-2.
 */

public class BookshelfFragment  extends Fragment {
    private View view;
    private List<Books> booksList=new ArrayList<>();
    private BookShelfAdapter bookShelfAdapter;
    private RecyclerView recyclerView;
    public static final int LOAD_BOOK_DATA=1;
    private Handler bookHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_bookshelf,container,false);

        recyclerView=(RecyclerView)view.findViewById(R.id.recycler_view_book);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        bookShelfAdapter=new BookShelfAdapter(booksList);
        recyclerView.setAdapter(bookShelfAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String mainUrl=GetBookSpider.sendRequest("http://book.meiriyiwen.com/");
                List<String> bookHtmlList=new ArrayList<String>();
                bookHtmlList = GetBookSpider.getBookHtmlList(mainUrl);
                for (int i=0;i<bookHtmlList.size();i++){
                    Books book=new Books(bookHtmlList.get(i));
                    booksList.add(book);
                    Log.d("BookShelfFragment:","book name:《"+book.getBookName()+"》;book author:"+book.getBookAuthor());
                }
                Message message=new Message();
                message.what=LOAD_BOOK_DATA;
                bookHandler.sendMessage(message);

            }
        }).start();

        bookHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==LOAD_BOOK_DATA){
                    bookShelfAdapter.notifyDataSetChanged();
                }
            }
        };

        return view;
    }
}
