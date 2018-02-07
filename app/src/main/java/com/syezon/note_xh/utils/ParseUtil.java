package com.syezon.note_xh.utils;

import com.syezon.note_xh.adapter.NewsAdapter;
import com.syezon.note_xh.bean.BLNewsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class ParseUtil {

    public static List<BLNewsBean> parseNews(String result) throws JSONException {
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
            bean.setSource(temp.optString("source"));
            List<String> list = new ArrayList<>();
            JSONArray imgs = temp.optJSONArray("images");
            if(imgs != null && imgs.length() > 0) {
                for (int k = 0; k < imgs.length(); k++) {
                    String imgUrl = imgs.optString(k);
                    list.add(imgUrl);
                }
            }
            bean.setImages(list);
            if(bean.getImages().size() >= 3){
                bean.setShowType(NewsAdapter.NEWS_SHOW_THREE_PIC);
            }else{
                bean.setShowType(NewsAdapter.NEWS_TYPE_PIC_TEXT);
            }
            news.add(bean);
        }
        return news;
    }
}
