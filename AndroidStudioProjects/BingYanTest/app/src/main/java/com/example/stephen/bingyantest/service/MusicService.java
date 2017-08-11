package com.example.stephen.bingyantest.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.stephen.bingyantest.R;
import com.example.stephen.bingyantest.activity.VoicePlayActivity;

import java.io.File;
import java.io.IOException;

public class MusicService extends Service {
    public MusicService() {
    }

    private MediaPlayer mediaPlayer;
    private int position=0;
    private MusicServiceREceiver myReceiver;
    private boolean onPrepareed=false;
    private String voiceName;
    private String voiceAuthor;

    //notification
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;

    public static final String CHANGE_SEEKBAR="com.example.stephen.bingyantest.action.CHANGE_SEEKBAR";
    public static final String SET_SB_MAX="com.example.stephen.bingyantest.action.SET_SB_MAX";
    public static final String CLOSE_ACTIVITY="com.example.stephen.bingyantest.action.CLOSE_VOICE";//点击通知栏的X时，关闭此播放活动
    public static final String PLAY_MUSIC="com.example.stephen.bingyantest.action.PLAY_VOICE";//点击播放、暂停按钮，控制播放
    public static final String TOUCHING_CHANGE_PROCESS= "com.example.stephen.bingyantest" +
            ".action.TOUCHING_CHANGE_PROCESS";//滑动seekbar时，通知service改变歌曲进度
    public static final String STOP_MUSIC="com.example.stephen.bingyantest.action.STOP_MUSIC";//暂停按钮，通知activity停止播放

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注冊廣播接收器
        myReceiver=new MusicServiceREceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(PLAY_MUSIC);
        intentFilter.addAction(TOUCHING_CHANGE_PROCESS);
        registerReceiver(myReceiver,intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        //获取voice相关信息
        File file=new File(intent.getStringExtra("voice_path"));
        if (!file.exists())Log.d("MusicService","File don't exist!!!!");
        voiceAuthor=intent.getStringExtra("voice_author");
        voiceName=intent.getStringExtra("voice_name");
        //初始化mediaplay
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d("MusicService","Play Error!!!!!!!!!");
                return false;
            }
        });
        try {
            mediaPlayer.setDataSource(file.getPath());
        }catch (IOException e){
            e.printStackTrace();
            Log.d("MusicService","SetDataSource Error!!!!!!!!!");
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                onPrepareed=true;
                Intent setSeekbarMaxIntent =new Intent(SET_SB_MAX);
                setSeekbarMaxIntent.putExtra("max_seekbar",mediaPlayer.getDuration());
                sendBroadcast(setSeekbarMaxIntent);
            }
        });
        //初始化notification
        refreshNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1&&(mediaPlayer.isPlaying())){
                //发送广播更新activity中的seekbar进度
                Intent changeSeekbarIntent =new Intent(CHANGE_SEEKBAR);
                position=mediaPlayer.getCurrentPosition();
                changeSeekbarIntent.putExtra("position",position);
                sendBroadcast(changeSeekbarIntent);
                //隔一秒再发送一条消息，更新
                handler.sendEmptyMessageDelayed(1,1000);
                refreshNotification();
            }
        }
    };

    /**
     * 广播接收器
     */
    public class MusicServiceREceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()==PLAY_MUSIC){
                if (!mediaPlayer.isPlaying()){
                    if (position!=0){
                        mediaPlayer.seekTo(position);
                    }
                    mediaPlayer.start();
                    refreshNotification();
                    Message message=new Message();
                    message.what=1;
                    handler.sendMessage(message);
                }else if (mediaPlayer.isPlaying()){
                    position=mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                    refreshNotification();
                    Intent stopPlayIntent=new Intent(STOP_MUSIC);
                    sendBroadcast(stopPlayIntent);
                }
            }else if (intent.getAction()==TOUCHING_CHANGE_PROCESS){
                position=intent.getIntExtra("touching_position",1);
                mediaPlayer.seekTo(position);
            }
        }
    }

    /**
     * 更新notification
     */
    public void refreshNotification(){
        notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent closeAvtivityIntent=new Intent(CLOSE_ACTIVITY);
        PendingIntent closeAvtivityPendingIntent=PendingIntent.getBroadcast(MusicService.this,0,closeAvtivityIntent,0);
        Intent playMusicIntent=new Intent(PLAY_MUSIC);
        PendingIntent playMusicPendingIntent=PendingIntent.getBroadcast(MusicService.this,0,playMusicIntent,0);
        //自定义布局
        RemoteViews views=new RemoteViews(getPackageName(),R.layout.notification_layout);
        views.setTextViewText(R.id.notification_voice_name,voiceName);
        views.setTextViewText(R.id.notification_voice_author,voiceAuthor);
        views.setOnClickPendingIntent(R.id.notification_close,closeAvtivityPendingIntent);
        views.setOnClickPendingIntent(R.id.notification_play,playMusicPendingIntent);
        if (mediaPlayer.isPlaying()){
            views.setImageViewResource(R.id.notification_play,R.drawable.notification_voice_pause);
        }else {
            views.setImageViewResource(R.id.notification_play,R.drawable.notification_voice_play);
        }
        views.setProgressBar(R.id.progressbar,mediaPlayer.getDuration(),mediaPlayer.getCurrentPosition(),false);
        //点击notificaation回到play活动
        Intent restartBroadcastIntent=new Intent(MusicService.this,VoicePlayActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(MusicService.this,1,restartBroadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder=new NotificationCompat.Builder(MusicService.this);
        mBuilder.setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .setContent(views)
                .setOngoing(true);
        notificationManager.notify(1,mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        notificationManager.cancel(1);
        unregisterReceiver(myReceiver);
    }
}
