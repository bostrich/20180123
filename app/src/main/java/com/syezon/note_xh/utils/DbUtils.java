package com.syezon.note_xh.utils;

import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.db.NoteEntity;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by admin on 2016/7/16.
 */
public class DbUtils {

    //获取某种类的篇幅数
    public static int getCategoryNumber(String name){
        try {
            List<NoteEntity> list= NoteApplication.dbManager.selector(NoteEntity.class)
                    .where("sortname","=",name).findAll();
            if(list!=null){
                return list.size();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //获取收藏的篇幅数
    public static int getCollectedNumber(){
        try {
            List<NoteEntity> list= NoteApplication.dbManager.selector(NoteEntity.class)
                    .where("iscollect","=",true).findAll();
            if(list!=null){
                return list.size();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
