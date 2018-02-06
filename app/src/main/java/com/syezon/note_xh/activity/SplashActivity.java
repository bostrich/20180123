package com.syezon.note_xh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;

import com.syezon.note_xh.Config.AppConfig;
import com.syezon.note_xh.R;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.db.MyDataBase;
import com.syezon.note_xh.db.Note;
import com.syezon.note_xh.db.NoteEntity;
import com.syezon.note_xh.utils.DateUtils;
import com.syezon.note_xh.utils.PreferenceKeyUtils;
import com.syezon.note_xh.utils.SharedPerferencesUtil;

import org.xutils.ex.DbException;

import java.util.List;

public class SplashActivity extends AppCompatActivity{
    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
//        Log.d("DeviceInfo", TestUtils.getDeviceInfo(this));

        //将1.0版本的数据库数据迁移过来
        if(!SharedPerferencesUtil.getBooleanData(this, PreferenceKeyUtils.ISUPDATE,false)){
            MyDataBase mDb=new MyDataBase(this);
            List<Note> noteList=mDb.getAllNoteList();
            if(noteList!=null&&noteList.size()>0){
                for(int i=0;i<noteList.size();i++){
                    Note note=noteList.get(i);
                    NoteEntity noteEntity=new NoteEntity();
                    //将老便签数据事件转换为时间戳
                    String date=note.getDate();
                    String time=note.getTime();
                    String newTime=date+"-"+time;
                    noteEntity.setTime(DateUtils.getTimeStamp(newTime,"yyyy-M-d-H:mm"));
//                    Log.e("tttt",DateUtils.getTimeStamp(newTime,"yyyy-M-d-H:mm"));
                    noteEntity.setTitle("");
                    noteEntity.setContent(note.getContent());

                    noteEntity.setHasImage(false);
                    noteEntity.setWeatherStr("tianqi");
                    noteEntity.setSortName("");
                    //为老便签数据添加简要内容，即前两行内容
                    String content=note.getContent();
                    String[] strings=content.split("\n");
                    String briefContent =strings[0];
                    if(strings.length>1){
                        briefContent+=strings[1];
                    }
                    noteEntity.setBriefContent(briefContent);
                    noteEntity.setCollect(false);
                    try {
                        NoteApplication.dbManager.saveBindingId(noteEntity);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }

            SharedPerferencesUtil.saveBooleanData(this, PreferenceKeyUtils.ISUPDATE,true);
//            mDb.getDb().execSQL("DROP TABLE IF EXISTS NOTES");
            deleteDatabase("note.db");
        }

        //开屏展示0.5S
       new Thread(new Runnable() {
           @Override
           public void run() {
               handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       if(!TextUtils.isEmpty(SharedPerferencesUtil.getStringData(SplashActivity.this, PreferenceKeyUtils.PASSWORD,""))){
                           skipToPasswordActivity();
                       }else {
                           skipToShowPageActivity();
                       }
                   }
               },4000);
           }
       }).start();

        //获取参数配置
        AppConfig.getParams(this);
    }

    //跳转到ShowPageActivity
    private void skipToShowPageActivity() {
        Intent intent = new Intent(this, ShowPageActivity.class);
        startActivity(intent);
        finish();
    }

    //跳转到PasswordActivity
    private void skipToPasswordActivity() {
        Intent intent = new Intent(this, PasswordActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
