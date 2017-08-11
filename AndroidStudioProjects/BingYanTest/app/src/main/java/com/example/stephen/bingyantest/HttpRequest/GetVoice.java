package com.example.stephen.bingyantest.HttpRequest;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stephen on 17-7-15.
 */

public class GetVoice {

    public static String getVoiceMainHtml(String html){
        Pattern p=Pattern.compile("<div class=\"img_list\">(.*?)<div class=\"page_num\">");
        Matcher m=p.matcher(html);
        String voiceMainHtml=null;
        if (m.find()) {
            voiceMainHtml = m.group();
        }
        return voiceMainHtml;
    }

    public static List<String> getAuthorHtmlList(String html){
        String mHtml=getVoiceMainHtml(html);
        if (mHtml!=null){
            List<String> authorHtmlList=new ArrayList<>();
            Pattern p=Pattern.compile("<div class=\"author_name\">(.*?)</div>");
            Matcher m=p.matcher(mHtml);
            Boolean isFind = m.find();
            while (isFind) {
                String temp = m.group(1);
                temp=temp.replaceAll(" ","");
                temp=temp.replaceAll("&nbsp;&nbsp;","    ");
                //添加成功匹配的成果
                authorHtmlList.add(temp);
                //继续查找下一个对象
                isFind = m.find();
            }
            return authorHtmlList;
        }
        return null;
    }

    public static List<String> getNameHtmlList(String html){
            String mHtml=getVoiceMainHtml(html);
        if (mHtml!=null){
            List<String> nameHtmlList=new ArrayList<>();
            Pattern p=Pattern.compile("<div class=\"list_author\">.*?target=\"_blank\" >(.*?)</a>");
            Matcher m=p.matcher(mHtml);
            Boolean isFind = m.find();
            while (isFind) {
                String temp = m.group(1);
                temp=temp.replaceAll(" ","");
                //添加成功匹配的成果
                nameHtmlList.add(temp);
                //继续查找下一个对象
                isFind = m.find();
            }
            return nameHtmlList;
        }
        return null;
    }

    public static List<String> getTagList(String html){
        String mHtml=getVoiceMainHtml(html);
        if (mHtml!=null){
            List<String> nameHtmlList=new ArrayList<>();
            Pattern p=Pattern.compile("<span class=\"voice_tag\">(.*?)</span>");
            Matcher m=p.matcher(mHtml);
            Boolean isFind = m.find();
            while (isFind) {
                String temp = m.group(1);
                //添加成功匹配的成果
                nameHtmlList.add(temp);
                //继续查找下一个对象
                isFind = m.find();
            }
            return nameHtmlList;
        }
        return null;
    }

    public static List<String> getImageUrlList(String html){
        String mHtml=getVoiceMainHtml(html);
        if (mHtml!=null){
            List<String> imageUrlList=new ArrayList<>();
            Pattern p=Pattern.compile("<img width=\"250px\" src=\"(.*?)\"/>");
            Matcher m=p.matcher(mHtml);
            Boolean isFind = m.find();
            while (isFind) {
                String temp ="http://voice.meiriyiwen.com/" + m.group(1);
                //添加成功匹配的成果
                imageUrlList.add(temp);
                //继续查找下一个对象
                isFind = m.find();
            }
            return imageUrlList;
        }
        return null;
    }

    public static List<String> getVoiceUrlList(String html){
        String mHtml = getVoiceMainHtml(html);
        if (mHtml!=null){
            List<String> voiceUrlList = new ArrayList<>();
            Pattern p = Pattern.compile("<a class=\"box_list_img cover_play\" href=\"(.*?)\" target=\"_blank\">");
            Matcher m = p.matcher(mHtml);
            Boolean isFind = m.find();
            while (isFind) {
                String temp = "http://voice.meiriyiwen.com/" + m.group(1);
                //添加成功匹配的成果
                voiceUrlList.add(temp);
                //继续查找下一个对象
                isFind = m.find();
            }
            return voiceUrlList;
        }
        return null;
    }

    public static List<String> getVoiceDownloadList(List<String> playUrlList){
        List<String> voiceDownloadUrlList=new ArrayList<>();
        if (playUrlList!=null) {
            for (int i = 0; i < playUrlList.size(); i++) {
                String url = playUrlList.get(i);
                String html = GetVoicePlayHtml.sendHttpRequest(url);
                Pattern p = Pattern.compile("<audio src=\"(.*?)\" preload=\"none\"");
                Matcher m = p.matcher(html);
                Boolean isFind = m.find();
                if (isFind) {
                    String temp = m.group(1);
                    //添加成功匹配的成果
                    voiceDownloadUrlList.add(temp);
                }
            }
        }
        return voiceDownloadUrlList;
    }

}
