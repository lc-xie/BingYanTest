package com.example.stephen.bingyantest.imageThreeCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.stephen.bingyantest.bean.Article;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by stephen on 17-7-11.
 * 文件读写
 */

public class FileUtil {

    private final static String TAG = FileUtil.class.getSimpleName();

    public final static String APP_PATH = Environment.getExternalStorageDirectory() + "/每日一文";
    public final static String ARTICLE_PATH = APP_PATH + "/Article";
    public final static String IMAGE_PATH = APP_PATH + "/Image";

    public FileUtil() {
        File appDir = new File(APP_PATH);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        File articleDir = new File(ARTICLE_PATH);
        if (!articleDir.exists()) {
            articleDir.mkdirs();
        }
        File bookImageDir = new File(IMAGE_PATH);
        if (!bookImageDir.exists()) {
            bookImageDir.mkdirs();
        }
    }

    public void saveImage(String fileName, Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "图片为空，无法保存");
            return;
        }
        putImageToFile(bitmap, fileName, IMAGE_PATH);
        Log.d(TAG, "save image " + fileName + " to " + IMAGE_PATH + " successful!");
    }

    public Bitmap getImageFromFile(String name) throws IOException {
        return getImageFromFile(name, IMAGE_PATH);
    }

    public void saveArticle(Article article) {
        File file = new File(ARTICLE_PATH, article.getArticalTitle() + ".txt");
        try {
            if (file.exists()) {
                return;
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] data = (article.getArticalTitle() + "\n").getBytes();
            fos.write(data);
            byte[] data1 = (article.getArticalAuthor() + "\t\n\n").getBytes();
            fos.write(data1);
            byte[] data2 = (article.getArticalContent() + "\t\n\n").getBytes();
            fos.write(data2);
            fos.close();
        } catch (IOException e) {
            Log.e(TAG,  "Save article 《" + article.getArticalTitle() + "》 failed!");
            e.printStackTrace();
        }
    }

    /**
     * 将bitmap保存到本地
     *
     * @param bitmap bitmap
     * @param fileName 保存的文件名
     * @param path 路径
     */
    private void putImageToFile(Bitmap bitmap, String fileName, String path) {
        FileOutputStream outputStream;
        try {
            File file = new File(path, fileName);
            if (!file.exists()) file.createNewFile();
            outputStream = new FileOutputStream(file);
            // 将图像写到流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从本地文件获取image
     *
     * @param name image 名字
     * @param path 路径
     * @return bitmap
     */
    private Bitmap getImageFromFile(String name, String path) {
        Bitmap bitmap = null;
        File file = new File(path, name);
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return bitmap;
    }

    private String newGetCacheDir(Context context, String lastPath) {
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
