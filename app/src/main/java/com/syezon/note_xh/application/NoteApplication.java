package com.syezon.note_xh.application;

import android.app.Application;
import android.text.TextUtils;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.syezon.note_xh.BuildConfig;
import com.syezon.note_xh.db.NoteEntity;
import com.syezon.note_xh.utils.LogUtil;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.Calendar;
import java.util.List;

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
                             //针对1.0版本升级到2.0版本时时间没有精确到毫秒数据多次迁移BUG问题
                             if(oldVersion <= 3){
                                 try {
                                     List<NoteEntity> all = db.findAll(NoteEntity.class);
                                     for (NoteEntity entity :all) {
                                         //从老版本升级时间戳修改
                                         if(entity.getVersion() ==0 && entity.getTime().endsWith("000")) {
                                             String stf = entity.getTime();
                                             LogUtil.e("数据库升级：", "老版本时间：" + stf);

                                             String currentTime = String.valueOf(System.currentTimeMillis());
                                             stf = stf.substring(0, stf.length() - 3) + currentTime.substring(currentTime.length() - 3, currentTime.length());
                                             LogUtil.e("数据库升级：", "升级后时间：" + stf);
                                             entity.setTime(stf);
                                             db.update(entity, "time");
                                             LogUtil.e("数据库升级：", "更新时间成功");
                                         }
                                     }
                                 } catch (Exception e) {
                                     e.printStackTrace();
                                     LogUtil.e("数据库升级：", "error:" + e.getMessage());
                                 }
                             }
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

    }
}
