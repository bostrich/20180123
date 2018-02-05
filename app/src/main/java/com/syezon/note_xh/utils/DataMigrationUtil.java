package com.syezon.note_xh.utils;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.syezon.note_xh.Config.Conts;
import com.syezon.note_xh.application.NoteApplication;
import com.syezon.note_xh.db.NoteEntity;
import com.syezon.note_xh.db.NoteSortEntity;
import com.syezon.note_xh.event.EditEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class DataMigrationUtil {

    public static final String CATEGORY = "category";
    public static final String CATEGORYS = "categorys";
    public static final String NOTES = "notes";
    public static final String SORTNAME = "sortname";
    public static final String TIME = "time";
    public static final String BRIEFCONTENT = "briefcontent";
    public static final String CONTENT = "content";
    public static final String TITLE = "title";
    public static final String HASIMAGE = "hasimage";
    public static final String IMAGEPATH = "imagepath";
    public static final String WEATHER = "weather";
    public static final String ISCOMPLETE = "iscomplete";
    public static final String ISCOLLECT = "iscollect";
    public static final String VERSION = "version";

    public static final String BRIEFNOTE= "briefnote/notes";
    private static final String TAG = DataMigrationUtil.class.getName();

    private static String content = "";
    private static String imgPath = "";
    private static boolean changImgPath = false;


    public static boolean migrationData(){
        try {
            JSONObject object = new JSONObject();
            //获取并添加分类
            List<String> category = getNoteCategory();
            if(category.size() > 0){
                JSONArray jay_category = new JSONArray();
                for (String name : category) {
                    JSONObject obj_category = new JSONObject();
                    obj_category.put(CATEGORY, name);
                    jay_category.put(obj_category);
                }
                object.put(CATEGORYS, jay_category);
            }

            //获取便签数据并保存到json中去
            List<DbModel> newDbModelList1 = NoteApplication.dbManager.findDbModelAll(new SqlInfo
                    ("select _id,sortname,time,briefcontent,content,title,hasimage,imagepath,weather,iscomplete,iscollect,version from note"));
            //TODO 判断内存空间是否充足

            //判断文件夹是否存在并删除多余文件
            final String file_path = Conts.FOLDER_COMPRESS;
            File files = new File(file_path);
            if(files.exists()){
                if(files.isDirectory()) deleteDirWihtFile(files);
            }else{
                files.mkdirs();
            }
            if(newDbModelList1.size() > 0) {
                JSONArray array = new JSONArray();
                for (DbModel model : newDbModelList1) {
                    JSONObject obj_note = new JSONObject();
                    obj_note.put(SORTNAME, model.getString(SORTNAME));
                    obj_note.put(TIME, model.getString(TIME));
                    obj_note.put(BRIEFCONTENT, model.getString(BRIEFCONTENT));
                    obj_note.put(TITLE, model.getString(TITLE));
                    obj_note.put(HASIMAGE, model.getBoolean(HASIMAGE));
                    content = model.getString(CONTENT);
                    imgPath = model.getString(IMAGEPATH);
                    changImgPath = false;
                    //获取图片并将图片copy到文件中
                    if (!TextUtils.isEmpty(model.getString(IMAGEPATH))) {
                        Html.fromHtml(model.getString(CONTENT), new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                String tag = source.substring(0, 1);
                                String url = source.substring(2);
                                if (tag.equals("1")) {
                                    String path = file_path + "/" + System.currentTimeMillis() + ".jpg";
                                    String target_url = url;
                                    if (url.startsWith("file://")) {
                                        target_url = url.replace("file://", "");
                                    }
                                    try {
                                        fileCopy(target_url, path);
                                        boolean contains = content.contains(url);
                                        content = content.replace(url, "file://" + path.replace(file_path, Conts.FOLDER_PIC));
                                        if(!changImgPath){
                                            imgPath = imgPath.replace(url, "file://" + path.replace(file_path, Conts.FOLDER_PIC));
                                            changImgPath = true;
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return null;
                            }
                        }, null);
                    }
                    obj_note.put(CONTENT, content);
                    obj_note.put(IMAGEPATH, imgPath);
                    obj_note.put(WEATHER, model.getString(WEATHER));
                    obj_note.put(ISCOMPLETE, model.getBoolean(ISCOMPLETE));
                    obj_note.put(ISCOLLECT, model.getBoolean(ISCOLLECT));
                    obj_note.put(VERSION, model.getInt(VERSION));
                    array.put(obj_note);
                }
                object.put(NOTES, array);
                //将json数据保存到文件中
                File file = new File(file_path + "/briefnote.txt");
                FileOutputStream outputStream = new FileOutputStream(file);
                BufferedWriter br = new BufferedWriter(new OutputStreamWriter(outputStream));
                br.write(object.toString());
                br.close();
                outputStream.close();
                return true;
            }
        } catch (DbException e) {
            e.printStackTrace();
        } catch( JSONException e){
            e.printStackTrace();
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }


    public static void dataMerge(String path) throws Exception{
        File file = new File(path + "/notes/briefnote.txt");
        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String result = br.readLine();
        JSONObject object = new JSONObject(result);
        JSONArray ary = object.optJSONArray(CATEGORYS);
        List<String> categorys = getNoteCategory();
        if (ary != null && ary.length() > 0) {
            for (int i = 0; i < ary.length(); i++) {
                JSONObject category = ary.optJSONObject(i);
                String title = category.optString(CATEGORY);
                if (!categorys.contains(title)) {
                    //将分类名称存入数据库
                    NoteSortEntity noteSortEntity = new NoteSortEntity(title);
                    char firstChar = title.charAt(0);
                    String firstLetter = StringUtils.converterToFirstSpell(String.valueOf(firstChar));
                    noteSortEntity.setFirstLetterAsc(StringUtils.getAsc(firstLetter));
                    NoteApplication.dbManager.saveBindingId(noteSortEntity);
                }
            }
        }
        LogUtil.e(TAG, "解析添加完成分类");

        JSONArray ary_notes = object.optJSONArray(NOTES);
        //获取本地数据信息看是否有重复的信息并过滤
        List<DbModel> oldDbNotes = new ArrayList<>();
        try {
            oldDbNotes.addAll(NoteApplication.dbManager.findDbModelAll(new SqlInfo
                    ("select _id,sortname,time,briefcontent,content,title,hasimage,imagepath,weather" +
                            ",iscomplete,iscollect,version from note")));
        }catch(Exception e){
            e.printStackTrace();
        }

        if (ary_notes != null && ary_notes.length() > 0) {
            for (int i = 0; i < ary_notes.length(); i++) {
                JSONObject obj = ary_notes.optJSONObject(i);
                NoteEntity noteEntity = new NoteEntity();
                noteEntity.setSortName(obj.optString(SORTNAME));
                noteEntity.setTime(obj.optString(TIME));
                noteEntity.setBriefContent(obj.optString(BRIEFCONTENT));
                noteEntity.setContent(obj.optString(CONTENT));
                noteEntity.setTitle(obj.optString(TITLE));
                noteEntity.setHasImage(obj.optBoolean(HASIMAGE));
                noteEntity.setImagePath(obj.optString(IMAGEPATH));
                noteEntity.setWeatherStr(obj.optString(WEATHER));
                noteEntity.setCollect(obj.optBoolean(ISCOLLECT));
                noteEntity.setComplete(obj.optBoolean(ISCOLLECT));
                noteEntity.setVersion(obj.optInt(VERSION));
                //判断是否有重复数据
                boolean isAdded = false;
                for (DbModel dbModel: oldDbNotes) {
                    if (dbModel.getString(TIME).equals(noteEntity.getTime())) {
                        if (dbModel.getInt(VERSION) < noteEntity.getVersion()) {
                            try {
                                NoteApplication.dbManager.update(noteEntity, "weather", "sortname", "title", "content", "hasimage"
                                        , "iscollect", "imagepath", "briefcontent", "version");
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                        isAdded = true;
                        break;
                    }
                }
                if(!isAdded){
                    try {
                        NoteApplication.dbManager.saveBindingId(noteEntity);
                    } catch (DbException e) {
                        e.printStackTrace();
                        LogUtil.e(TAG, "解析添加笔记失败" + noteEntity.getTitle());
                    }
                }

            }
        }
        LogUtil.e(TAG, "解析添加完成笔记");

    }

    public static List<String> getNoteCategory() throws DbException {
        List<String> category = new ArrayList<>();
        List<NoteSortEntity> sortEntityList = NoteApplication.dbManager.findAll(NoteSortEntity.class);
        for (NoteSortEntity entity : sortEntityList) {
            if (!TextUtils.isEmpty(entity.getSortName()))
                category.add(entity.getSortName());
        }
        return category;
    }

    public static boolean fileCopy(String oldFilePath,String newFilePath) throws IOException {
        //如果原文件不存在
        if(fileExists(oldFilePath) == false){
            return false;
        }
        //获得原文件流
        FileInputStream inputStream = new FileInputStream(new File(oldFilePath));
        byte[] data = new byte[1024];
        File file = new File(newFilePath);
        //输出流
        FileOutputStream outputStream =new FileOutputStream(newFilePath);
        //开始处理流
        while (inputStream.read(data) != -1) {
            outputStream.write(data);
        }
        inputStream.close();
        outputStream.close();
        return true;
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
    }
}
