package com.syezon.note_xh.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.syezon.note_xh.BuildConfig;

/**
 * 优化频繁toast.
 */
public class ToastUtils {
    private static Toast toast;
    private static Handler handler=new Handler(Looper.getMainLooper());
    private static String uniqueText="";
    private static String debugText="";

    public static void showUniqueToast(final Context context, String text){//当前toast会覆盖上一个toast
        if(!TextUtils.equals(uniqueText,text)){
            uniqueText=text;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(toast==null){
                    toast=Toast.makeText(context.getApplicationContext(), uniqueText, Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    toast.setText(uniqueText);
                    toast.show();
                }
            }
        });
    }

    public static void showDebugToast(final Context context, final String text){
        if(!BuildConfig.DEBUG){
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showUniqueDebugToast(final Context context, String text){
        if(!BuildConfig.DEBUG){
            return;
        }
        if(!TextUtils.equals(debugText,text)){
            debugText=text;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(toast==null){
                    toast=Toast.makeText(context.getApplicationContext(), debugText, Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    toast.setText(debugText);
                    toast.show();
                }
            }
        });
    }

    public static void showNewDebugToast(final Context context, final String text){
        if(!BuildConfig.DEBUG){
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showNewToast(final Context context, final String text){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
