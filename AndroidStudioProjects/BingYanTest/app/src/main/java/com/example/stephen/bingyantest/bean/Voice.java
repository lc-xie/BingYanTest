package com.example.stephen.bingyantest.bean;

/**
 * Created by stephen on 17-7-15.
 */

public class Voice {
    private String voiceName;
    private String voiceAuthor;
    private String voiceNumber;
    private String voiceImageUrl;
    private String voicePlayUrl;//进入播放网页页面的链接

    public String getVoiceDownloadUrl() {
        return voiceDownloadUrl;
    }

    private String voiceDownloadUrl;//音乐下载链接

    public String getVoiceName() {
        return voiceName;
    }

    public String getVoiceAuthor() {
        return voiceAuthor;
    }

    public String getVoiceNumber() {
        return voiceNumber;
    }

    public String getVoiceImageUrl() {
        return voiceImageUrl;
    }

    public String getVoicePlayUrl() {
        return voicePlayUrl;
    }

    public void setVoiceDownloadUrl(String voiceDownloadUrl) {
        this.voiceDownloadUrl = voiceDownloadUrl;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public void setVoiceAuthor(String voiceAuthor) {
        this.voiceAuthor = voiceAuthor;
    }

    public void setVoiceNumber(String voiceNumber) {
        this.voiceNumber = voiceNumber;
    }

    public void setVoiceImageUrl(String voiceImageUrl) {
        this.voiceImageUrl = voiceImageUrl;
    }

    public void setVoicePlayUrl(String voicePlayUrl) {
        this.voicePlayUrl = voicePlayUrl;
    }
}
