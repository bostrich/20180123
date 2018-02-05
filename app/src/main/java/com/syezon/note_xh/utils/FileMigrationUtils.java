package com.syezon.note_xh.utils;

import com.syezon.note_xh.constants.Constant;
import com.syezon.note_xh.bean.FileInfo;

import java.util.Map;

/**
 * Created by admin on 2017/9/30.
 */

public class FileMigrationUtils {
    /**
     * 添加一个FileInfo
     */
    public static void addFileInfo(FileInfo fileInfo){
        if(!Constant.mFileInfoMap.containsKey(fileInfo.getFilePath())){
            Constant.mFileInfoMap.put(fileInfo.getFilePath(), fileInfo);
        }
    }

    /**
     * 更新FileInfo
     */
    public static void updateFileInfo(FileInfo fileInfo){
        Constant.mFileInfoMap.put(fileInfo.getFilePath(), fileInfo);
    }

    /**
     * 删除一个FileInfo
     */
    public static void delFileInfo(FileInfo fileInfo){
        if(Constant.mFileInfoMap.containsKey(fileInfo.getFilePath())){
            Constant.mFileInfoMap.remove(fileInfo.getFilePath());
        }
    }

    /**
     * 是否存在FileInfo
     */
    public static boolean isExist(FileInfo fileInfo){
        if(Constant.mFileInfoMap == null) return false;
        return Constant.mFileInfoMap.containsKey(fileInfo.getFilePath());
    }

    /**
     * 判断文件集合是否有元素
     */
    public static boolean isFileInfoMapExist(){
        if(Constant.mFileInfoMap == null || Constant.mFileInfoMap.size() <= 0){
            return false;
        }
        return true;
    }

    /**
     * 获取全局变量中的FileInfoMap
     */
    public static Map<String, FileInfo> getFileInfoMap(){
        return Constant.mFileInfoMap;
    }

    /**
     * 获取即将发送文件列表的总长度
     */
    public static long getAllSendFileInfoSize(){
        long total = 0;
        for(FileInfo fileInfo : Constant.mFileInfoMap.values()){
            if(fileInfo != null){
                total = total + fileInfo.getSize();
            }
        }
        return total;
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////
    //发送方
    /**
     * 添加一个FileInfo
     */
    public static void addReceiverFileInfo(FileInfo fileInfo){
        if(!Constant.mReceiverFileInfoMap.containsKey(fileInfo.getFilePath())){
            Constant.mReceiverFileInfoMap.put(fileInfo.getFilePath(), fileInfo);
        }
    }

    /**
     * 更新FileInfo
     */
    public static void updateReceiverFileInfo(FileInfo fileInfo){
        Constant.mReceiverFileInfoMap.put(fileInfo.getFilePath(), fileInfo);
    }

    /**
     * 删除一个FileInfo
     */
    public static void delReceiverFileInfo(FileInfo fileInfo){
        if(Constant.mReceiverFileInfoMap.containsKey(fileInfo.getFilePath())){
            Constant.mReceiverFileInfoMap.remove(fileInfo.getFilePath());
        }
    }

    /**
     * 是否存在FileInfo
     */
    public static boolean isReceiverInfoExist(FileInfo fileInfo){
        if(Constant.mReceiverFileInfoMap == null) return false;
        return Constant.mReceiverFileInfoMap.containsKey(fileInfo.getFilePath());
    }

    /**
     * 判断文件集合是否有元素
     */
    public static boolean isReceiverFileInfoMapExist(){
        if(Constant.mReceiverFileInfoMap == null || Constant.mReceiverFileInfoMap.size() <= 0){
            return false;
        }
        return true;
    }

    /**
     * 获取全局变量中的FileInfoMap
     */
    public static Map<String, FileInfo> getReceiverFileInfoMap(){
        return Constant.mReceiverFileInfoMap;
    }


    /**
     * 获取即将接收文件列表的总长度
     */
    public static long getAllReceiverFileInfoSize(){
        long total = 0;
        for(FileInfo fileInfo : Constant.mReceiverFileInfoMap.values()){
            if(fileInfo != null){
                total = total + fileInfo.getSize();
            }
        }
        return total;
    }
}
