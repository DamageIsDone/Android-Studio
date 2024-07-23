package com.example.dbapplication;

public class News {
    String title; //新闻标题
    String date; //发布时间
    String content; //新闻内容

    public News() {
    }
    public News(String title, String date, String content) {
        this.title = title;
        this.date = date;
        this.content = content;
    }
//添加getter、setter…

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }
}