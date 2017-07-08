package com.example.stephen.bingyantest.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stephen.bingyantest.HttpRequest.GetArtical;
import com.example.stephen.bingyantest.R;

/**
 * Created by stephen on 17-7-2.
 */

public class ArticalFragment extends Fragment {
    private View view;
    private GetArtical getArtical;
    private TextView title,author,content;
    public static final int LOAD_ARTICAL_DATA=1;
    private Handler handler;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_artical,container,false);

        title=(TextView)view.findViewById(R.id.artical_title);
        author=(TextView)view.findViewById(R.id.artical_author);
        content=(TextView)view.findViewById(R.id.artical_content);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getArtical=new GetArtical();
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
                }
            }
        };
        return view;
    }
}
