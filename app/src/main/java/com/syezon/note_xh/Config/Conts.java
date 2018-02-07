package com.syezon.note_xh.Config;

import android.os.Environment;

/**
 *
 */

public class Conts {

   public static final String URL_BL_NEWS_NORMAL = "http://newscdn.wlanbanlv.com/webapi/external/lists?from=zengqiangqi&channelId=170&num=20&page=1";
   public static final String URL_BL_NEWS= "http://newscdn.wlanbanlv.com/webapi/external/lists?from=zengqiangqi&channelId=131&num=20&page=";

   public static final String FOLDER_COMPRESS = Environment.getExternalStorageDirectory() + "/note_pic/compress";
   public static final String FOLDER_PIC = Environment.getExternalStorageDirectory() + "/note_pic";
   public static final String FOLDER_DECOMPRESS = Environment.getExternalStorageDirectory() + "/note_pic/decompress";
   public static final String FOLDER_SPLASH = Environment.getExternalStorageDirectory() + "/note_pic/splash/";
   public static final String MIGRATION_ZIP_SEND = Environment.getExternalStorageDirectory() + "/note_pic/send.zip";
   public static final String MIGRATION_ZIP_RECEIVED = Environment.getExternalStorageDirectory() + "/note_pic/received.zip";

   public static final int TYPE_NEWS = 1;
   public static final int TYPE_URL = 2;
   public static final int TYPE_APK = 3;
   public static final int TYPE_NOTE = 4;

   public static final int NEWS_SOURCE_BL = 1;

   public static final int DEFAULT_SERVER_UDP_PORT =  4567;
   public static final String DEFAULT_HOTSPOT_NAME = "note_migration123";
}
