package com.example.stephen.bingyantest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.adapter.ChapterAdapter;
import com.example.stephen.bingyantest.bean.Books;
import com.example.stephen.bingyantest.fragment.BookshelfFragment;
import com.example.stephen.bingyantest.imageThreeCache.FileUtil;
import com.example.stephen.bingyantest.imageThreeCache.ImageCallBack;
import com.example.stephen.bingyantest.imageThreeCache.ImageTool;
import com.example.stephen.bingyantest.imageThreeCache.LruCacheHelper;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

public class BookReadingActivity extends AppCompatActivity {

    private ImageView back;
    private TextView topText;
    ImageView bookImage;
    TextView bookName;
    TextView bookAuthor;
    Books books;
    List<String> chapterList;
    List<String> chapterContentList;

    /**
     *三级缓存相关类
     */
    private ImageTool imageTool;
    private LruCacheHelper lruCacheHelper;
    private FileUtil fileUtil;


    RecyclerView chapterRecyclerView;
    ChapterAdapter chapterAdapter;

    Handler hander;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_reading);
        //初始化头布局
        topText=(TextView)findViewById(R.id.top_text);
        topText.setText("详情");
        back=(ImageView)findViewById(R.id.back_book_detail);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //初始化图片三级存储有关类
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        lruCacheHelper=new LruCacheHelper(mCacheSize);
        fileUtil=new FileUtil(this,"/icon");
        imageTool=new ImageTool(lruCacheHelper,fileUtil);

        chapterRecyclerView=(RecyclerView)findViewById(R.id.recycler_view_chapter_list);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        chapterRecyclerView.setLayoutManager(linearLayoutManager);

        bookAuthor=(TextView)findViewById(R.id.book_author);
        bookImage=(ImageView)findViewById(R.id.book_image);
        bookName=(TextView)findViewById(R.id.book_name);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent=getIntent();
                String url=intent.getStringExtra("url_data");
                Message m = new Message();
                if(url!=null&&url!="") {
                    books = new Books(url);
                    books.intiChapter(url);
                    chapterList = books.getChapterNameList();
                    chapterContentList = books.getChapterContentList();
                    Log.d("BookReadingActivity", "size:" + chapterContentList.size());
                    m.what = 11;
                }else m.what=10;
                hander.sendMessage(m);
            }
        }).start();

        hander=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==11){
                    bookName.setText(books.getBookName());
                    bookAuthor.setText(books.getBookAuthor());
                    try {
                        Bitmap bitmap=imageTool.getBitmap(books.getBookImageUrl(), new ImageCallBack() {
                            @Override
                            public void imageLoadded(Bitmap bitmap, String tag) {
                                bookImage.setImageBitmap(bitmap);
                            }
                        });
                        bookImage.setImageBitmap(bitmap);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    chapterAdapter=new ChapterAdapter(chapterList,chapterContentList);
                    chapterRecyclerView.setAdapter(chapterAdapter);
                }else {
                    bookName.setText("null");
                    bookAuthor.setText("null");
                    bookImage.setImageResource(R.drawable.temp);
                }
            }
        };
    }

}
