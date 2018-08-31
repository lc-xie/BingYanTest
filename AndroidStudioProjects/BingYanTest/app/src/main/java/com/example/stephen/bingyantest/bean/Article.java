package com.example.stephen.bingyantest.bean;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by stephen on 17-7-6.
 */

public class Article {
    private String articalTitle;
    private String articalAuthor;
    private String articalContent;
    private String articleNextRandom;   //随机文章链接

    public String getArticleNextRandom() {
        return articleNextRandom;
    }

    public void setArticleNextRandom(String articleNextRandom) {
        this.articleNextRandom = articleNextRandom;
    }

    public String getArticalTitle() {
        return articalTitle;
    }

    public void setArticalTitle(String articalTitle) {
        this.articalTitle = articalTitle;
    }

    public String getArticalAuthor() {
        return articalAuthor;
    }

    public void setArticalAuthor(String articalAuthor) {
        this.articalAuthor = articalAuthor;
    }

    public String getArticalContent() {
        return articalContent;
    }

    public void setArticalContent(String articalContent) {
        this.articalContent = articalContent;
    }
}
