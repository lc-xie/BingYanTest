package com.example.stephen.bingyantest.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stephen.bingyantest.HttpRequest.GetArtical;
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

public class ArticalFragment extends Fragment {
    private View view;
    private ScrollView scrollView;
    private GetArtical getArtical;
    private TextView title,author,content;
    private Handler handler;
    private boolean ifDown=false;//是否已经从网络下载了文章
    private String randomUrl;//获取随机文章的链接
    private String mainUrl="https://meiriyiwen.com";
    //private String todayArticalName;//今日文章的标题（文件名）

    private ArticalReceiver articalReceiver;
    public static final int LOAD_ARTICAL_DATA=1;
    public static final String GET_RANDOM_ARTICAL="com.example.stephen.bingyantest.action.GET_RANDOM_ARTICAL";
    public static final String GET_TODAY_ARTICAL="com.example.stephen.bingyantest.action.GET_TODAY_ARTICAL";
    public static final String TODAY_ARTICAL="com.example.stephen.bingyantest.type.TODAY_ARTICAL";
    public static final String RANDOM_ARTICAL="com.example.stephen.bingyantest.type.RANDOM_ARTICAL";
    public static final String NET_FAULT="com.example.stephen.bingyantest.action.NET_FAULT";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_artical,container,false);
        scrollView=(ScrollView)view.findViewById(R.id.scrollView_artical);

        title=(TextView)view.findViewById(R.id.artical_title);
        author=(TextView)view.findViewById(R.id.artical_author);
        content=(TextView)view.findViewById(R.id.artical_content);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getArtical=new GetArtical(mainUrl,TODAY_ARTICAL);
                Message message=new Message();
                message.what=LOAD_ARTICAL_DATA;
                handler.sendMessage(message);
            }
        }).start();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==LOAD_ARTICAL_DATA){
                    title.setText(getArtical.getTitle());
                    author.setText(getArtical.getAuthor());
                    content.setText(getArtical.getContent());
                    randomUrl=getArtical.getRandomArticalUrl();
                    ifDown=true;
                }
            }
        };

        return view;
    }

    public void getArticalFromFile(){
        File fileDir=new File(Environment.getExternalStorageDirectory(),"/meiriyiwen/Artical/TodayArtical");
        File[] files=fileDir.listFiles();
        try {
            File file=files[0];
            InputStream inputStream=new FileInputStream(file);
            BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line = "";
            line=buff.readLine();
            title.setText(line);
            line=buff.readLine();
            author.setText(line);
            StringBuffer res = new StringBuffer();
            while((line = buff.readLine()) != null){
                res.append(line);
            }
            content.setText(res.toString());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //广播接收器，接收到随机一文的广播时加载随机文章
    public class ArticalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()==GET_RANDOM_ARTICAL) {
                if (randomUrl != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getArtical = new GetArtical(randomUrl,RANDOM_ARTICAL);
                            Message message = new Message();
                            message.what = LOAD_ARTICAL_DATA;
                            handler.sendMessage(message);
                            scrollView.scrollTo(0, 0);
                        }
                    }).start();
                } else {
                    Toast.makeText(getContext(), "获取文章失败！", Toast.LENGTH_SHORT).show();
                }
            }else if (intent.getAction()==GET_TODAY_ARTICAL){
                getArticalFromFile();
                scrollView.scrollTo(0, 0);
            }else if (intent.getAction()==NET_FAULT){
                getArticalFromFile();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(GET_RANDOM_ARTICAL);
        intentFilter.addAction(GET_TODAY_ARTICAL);
        intentFilter.addAction(NET_FAULT);
        articalReceiver=new ArticalReceiver();
        getActivity().registerReceiver(articalReceiver,intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(articalReceiver);
    }
}
