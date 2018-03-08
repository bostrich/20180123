package com.syezon.note_xh.bean;

import android.content.Context;
import android.content.Intent;

import com.syezon.note_xh.Config.AppConfig;
import com.syezon.note_xh.activity.NewsActivity;
import com.syezon.note_xh.fragment.NewsFragment;
import com.syezon.note_xh.utils.StatisticUtils;
import com.syezon.note_xh.utils.WebHelper;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class NoteNewsInfo extends BaseNoteBean {

    private List<BaseNewInfo> news = new ArrayList<>();
    private int position;
    private String source;
    private boolean reportShow;

    public NoteNewsInfo() { }

    public NoteNewsInfo(List<BaseNewInfo> news) {
        this.news = news;
    }

    public List<BaseNewInfo> getNews() {
        return news;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setNews(List<BaseNewInfo> news) {
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
        return "note_new";
    }

    @Override
    public String getPicUrl() {
        return news.get(position % news.size()).getImages().get(0);
    }

    @Override
    public String getTitle() {
        return news.get(position % news.size()).getSource();
    }

    @Override
    public String getDesc() {
        return news.get(position % news.size()).getTitle();
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
        position ++;
        return false;
    }

    @Override
    public DbModel getDbModel() {
        return null;
    }

    @Override
    public void click(final Context context) {
        StatisticUtils.report(context, StatisticUtils.ID_NOTE_NEWS, StatisticUtils.EVENT_CLICK, AppConfig.NEWS_SOURCE);
        WebHelper.showNoteNews(context, news.get((position -1) % news.size()).getTitle()
                , news.get((position -1) % news.size()).getUrl(), new WebHelper.SimpleWebLoadCallBack(){
                    @Override
                    public void backClick() {
                        Intent intent = new Intent(context, NewsActivity.class);
                        intent.putExtra(NewsFragment.NEWS_SOURCE, source);
                        context.startActivity(intent);
                    }
                });
    }

    @Override
    public void show(Context context) {
        if(!reportShow) {
            StatisticUtils.report(context, StatisticUtils.ID_NOTE_NEWS, StatisticUtils.EVENT_IMAGE_SHOW, AppConfig.NEWS_SOURCE);
            reportShow = true;
        }
    }
}
