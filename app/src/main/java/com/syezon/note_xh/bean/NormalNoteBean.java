package com.syezon.note_xh.bean;

import android.content.Context;
import android.content.Intent;

import com.syezon.note_xh.activity.AddNoteActivity;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.db.NoteEntity;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

/**
 *
 */

public class NormalNoteBean extends BaseNoteBean{

    private DbModel dbModel;

    public NormalNoteBean() {}

    public NormalNoteBean(DbModel dbModel) {
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
        return dbModel.getString("briefcontent");
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

}
