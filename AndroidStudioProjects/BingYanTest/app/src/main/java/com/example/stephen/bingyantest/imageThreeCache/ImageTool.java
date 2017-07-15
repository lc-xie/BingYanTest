package com.example.stephen.bingyantest.imageThreeCache;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by stephen on 17-7-11.
 */

public class ImageTool {

    private static LruCacheHelper lruCacheHelper;
    private static FileUtil fileUtilHelper;
    //private ImageCallBack imageCallBack;

    public ImageTool(LruCacheHelper lruCacheHelper,FileUtil fileUtilHelper){
        this.lruCacheHelper=lruCacheHelper;
        this.fileUtilHelper=fileUtilHelper;
    }

    public Bitmap getBitmap(final String url, final ImageCallBack imageCallBack)throws IOException {
        Bitmap bitmap=null;
        final String imageName=url.substring(url.lastIndexOf(File.separator)+1);
        if (lruCacheHelper.get(imageName)!=null){
            return lruCacheHelper.get(imageName);
        }else if(fileUtilHelper.getImageFromStorage(imageName)!=null){
            bitmap =fileUtilHelper.getImageFromStorage(imageName);
            lruCacheHelper.put(imageName, bitmap);   /** 将Bitmap 加入内存缓存*/
            return bitmap;
        }else {
            final Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    imageCallBack.imageLoadded((Bitmap)msg.obj, url);
                }
            };
            new Thread(new Runnable() {

                @Override
                public void run() {
                    Bitmap bitmap = ImageHttpClient.getImageFromNet(url);
                    if(bitmap!=null){
                        /** 保存在SD卡或者手机目录*/
                        fileUtilHelper.putImageToStorage(imageName, bitmap);
                        lruCacheHelper.put(imageName, bitmap);   /** 将Bitmap 加入内存缓存*/
                        Message msg = handler.obtainMessage();
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    }
                    return;
                }
            }).start();
        }
        return bitmap;
    }
}
