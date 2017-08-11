package com.example.stephen.bingyantest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stephen.bingyantest.HttpRequest.VoiceDownloadTool;
import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.bean.Voice;
import com.example.stephen.bingyantest.service.MusicService;
import com.google.gson.Gson;

public class VoicePlayActivity extends AppCompatActivity {

    private TextView voiceName;
    private TextView voiceAuthor;
    private Voice voice;
    /**
     * mediaPlay相关变量
     */
    private TextView startTime,endTime;//显示歌曲当前播放时间和总时间
    private ImageView buttonImage;//播放按钮
    private SeekBar seekBar;//进度条
    private int position=0;//当前播放位置
    //action
    public static final String CHANGE_SEEKBAR="com.example.stephen.bingyantest.action.CHANGE_SEEKBAR";
    public static final String SET_SB_MAX="com.example.stephen.bingyantest.action.SET_SB_MAX";
    public static final String CLOSE_ACTIVITY="com.example.stephen.bingyantest.action.CLOSE_VOICE";//点击通知栏的X时，关闭此播放活动
    public static final String PLAY_MUSIC="com.example.stephen.bingyantest.action.PLAY_VOICE";//点击播放、暂停按钮，控制播放
    public static final String TOUCHING_CHANGE_PROCESS= "com.example.stephen.bingyantest" +
            ".action.TOUCHING_CHANGE_PROCESS";//滑动seekbar时，通知service改变歌曲进度
    public static final String STOP_MUSIC="com.example.stephen.bingyantest.action.STOP_MUSIC";//暂停按钮，通知activity停止播放

    private TextChangeReceiver textChangeReceiver;//广播接收器
    private boolean buttonOnClickable=false;
    private static int DOWNLOAD_VOICE_FILE=4;
    //录音文件相关
    private String filePath;
    private String voiceLoadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_play);
        initVoice();
        initMediaPlay();
        //初始化头布局
        ImageView back;
        TextView topText;
        topText=(TextView)findViewById(R.id.top_text);
        topText.setText("播放");
        back=(ImageView)findViewById(R.id.back_book_detail);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PlayVoiceActivity","Clicked back button!");
                finish();
            }
        });

        //按键响应
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonOnClickable){
                    Toast.makeText(VoicePlayActivity.this,"Voice file is loading...",Toast.LENGTH_SHORT).show();
                    return;
                }
                playPauseVoice();
            }
        });

        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==DOWNLOAD_VOICE_FILE){
                    buttonOnClickable=true;
                    //开启播放音乐的服务
                    Intent bindIntent=new Intent(VoicePlayActivity.this,MusicService.class);
                    bindIntent.putExtra("voice_author",voice.getVoiceAuthor());
                    bindIntent.putExtra("voice_name",voice.getVoiceName());
                    bindIntent.putExtra("voice_path",filePath);
                    startService(bindIntent);
                }
            }
        };
        //获取录音文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                String voiceFileName;
                voiceFileName=voice.getVoiceName()+".mp3";
                filePath= VoiceDownloadTool.downloadVoice(voiceLoadUrl,voiceFileName);
                if (filePath!=null){
                    Message message=new Message();
                    message.what=DOWNLOAD_VOICE_FILE;
                    handler.sendMessage(message);
                }else {
                    Toast.makeText(VoicePlayActivity.this,"获取音频文件失败！",Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }

    public void initVoice(){
        Intent intent=getIntent();
        String voiceJsonStr=intent.getStringExtra("voiceJsonStr");
        Gson gson=new Gson();
        voice=gson.fromJson(voiceJsonStr,Voice.class);
        voiceName=(TextView)findViewById(R.id.text_name_voice);
        voiceAuthor=(TextView)findViewById(R.id.text_author_voice);
        voiceAuthor.setText(voice.getVoiceAuthor());
        voiceName.setText(voice.getVoiceName());
        voiceLoadUrl=voice.getVoiceDownloadUrl();
    }

    public void initMediaPlay(){
        //初始化UI
        startTime=(TextView)findViewById(R.id.text_start_time);
        endTime=(TextView)findViewById(R.id.text_end_time);
        startTime.setText("00:00");
        endTime.setText("00:00");
        buttonImage=(ImageView)findViewById(R.id.button_voice_play);
        //初始化广播相关控件
        //初始化seekbar
        seekBar=(SeekBar)findViewById(R.id.progress_voice_play);
        seekBar.setOnSeekBarChangeListener(new MySeekBarChangeListener());
    }

    /**
     * 播放、暂停
     */
    public void playPauseVoice(){
        Intent playIntent=new Intent(PLAY_MUSIC);
        sendBroadcast(playIntent);
    }

    /**
     * 广播接收器
     */
    public class TextChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if (action==CLOSE_ACTIVITY){
                finish();
            }else if (intent.getAction()==SET_SB_MAX){
                seekBar.setMax(intent.getIntExtra("max_seekbar",0));
                endTime.setText(calTime(intent.getIntExtra("max_seekbar",0)));
            }else if (intent.getAction()==CHANGE_SEEKBAR){
                position=intent.getIntExtra("position",2);
                seekBar.setProgress(position);
                startTime.setText(calTime(position));
                buttonImage.setImageResource(R.drawable.ic_voice_pause);
            }else if (intent.getAction()==STOP_MUSIC){
                buttonImage.setImageResource(R.drawable.ic_voice_play);
            }
        }
    }

    /**
     * seekbar手动滑动时的监听器
     */
    public class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            position=seekBar.getProgress();
            //通知服务，seekbar滑动改变了歌曲进度
            Intent touchingChangeProcessIntent=new Intent(TOUCHING_CHANGE_PROCESS);
            touchingChangeProcessIntent.putExtra("touching_position",position);
            sendBroadcast(touchingChangeProcessIntent);
        }
    }

    /**
     * 将duration转换成分秒形式
     * @param druation
     * @return 歌曲当前时间
     */
    public String calTime(int druation){
        int min,sec,allSec;
        allSec=druation/1000+1;
        min=allSec/60;
        sec=allSec%60;
        String minStr,secStr;
        if (min==0){
            minStr="00";
        }else minStr=""+min;
        if (sec<10){
            secStr="0"+sec;
        }else secStr=""+sec;
        return minStr+":"+secStr;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter;//过滤器
        intentFilter=new IntentFilter();
        intentFilter.addAction(CLOSE_ACTIVITY);
        intentFilter.addAction(CHANGE_SEEKBAR);
        intentFilter.addAction(SET_SB_MAX);
        intentFilter.addAction(STOP_MUSIC);
        textChangeReceiver=new TextChangeReceiver();
        registerReceiver(textChangeReceiver,intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(textChangeReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(VoicePlayActivity.this,MusicService.class));
        super.onDestroy();
    }
}
