package com.example.stephen.bingyantest.imageThreeCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by stephen on 17-7-11.
 */

public class FileUtil {
    /**
     * 手机的缓存根目录
     */
    private static String mDataRootPath = null;
    /**
     * 保存Image的目录名
     */
    private final static String FOLDER_NAME = "/AndroidImage";

    /**
     * 保存Image的absoulate目录
     */
    private String filePath=null;

    public FileUtil(Context context,String lastPath){
        /*mDataRootPath = context.getCacheDir().getPath();
        filePath=mDataRootPath + FOLDER_NAME;*/
        filePath=newGetCacheDir(context,lastPath);
    }

    /**
     * save image to phone filePath
     */
    public void putImageToStorage(String fileName, Bitmap bitmap){
        if(bitmap == null){
            return;
        }
        FileOutputStream outputStream;
        try {
            File file1 = new File(filePath);
            file1.mkdirs();
            //打印文件路径名
            //Log.d("FileUtilHelper:",file1.getPath());
            File file=new File(file1,fileName);
            if (!file.exists())file.createNewFile();
            outputStream = new FileOutputStream(file);
            // 将图像写到流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.d("FileUtilHelper","save image "+fileName+" to storage successful!");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * get image from phone storage
     * @param fileName
     * @return bitmap
     * @throws IOException
     */
    public  Bitmap getImageFromStorage(String fileName)throws IOException{
        Bitmap bitmap = null;
        File file=new File(filePath,fileName);
        if(file.exists())
        {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return bitmap;
    }

    private String  newGetCacheDir(Context context,String lastPath) {
        String state = Environment.getExternalStorageState();
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // 有sd卡
            dir = new File(Environment.getExternalStorageDirectory(), "/Android/data/" + context.getPackageName()
                    + lastPath);
        } else {
            // 没有sd卡
            dir = new File(context.getCacheDir(), lastPath);
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath().toString();
    }

}
