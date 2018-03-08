package com.syezon.note_xh.bean;

import android.content.Context;

import com.syezon.note_xh.Config.AdConfig;
import com.syezon.note_xh.download.DownloadBean;
import com.syezon.note_xh.download.DownloadManager;
import com.syezon.note_xh.download.feedback.NotificationDownloadFeedback;
import com.syezon.note_xh.event.EditEvent;
import com.syezon.note_xh.utils.DialogUtils;
import com.syezon.note_xh.utils.LogUtil;
import com.syezon.note_xh.utils.SharedPerferencesUtil;
import com.syezon.note_xh.utils.StatisticUtils;
import com.syezon.note_xh.utils.UmengReportUtil;
import com.syezon.note_xh.utils.WebHelper;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.syezon.note_xh.download.DownloadManager.DOWNLOAD_STRATERY_SERVICE;

/**
 * Created by June on 2018/3/1.
 */

public class NoteAdsBean extends BaseNoteBean {

    private static final String TAG = NoteAdsBean.class.getName();
    private List<NoteAdInfo> infos = new ArrayList<>();//广告集合
    private int position;//广告位置
    private NoteAdInfo info;

    public NoteAdsBean(){}

    public NoteAdsBean(List<NoteAdInfo> infos){
        this.infos.clear();
        this.infos.addAll(infos);
        if(infos.size() > 0) info = this.infos.get(position++ % this.infos.size());
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
        return info.getPic();
    }

    @Override
    public String getTitle() {
        return info.getName();
    }

    @Override
    public String getDesc() {
        return info.getName();
    }

    @Override
    public boolean hasImage() {
        return !info.getPic().equals("");
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
        final NoteAdInfo info = infos.get((position -1) % infos.size());
        StatisticUtils.report(context, StatisticUtils.ID_NOTE_AD, StatisticUtils.EVENT_CLICK, info.getId());
        if(info.getType().equals(AdConfig.TYPE_URL.getName())){
            WebHelper.showAdDetail(context, getTitle(), info.getUrl(), new WebHelper.SimpleWebLoadCallBack(){
                @Override
                public void loadComplete(String url) {

                }
            });
        }else if(info.getType().equals(AdConfig.TYPE_APK.getName())){
            DialogUtils.showDownloadHint(context, info, new DialogUtils.DialogListener<NoteAdInfo>() {
                @Override
                public void confirm(NoteAdInfo bean) {
                    DownloadBean downloadBean = new DownloadBean();
                    downloadBean.setAppName(bean.getName());
                    bean.setUrl(info.getUrl());
                    DownloadManager.getInstance(context).download(downloadBean, DOWNLOAD_STRATERY_SERVICE
                            , new NotificationDownloadFeedback(context));
                }
                @Override
                public void cancel() {

                }
            });
        }
        //广告点击后当天不显示
        String showInfo = SharedPerferencesUtil.getNoteInfo(context);
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);
        String ids = info.getId();
        if(!showInfo.equals("")){
            try {
                JSONObject obj = new JSONObject(showInfo);
                int day = obj.optInt("day");
                ids = obj.optString("ids");
                if(today == day){
                    ids += (";" + info.getId());
                }else{
                    ids = info.getId();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject save = new JSONObject();
        try {
            save.put("day", today);
            save.put("ids", ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "day:" + today + "----ids:" + ids);
        SharedPerferencesUtil.saveNoateInfo(context, save.toString());
    }

    public static String getShowIds(Context context){
        String showInfo = SharedPerferencesUtil.getNoteInfo(context);
        try {
            JSONObject obj = new JSONObject(showInfo);
            int day = obj.optInt("day");
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_YEAR);
            if(today != day){
                return null;
            }else{
                String ids = obj.optString("ids");
                return ids;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void show(Context context) {
        if(!info.isReportShow()){
            StatisticUtils.report(context, StatisticUtils.ID_NOTE_AD, StatisticUtils.EVENT_IMAGE_SHOW, info.getId());
            info.setReportShow(true);
        }
        info = infos.get(position++ % infos.size());
    }
}
