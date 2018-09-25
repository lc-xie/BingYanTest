package com.example.stephen.bingyantest.HttpUtil;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stephen on 17-7-21.
 * 音频文件获取工具类
 * 文件不在本地则从网页下载到本地，并返回本地文件路径
 */

public class VoiceDownloadTool {
    public static String downloadVoice(String urlStr, String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        if (file.exists()) {   //若文件存在，则直接读取
            return file.getAbsolutePath();
        } else {     //不存在则下载
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                URL resultUrl = new URL(urlStr);
                HttpURLConnection connection = null;
                connection = (HttpURLConnection) resultUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.setRequestProperty("Content-Type", "audio/mpeg");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36");
                inputStream = connection.getInputStream();
                //创建临时文件，文件名后面加上"(下载中)"，待文件下载完成之后再重命名
                File tempFile = new File(Environment.getExternalStorageDirectory(), fileName + "(下载中)");
                //若存在临时文件，说明上次下载未完成，删除未完成的文件，重新下载
                tempFile.createNewFile();
                outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[4 * 1024];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
                tempFile.renameTo(file);
                return file.getAbsolutePath();
            } catch (Exception e) {
                System.out.println("下载voice时，发送GET请求出现异常！" + e);
                e.printStackTrace();
                return null;
            }
        }
    }
}
