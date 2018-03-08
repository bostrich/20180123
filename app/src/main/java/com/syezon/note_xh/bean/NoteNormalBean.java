package com.syezon.note_xh.bean;

import android.content.Context;
import android.content.Intent;
import android.text.Html;

import com.syezon.note_xh.activity.AddNoteActivity;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.db.NoteEntity;
import com.syezon.note_xh.utils.LogUtil;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

/**
 *
 */

public class NoteNormalBean extends BaseNoteBean{

    private static final String TAG = NoteNormalBean.class.getName();
    private DbModel dbModel;

    public NoteNormalBean() {}

    public NoteNormalBean(DbModel dbModel) {
        this.dbModel = dbModel;
    }

    @Override
    public String getTime() {
        return dbModel.getString("time");
    }

    @Override
    public String getWeather() {
        return dbModel.getString("weather");
    }

    @Override
    public String getPicUrl() {
        return dbModel.getString("imagepath");
    }

    @Override
    public String getTitle() {
        return dbModel.getString("title");
    }

    @Override
    public String getDesc() {
        String temp = dbModel.getString("content");
        String result = "";
        Html.fromHtml(temp);
        if(temp != null){
            String[] split = Html.fromHtml(temp).toString().split("\n");
            int count = 0;
            for (int i = 0; i < split.length; i++) {
                LogUtil.e(TAG, split[i]);
                if(!split[i].startsWith("<img") && count < 3){
                    result += split[i] + "\n";
                    count ++;
                }
            }
        }
        return result;
    }

    @Override
    public boolean hasImage() {
        return dbModel.getBoolean("hasimage");
    }

    @Override
    public boolean isCollected() {
        return dbModel.getBoolean("iscollect");
    }

    @Override
    public boolean isCompleted() {
        return dbModel.getBoolean("iscomplete");
    }

    @Override
    public DbModel getDbModel() {
        return dbModel;
    }

    @Override
    public void click(Context context) {
        try {
            NoteEntity noteEntity = NoteApplication.dbManager.findById(NoteEntity.class, dbModel.getInt("_id"));
            Intent intent = new Intent(context, AddNoteActivity.class);
            intent.putExtra("note_entity", noteEntity);
            context.startActivity(intent);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show(Context context) {

    }
}
