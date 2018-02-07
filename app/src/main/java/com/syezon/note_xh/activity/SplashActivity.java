package com.syezon.note_xh.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.syezon.note_xh.Config.AppConfig;
import com.syezon.note_xh.R;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.bean.AdInfo;
import com.syezon.note_xh.db.MyDataBase;
import com.syezon.note_xh.db.Note;
import com.syezon.note_xh.db.NoteEntity;
import com.syezon.note_xh.utils.DateUtils;
import com.syezon.note_xh.utils.PreferenceKeyUtils;
import com.syezon.note_xh.utils.SharedPerferencesUtil;
import com.syezon.note_xh.utils.WebHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.img_icon)
    ImageView imgIcon;
    private Handler handler = new Handler();
    private boolean isClick;
    private AdInfo adInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
//        Log.d("DeviceInfo", TestUtils.getDeviceInfo(this));
        ButterKnife.bind(this);
        setViews();

        //将1.0版本的数据库数据迁移过来
        if (!SharedPerferencesUtil.getBooleanData(this, PreferenceKeyUtils.ISUPDATE, false)) {
            MyDataBase mDb = new MyDataBase(this);
            List<Note> noteList = mDb.getAllNoteList();
            if (noteList != null && noteList.size() > 0) {
                for (int i = 0; i < noteList.size(); i++) {
                    Note note = noteList.get(i);
                    NoteEntity noteEntity = new NoteEntity();
                    //将老便签数据事件转换为时间戳
                    String date = note.getDate();
                    String time = note.getTime();
                    String newTime = date + "-" + time;
                    noteEntity.setTime(DateUtils.getTimeStamp(newTime, "yyyy-M-d-H:mm"));
//                    Log.e("tttt",DateUtils.getTimeStamp(newTime,"yyyy-M-d-H:mm"));
                    noteEntity.setTitle("");
                    noteEntity.setContent(note.getContent());

                    noteEntity.setHasImage(false);
                    noteEntity.setWeatherStr("tianqi");
                    noteEntity.setSortName("");
                    //为老便签数据添加简要内容，即前两行内容
                    String content = note.getContent();
                    String[] strings = content.split("\n");
                    String briefContent = strings[0];
                    if (strings.length > 1) {
                        briefContent += strings[1];
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

            SharedPerferencesUtil.saveBooleanData(this, PreferenceKeyUtils.ISUPDATE, true);
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
                        if (adInfo != null && isClick) {
                            WebHelper.showNoteNews(SplashActivity.this, adInfo.getName(), adInfo.getUrl()
                                    , new WebHelper.SimpleWebLoadCallBack() {
                                        @Override
                                        public void backClick() {
                                            if (!TextUtils.isEmpty(SharedPerferencesUtil.getStringData(SplashActivity.this, PreferenceKeyUtils.PASSWORD, ""))) {
                                                skipToPasswordActivity();
                                            } else {
                                                skipToShowPageActivity();
                                            }
                                        }
                                    });
                        } else {
                            if (!TextUtils.isEmpty(SharedPerferencesUtil.getStringData(SplashActivity.this, PreferenceKeyUtils.PASSWORD, ""))) {
                                skipToPasswordActivity();
                            } else {
                                skipToShowPageActivity();
                            }
                        }
                    }
                }, 4000);
            }
        }).start();

        //获取参数配置
        AppConfig.getParams(this);
    }

    /**
     * 设置开屏页面
     */
    private void setViews() {
        String splashInfo = SharedPerferencesUtil.getSplashInfo(this);
        try {
            JSONObject obj = new JSONObject(splashInfo);
            adInfo = new AdInfo();
            adInfo.setUrl(obj.optString("url"));
            adInfo.setName(obj.optString("name"));
            adInfo.setPic(obj.optString("filepath"));
            final int order = obj.optInt("order");
            File file = new File(adInfo.getPic());
            if (file.exists() && file.length() > 1000) { // 判断是否获取广告图成功
                Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0) {
                    imgIcon.setImageBitmap(bmp);
                    SharedPerferencesUtil.saveSplashOrder(SplashActivity.this, order);
                    imgIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isClick = true;
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
