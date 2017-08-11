package com.example.stephen.bingyantest.fragment;

import android.content.Context;
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
import android.widget.Toast;

import com.example.stephen.bingyantest.HttpRequest.GetHtmlFromUrl;
import com.example.stephen.bingyantest.HttpRequest.GetVoice;
import com.example.stephen.bingyantest.activity.MyApplication;
import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.adapter.VoiceAdapter;
import com.example.stephen.bingyantest.bean.Voice;
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

public class VoiceFragment extends Fragment {
    private View view;
    private List<String> voiceAuthorList=new ArrayList<>();
    private List<String> voiceNameList=new ArrayList<>();
    private List<String> voiceTagList=new ArrayList<>();
    private List<String> voiceImageUrlList=new ArrayList<>();
    private List<String> voicePlayUrlList=new ArrayList<>();
    private List<String> voiceDownloadUrlList=new ArrayList<>();
    private String mainHtml;//主页面的html源码
    private List<Voice> voiceList=new ArrayList<>();
    private int LOADING_VOICE_PAGE_First=1;//current page
    private int LOADING_VOICE_PAGE_FAILED=0;

    private Context context=this.getContext();
    Handler handler;
    //private static int LOAD_VOICE_HTML=111;
    private VoiceAdapter voiceAdapter;
    private RecyclerView recyclerView;

    /**
     *三级缓存相关类
     */
    private ImageTool imageTool;
    private LruCacheHelper lruCacheHelper;
    private FileUtil fileUtil;
    /**
     * loadingVoice next page voice
     */
    private boolean loadingVoice=false;
    private int LOADING_VOICE_PAGE=1;//current page
    //private String nextPageUrl;
    private String currentPageHtml;
    private Handler voiceHander;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        Log.d("VOiceFragment","ON_CREATE_VIEW!");
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
                if (mainHtml!=""){
                    currentPageHtml=mainHtml;
                    voiceTagList.clear();
                    voiceNameList.clear();
                    voiceAuthorList.clear();
                    voiceImageUrlList.clear();
                    voicePlayUrlList.clear();
                    voiceDownloadUrlList.clear();
                    voiceAuthorList= GetVoice.getAuthorHtmlList(mainHtml);
                    voiceImageUrlList=GetVoice.getImageUrlList(mainHtml);
                    voiceNameList=GetVoice.getNameHtmlList(mainHtml);
                    voiceTagList=GetVoice.getTagList(mainHtml);
                    voicePlayUrlList=GetVoice.getVoiceUrlList(mainHtml);
                    voiceDownloadUrlList=GetVoice.getVoiceDownloadList(voicePlayUrlList);
                    if (voiceNameList!=null) {
                        for (int i = 0; i < voiceNameList.size(); i++) {
                            Voice voice = new Voice();
                            voice.setVoiceAuthor(voiceAuthorList.get(i));
                            voice.setVoiceName(voiceNameList.get(i));
                            voice.setVoiceNumber(voiceTagList.get(i));
                            voice.setVoiceImageUrl(voiceImageUrlList.get(i));
                            voice.setVoicePlayUrl(voicePlayUrlList.get(i));
                            voice.setVoiceDownloadUrl(voiceDownloadUrlList.get(i));
                            voiceList.add(voice);
                        }
                        message.what = LOADING_VOICE_PAGE_First;
                    }else {
                        message.what=LOADING_VOICE_PAGE_FAILED;
                    }
                }else {
                    message.what=LOADING_VOICE_PAGE_FAILED;
                }
                handler.sendMessage(message);
            }
        }).start();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==LOADING_VOICE_PAGE_First){
                    voiceAdapter.notifyDataSetChanged();
                    LOADING_VOICE_PAGE++;
                }else if (msg.what==LOADING_VOICE_PAGE_FAILED){
                    Toast.makeText(MyApplication.getContext(),"网络链接出错！",Toast.LENGTH_SHORT).show();
                }
            }
        };

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
                if (!loadingVoice && totalItemCount < (lastVisibleItem + 3)) {
                    //标记loading为true,不能再加载，直到LOADING_VOICE_PAGE++
                    loadingVoice = true;
                    //下载新一页
                    loadNextPageVoices();
                }
            }
        });

        return view;
    }

    public void loadNextPageVoices(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentPageHtml=GetHtmlFromUrl.sendRequest(getNextPageUrl());
                if (currentPageHtml!=""){
                    voiceTagList.clear();
                    voiceNameList.clear();
                    voiceAuthorList.clear();
                    voiceImageUrlList.clear();
                    voicePlayUrlList.clear();
                    voiceDownloadUrlList.clear();
                    voiceAuthorList= GetVoice.getAuthorHtmlList(currentPageHtml);
                    voiceImageUrlList=GetVoice.getImageUrlList(currentPageHtml);
                    voiceNameList=GetVoice.getNameHtmlList(currentPageHtml);
                    voiceTagList=GetVoice.getTagList(currentPageHtml);
                    voicePlayUrlList=GetVoice.getVoiceUrlList(currentPageHtml);
                    voiceDownloadUrlList=GetVoice.getVoiceDownloadList(voicePlayUrlList);
                    if (voiceNameList!=null) {
                        for (int i = 0; i < voiceNameList.size(); i++) {
                            Voice voice = new Voice();
                            voice.setVoiceAuthor(voiceAuthorList.get(i));
                            voice.setVoiceName(voiceNameList.get(i));
                            voice.setVoiceNumber(voiceTagList.get(i));
                            voice.setVoiceImageUrl(voiceImageUrlList.get(i));
                            voice.setVoicePlayUrl(voicePlayUrlList.get(i));
                            voice.setVoiceDownloadUrl(voiceDownloadUrlList.get(i));
                            voiceList.add(voice);
                        }
                        Message message=new Message();
                        message.what=LOADING_VOICE_PAGE;
                        voiceHander.sendMessage(message);
                    }else {
                        Toast.makeText(getContext(),"网络请求出错！",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Message message=new Message();
                    message.what=LOADING_VOICE_PAGE_FAILED;
                    voiceHander.sendMessage(message);
                }
            }
        }).start();

        voiceHander=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==LOADING_VOICE_PAGE){
                    voiceAdapter.notifyDataSetChanged();
                    LOADING_VOICE_PAGE++;
                    loadingVoice=false;
                }
                if (msg.what==LOADING_VOICE_PAGE_FAILED){
                    Toast.makeText(context,"网络链接出错！",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public String getNextPageUrl(){
        String nextPageUrl=null;
        Pattern p=Pattern.compile("<a  class=\"curr_page\"  href.*?<a  href=\"(.*?)\">[0-9]+</a>");
        Matcher m=p.matcher(currentPageHtml);
        boolean ifFind=m.find();
        if (ifFind){
            nextPageUrl=m.group(1);
        }
        return nextPageUrl;
    }

    @Override
    public void onDestroyView() {
        Log.d("VOiceFragment","ON_DESTORY_VIEW!");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("VoiceFragment","ON_DESTORY!");
    }
}
