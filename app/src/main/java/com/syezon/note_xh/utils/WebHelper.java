package com.syezon.note_xh.utils;

import android.content.Context;
import android.content.Intent;

import com.syezon.note_xh.activity.WebActivity;

/**
 *
 */

public class WebHelper {
    public static final String ARG_URL = "url";
    public static final String ARG_TITLE = "title";
    public static final String ARG_BACK_NEWS = "back_to_news";


    /**
     * @param callBack
     */
    public static void showAdDetail(Context context, String title, String url, WebLoadCallBack callBack) {
        WebActivity.setWebLoadCallBack(callBack);
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_URL, url);
        context.startActivity(intent);
    }

    /**
     *
     * @param callBack
     */
    public static void showNoteNews(Context context, String title, String url, WebLoadCallBack callBack) {
        WebActivity.setWebLoadCallBack(callBack);
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_URL, url);
        intent.putExtra(ARG_BACK_NEWS, true);
        context.startActivity(intent);
    }


    public interface WebLoadCallBack {
        void loadBefore(String url);

        void loadComplete(String url);

        void loadError(String url);
    }

    public static class SimpleWebLoadCallBack implements WebLoadCallBack {
        @Override
        public void loadBefore(String url) {

        }

        @Override
        public void loadComplete(String url) {

        }

        @Override
        public void loadError(String url) {

        }
    }
}
