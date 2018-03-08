package com.syezon.note_xh.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片工具类
 */
public class PhotoUtils {
    public static final int REQUESTCODE_CAMAR = 1;
    public static final int REQUESTCODE_PHOTO = 2;

    /**
     * @param uri 从相机或图库获取的uri
     * @return 本地储存的uri
     */
    public static Uri convertUri(Context context,Uri uri) {
        InputStream is;
        try {
            is = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if (is != null) {
                is.close();
            }
            if (bitmap!=null) {
                return saveBitmap(context,bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return 将bitmap储存在SD卡，并返回uri
     */
    public static Uri saveBitmap(Context context,Bitmap bm) {
        File tmpDir = context.getFilesDir();
        if (tmpDir != null && !tmpDir.exists()) {
            tmpDir.mkdir();
        }
        File img = new File(tmpDir,System.currentTimeMillis()+".png");
        try {
            FileOutputStream fos = new FileOutputStream(img);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return 将bitmao转为drawable
     */
    public static Drawable getMyDrawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(bitmap);
        int imgHeight = drawable.getIntrinsicHeight();
        int imgWidth = drawable.getIntrinsicWidth();
        drawable.setBounds(0, 0, imgWidth, imgHeight);
        return drawable;
    }

    /**
     * @return 根据屏幕尺寸对图片进行相应的压缩，若超过屏幕宽度则压缩到屏幕宽度
     */
    public static Bitmap getScaledBitmap(ContentResolver contentResolver, Uri uri,int screeWidth,int screeHeight) {
        try {
            Bitmap bitmap ;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);

            options.inJustDecodeBounds = false;
            int imgWidth = options.outWidth;
            int imgHeight = options.outHeight;
            int scale = 1;
            if (imgWidth > imgHeight && imgWidth > screeWidth) {
                scale = (imgWidth / screeWidth);
            } else if (imgHeight > imgWidth && imgHeight > screeHeight) {
                scale = (imgHeight / screeHeight);
            }
            options.inSampleSize = scale;// 设置缩放比例
            bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);
            Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(bitmap, 624, 328);
//            Log.d("ssssss",""+bitmap);////////////////
            return bitmap1;
        } catch (Exception e) {
            e.printStackTrace();
//            Log.d("ssssss","22"+e.getMessage());////////////////
            return null;
        }
    }


    /**
     * @return 获取bitmap的大小
     */
    public static long getBitmapSize(Bitmap bitmap){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
