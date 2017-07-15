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

import com.example.stephen.bingyantest.HttpRequest.GetHtmlFromUrl;
import com.example.stephen.bingyantest.HttpRequest.GetVoice;
import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.adapter.BookShelfAdapter;
import com.example.stephen.bingyantest.adapter.VoiceAdapter;
import com.example.stephen.bingyantest.bean.Voice;
import com.example.stephen.bingyantest.imageThreeCache.FileUtil;
import com.example.stephen.bingyantest.imageThreeCache.ImageTool;
import com.example.stephen.bingyantest.imageThreeCache.LruCacheHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephen on 17-7-2.
 */

public class VoiceFragment extends Fragment {
    private View view;
    private List<String> voiceAuthorList=new ArrayList<>();
    private List<String> voiceNameList=new ArrayList<>();
    private List<String> voiceTagList=new ArrayList<>();
    private List<String> voiceImageUrlList=new ArrayList<>();
    private List<String> voicePlayUrlList=new ArrayList<>();
    private String mainHtml;//主页面的html源码
    private List<Voice> voiceList=new ArrayList<>();

    Handler handler;
    private static int LOAD_VOICE_HTML=111;
    private VoiceAdapter voiceAdapter;
    private RecyclerView recyclerView;

    /**
     *三级缓存相关类
     */
    private ImageTool imageTool;
    private LruCacheHelper lruCacheHelper;
    private FileUtil fileUtil;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_voice,container,false);
        //初始化图片三级存储有关类
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        lruCacheHelper=new LruCacheHelper(mCacheSize);
        fileUtil=new FileUtil(getContext(),"/voiceImage");
        imageTool=new ImageTool(lruCacheHelper,fileUtil);
        recyclerView=(RecyclerView)view.findViewById(R.id.recycler_view_voice);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        voiceAdapter=new VoiceAdapter(voiceList,imageTool);
        recyclerView.setAdapter(voiceAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mainHtml= GetHtmlFromUrl.sendRequest("http://voice.meiriyiwen.com/");

                Message message=new Message();
                message.what=LOAD_VOICE_HTML;
                handler.sendMessage(message);
            }
        }).start();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==LOAD_VOICE_HTML){
                    voiceAuthorList= GetVoice.getAuthorHtmlList(mainHtml);
                    voiceImageUrlList=GetVoice.getImageUrlList(mainHtml);
                    voiceNameList=GetVoice.getNameHtmlList(mainHtml);
                    voiceTagList=GetVoice.getTagList(mainHtml);
                    voicePlayUrlList=GetVoice.getVoiceUrlList(mainHtml);
                    for (int i=0;i<voiceNameList.size();i++){
                        Voice voice=new Voice();
                        voice.setVoiceAuthor(voiceAuthorList.get(i));
                        voice.setVoiceName(voiceNameList.get(i));
                        voice.setVoiceNumber(voiceTagList.get(i));
                        voice.setVoiceImageUrl(voiceImageUrlList.get(i));
                        voice.setVoiceUrl(voicePlayUrlList.get(i));
                        voiceList.add(voice);
                        Log.d("VoiceFragment",voice.getVoiceName());
                    }
                    voiceAdapter.notifyDataSetChanged();
                }
            }
        };

        return view;
    }
}
