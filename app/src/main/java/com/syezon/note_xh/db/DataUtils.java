package com.syezon.note_xh.db;

import com.syezon.note_xh.application.NoteApplication;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */

public class DataUtils {

    public static List<DbModel> getDataBySortName(String sortName){
        List<DbModel> dbModelList = new ArrayList<>();
        try {
            switch (sortName){
                case "收藏":
                    List<DbModel> newDbModelList = NoteApplication.dbManager.findDbModelAll(new SqlInfo("select _id,time,briefcontent,title,hasimage,imagepath,weather,iscomplete,iscollect from note where iscollect = 1"));
                    Collections.sort(newDbModelList, new Comparator<DbModel>() {
                        @Override
                        public int compare(DbModel o1, DbModel o2) {
                            return o1.getString("time").compareTo(o2.getString("time"));
                        }
                    });
                    dbModelList.clear();
                    dbModelList.addAll(newDbModelList);
                    break;
                case "未分类":
                    List<DbModel> newDbModelList1 = NoteApplication.dbManager.findDbModelAll(new SqlInfo("select _id,time,briefcontent,title,hasimage,imagepath,weather,iscomplete,iscollect from note where sortname = ''"));
                    Collections.sort(newDbModelList1, new Comparator<DbModel>() {
                        @Override
                        public int compare(DbModel o1, DbModel o2) {
                            return o1.getString("time").compareTo(o2.getString("time"));
                        }
                    });
                    dbModelList.clear();
                    dbModelList.addAll(newDbModelList1);
                    break;
                default:
                    List<DbModel> newDbModelList2 = NoteApplication.dbManager.findDbModelAll(new SqlInfo("select _id,time,briefcontent,title,hasimage,imagepath,weather,iscomplete,iscollect from note where sortname ='"+sortName+"'"));
                    Collections.sort(newDbModelList2, new Comparator<DbModel>() {
                        @Override
                        public int compare(DbModel o1, DbModel o2) {
                            return o1.getString("time").compareTo(o2.getString("time"));
                        }
                    });
                    dbModelList.clear();
                    dbModelList.addAll(newDbModelList2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbModelList;
    }
}
