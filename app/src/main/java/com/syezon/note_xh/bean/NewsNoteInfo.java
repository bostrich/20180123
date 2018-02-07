package com.syezon.note_xh.bean;

import android.content.Context;
import android.content.Intent;

import com.syezon.note_xh.activity.NewsActivity;
import com.syezon.note_xh.utils.WebHelper;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class NewsNoteInfo extends BaseNoteBean {

    private List<BLNewsBean> news = new ArrayList<>();
    private int position;

    public NewsNoteInfo() { }

    public NewsNoteInfo(List<BLNewsBean> news) {
        this.news = news;
    }

    public List<BLNewsBean> getNews() {
        return news;
    }

    public void setNews(List<BLNewsBean> news) {
        this.news = news;
    }

    public boolean isValid(){
        return news.size() > 0;
    }

    @Override
    public String getTime() {
        return System.currentTimeMillis() + "";
    }

    @Override
    public String getWeather() {
        return "tianqi";
    }

    @Override
    public String getPicUrl() {
        return news.get(position++ % news.size()).getImages().get(0);
    }

    @Override
    public String getTitle() {
        return news.get(position % news.size()).getTitle();
    }

    @Override
    public String getDesc() {
        return news.get(position % news.size()).getDescription();
    }

    @Override
    public boolean hasImage() {
        return true;
    }

    @Override
    public boolean isCollected() {
        return false;
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public DbModel getDbModel() {
        return null;
    }

    @Override
    public void click(final Context context) {
        WebHelper.showNoteNews(context, news.get((position -1) % news.size()).getTitle()
                , news.get((position -1) % news.size()).getUrl(), new WebHelper.SimpleWebLoadCallBack(){
                    @Override
                    public void backClick() {
                        Intent intent = new Intent(context, NewsActivity.class);
                        context.startActivity(intent);
                    }
                });
    }
}
