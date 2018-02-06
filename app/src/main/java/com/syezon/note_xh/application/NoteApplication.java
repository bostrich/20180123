package com.syezon.note_xh.application;

import android.app.Application;
import android.text.TextUtils;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.syezon.note_xh.BuildConfig;
import com.syezon.note_xh.CrashHandler;
import com.syezon.note_xh.db.NoteEntity;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * Note Application.
 */
public class NoteApplication extends Application {
    public static DbManager dbManager;
    public static Long startTime;
    public static boolean isLeave = false;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
        DbManager.DaoConfig config = new DbManager.DaoConfig()
                 .setDbName("note_hd.db")
                 .setAllowTransaction(true)
                 .setDbVersion(4)
                 .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                     @Override
                     public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                         if(oldVersion!=newVersion){
                             switch (oldVersion){
                                 case 2:
                                     try {
                                         db.addColumn(NoteEntity.class,"iscomplete");
                                         db.addColumn(NoteEntity.class, "version");
                                     } catch (DbException e) {
                                         e.printStackTrace();
                                     }
                                     break;
                                 case 3:
                                     try {
                                         db.addColumn(NoteEntity.class, "version");
                                     } catch (DbException e) {
                                         e.printStackTrace();
                                     }
                                     break;
                             }
                         }
                     }
                 });
        dbManager = x.getDb(config);

//        阿里百川反馈初始化
        FeedbackAPI.init(this, "24632337","03fe03e94a34275b3339fb900c4fa9e4");

        String channel = AnalyticsConfig.getChannel(this);
        if (!TextUtils.isEmpty(channel)) {
            MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this,"515abb3056240b1ec1009b3b",channel,MobclickAgent.EScenarioType. E_UM_NORMAL,true));
        }

        /** 友盟调试 */
        if (BuildConfig.DEBUG) {
            MobclickAgent.setDebugMode(true);
        }else {
            MobclickAgent.setDebugMode(false);
        }

        //禁止默认的页面统计方式
        MobclickAgent.openActivityDurationTrack(false);

        /** 设置是否对日志信息进行加密,默认false(不加密). */
        MobclickAgent.enableEncrypt(true);
        //添加错误日志
        CrashHandler.getInstance().init(this);

    }
}
