package com.syezon.note_xh.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by admin on 2016/8/3.
 */
public class FileUtils {

    /**
     * @return 外置储存是否可用
     */
    public static boolean isSDCardEnable(){
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 把文件拷贝到某一目录下
     * @param srcFile
     * @param destDir
     * @return
     */
    public static boolean copyFileToDir(String srcFile, String destDir, boolean delete){
        File fileDir = new File(destDir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        String destFile = destDir +"/" + new File(srcFile).getName();
        //避免重复赋值
        if(new File(destFile).exists()) return true;
        try{
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[]=new byte[1024];
            int len;
            while ((len= streamFrom.read(buffer)) > 0){
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            if(delete){
                deleteFile(new File(srcFile));
            }
            return true;
        } catch(Exception ex){
            return false;
        }
    }


    /**
     * 删除文件（包括目录）
     * @param delFile
     */
    public static void deleteFile(File delFile) {
        //如果是目录递归删除
        if (delFile.isDirectory()) {
            File[] files = delFile.listFiles();
            for (File file : files) {
                deleteFile(file);
            }
        }else{
            delFile.delete();
        }
    }

}
