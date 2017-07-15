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
import com.example.stephen.bingyantest.imageThreeCache.FileUtil;
import com.example.stephen.bingyantest.imageThreeCache.ImageTool;
import com.example.stephen.bingyantest.imageThreeCache.LruCacheHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stephen on 17-7-2.
 */

public class BookshelfFragment  extends Fragment {
    private View view;
    //网页html源码
    private String bookMainHtml=null;
    //存储每一页的url
    private List<String> pageUrlList=new ArrayList<>();
    /**
     * booklist全局变量，存储books item，
     * 访问到一个book item对应的html源码之后
     * 新建book对象，并添加到booklist中
     */
    private static List<Books> booksList=new ArrayList<>();
    private BookShelfAdapter bookShelfAdapter;
    private RecyclerView recyclerView;
    /**
     * message通信标记用
     */
    public static int LOAD_BOOK_DATA=1;//为 1时可以加载第一页的数据
    private Handler bookHandler;

    /**
     *三级缓存相关类
     */
    private ImageTool imageTool;
    private LruCacheHelper lruCacheHelper;
    private FileUtil fileUtil;

    private boolean loading=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_bookshelf,container,false);
        //初始化图片三级存储有关类
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        lruCacheHelper=new LruCacheHelper(mCacheSize);
        fileUtil=new FileUtil(getContext());
        imageTool=new ImageTool(lruCacheHelper,fileUtil);

        recyclerView=(RecyclerView)view.findViewById(R.id.recycler_view_book);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        bookShelfAdapter=new BookShelfAdapter(booksList,imageTool);
        recyclerView.setAdapter(bookShelfAdapter);

        /**
         * 给recyclerVew添加滑动监听器
         */
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager1=(LinearLayoutManager)recyclerView.getLayoutManager();
                int totalItemCount=linearLayoutManager1 .getItemCount();
                int lastVisibleItem=linearLayoutManager1.findLastVisibleItemPosition();
                if (!loading && totalItemCount < (lastVisibleItem + 3)&&LOAD_BOOK_DATA<=pageUrlList.size()) {
                    //标记loading为true,不能再加载，直到LOAD_BOOK_DATA++
                    loading = true;
                    //下载新一页
                    loadNextPageBooks();
                }
            }
        });

        //开启子线程获取bookshlef第一页的数据并显示
        new Thread(new Runnable() {
            @Override
            public void run() {
                bookMainHtml=GetBookSpider.sendRequest("http://book.meiriyiwen.com/");
                List<String> bookHtmlList=new ArrayList<String>();
                bookHtmlList = GetBookSpider.getBookHtmlList(bookMainHtml);
                for (int i=0;i<bookHtmlList.size();i++){
                    Books book=new Books(bookHtmlList.get(i));
                    booksList.add(book);
                    //Log.d("BookShelfFragment:","book name:《"+book.getBookName()+"》;book author:"+book.getBookAuthor());
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
                    Log.d("BookShelfAdapter","第"+LOAD_BOOK_DATA+"页");
                    getPagerUrlList();
                    //数据加载完成，标记第一页已加载，可以加载第二页
                    LOAD_BOOK_DATA++;
                    loading=false;
                }
            }
        };

        return view;
    }

    public void getPagerUrlList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Pattern p=Pattern.compile("<a  class=\"curr_page(.*?)</div>");
                Matcher m=p.matcher(bookMainHtml);
                String pageUrlHtml;//包含页面链接的所有html源码
                if (m.find()) {
                    pageUrlHtml = m.group();
                    Pattern p1 = Pattern.compile("href=\"(.+?)\">[0-9]</a>");
                    Matcher m1 = p1.matcher(pageUrlHtml);
                    Boolean isFind = m1.find();
                    while (isFind) {
                        String temp = m1.group(1);
                        //添加成功匹配的成果
                        pageUrlList.add(temp);
                        //继续查找下一个对象
                        isFind = m1.find();
                    }
                }
            }
        }).start();
    }

    public void loadNextPageBooks(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取第LOAD_BOOK_DATA页的有关book的html源码
                String bookMainHtml=GetBookSpider.sendRequest(pageUrlList.get(LOAD_BOOK_DATA-1));
                List<String> bookHtmlList=new ArrayList<String>();
                bookHtmlList = GetBookSpider.getBookHtmlList(bookMainHtml);
                for (int i=0;i<bookHtmlList.size();i++){
                    Books book=new Books(bookHtmlList.get(i));
                    booksList.add(book);
                }
                //新一页的book加载完成，发送消息
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
                    //数据加载完成，标记第一页已加载，可以加载下一页
                    Log.d("BookShelfAdapter","第"+LOAD_BOOK_DATA+"页");
                    LOAD_BOOK_DATA++;
                    loading=false;
                }
            }
        };
    }
}
