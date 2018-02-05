package com.syezon.note_xh.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.syezon.note_xh.R;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.utils.PreferenceKeyUtils;
import com.syezon.note_xh.utils.SharedPerferencesUtil;
import com.syezon.note_xh.utils.SystemBarTintManager;

import java.util.List;


/**
 * Activity基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Context mContext;
    protected SystemBarTintManager mTintManager;

    public int themeColorSourceId;//主题颜色
    public int customactionbarBackGroundSorceId;//与主题对应的相应的自定义actionBar的颜色
    public int collectWenjianjiaBackGroundSorceId;//收藏文件夹背景资源ID
    public int wenjianjiaBackGroundSorceId;//文件夹背景资源ID
    public int addBackGroundSourceId;//添加按钮背景资源ID
    public int editcompleteBackGroundSorceId;//编辑完成按钮背景资源ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setTheme(SharedPerferencesUtil.getIntData(this, PreferenceKeyUtils.THEME, R.style.GreenStyle));
        TypedArray a = obtainStyledAttributes(R.styleable.ResourceStyle);
        themeColorSourceId = a.getResourceId(R.styleable.ResourceStyle_theme_color, R.color.green);
        customactionbarBackGroundSorceId = a.getResourceId(R.styleable.ResourceStyle_custom_actionbar_color, R.color.top_green);
        collectWenjianjiaBackGroundSorceId = a.getResourceId(R.styleable.ResourceStyle_wenjianjia_collect_background, R.mipmap.collect_wenjianjia_green);
        wenjianjiaBackGroundSorceId = a.getResourceId(R.styleable.ResourceStyle_wenjianjia_background, R.mipmap.wenjianjia_green);
        addBackGroundSourceId = a.getResourceId(R.styleable.ResourceStyle_add_background, R.mipmap.add_green);
        editcompleteBackGroundSorceId = a.getResourceId(R.styleable.ResourceStyle_edit_complete_background, R.mipmap.editcomplete_green);
        a.recycle();

        //沉浸式状态栏相关
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setStatusBarTintResource(customactionbarBackGroundSorceId);
        }

    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    protected void setStatusBarResourceColor(int resColor){
        if (mTintManager!=null) {
            mTintManager.setStatusBarTintResource(resColor);
        }
    }

    protected void setStatusBarColor(int color){
        if (mTintManager!=null) {
            mTintManager.setStatusBarTintColor(color);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground()) {
            NoteApplication.isLeave = true;
            NoteApplication.startTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onResume() {
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
        //程序在后台超过两分钟则程序上锁
        if (NoteApplication.isLeave) {
            NoteApplication.isLeave = false;
            long endtime = System.currentTimeMillis();
            if (endtime - NoteApplication.startTime >= 120 * 1000) {
                Intent intent = new Intent(this, PasswordActivity.class);
                intent.putExtra("islock", true);
                startActivity(intent);
            }
        } else {
            NoteApplication.isLeave = false;
        }
    }

    /**
     * @return true if App is OnForeground
     */
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the device
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}