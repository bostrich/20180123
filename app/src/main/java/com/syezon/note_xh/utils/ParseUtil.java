package com.syezon.note_xh.utils;

import com.syezon.note_xh.bean.BLNewsBean;
import com.syezon.note_xh.bean.NewsNoteInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class ParseUtil {

    public static NewsNoteInfo parseNews(String result) throws JSONException {
        NewsNoteInfo info = new NewsNoteInfo();
        List<BLNewsBean> news = new ArrayList<>();
        JSONObject obj = new JSONObject(result);
        JSONArray ary = obj.optJSONArray("data");
        for (int i = 0; i < ary.length(); i++) {
            JSONObject temp = ary.optJSONObject(i);
            BLNewsBean bean = new BLNewsBean();
            bean.setDate(temp.optString("date"));
            bean.setUrl(temp.optString("url"));
            bean.setDescription(temp.optString("description"));
            bean.setTitle(temp.optString("title"));
            List<String> list = new ArrayList<>();
            JSONArray imgs = temp.optJSONArray("images");
            if(imgs != null && imgs.length() > 0) {
                for (int k = 0; k < imgs.length(); k++) {
                    String imgUrl = imgs.optString(k);
                    list.add(imgUrl);
                }
            }
            bean.setImages(list);
            news.add(bean);
        }
        info.setNews(news);
        return info;
    }
}
